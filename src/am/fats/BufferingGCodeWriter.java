/*  XPlotterSVG - Convert SVG to GCode
    Copyright (C) 2017-2026  Samuel Pickard
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package am.fats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures GCode lines into an in-memory buffer instead of writing them
 * straight through, while observing the X/Y extents of every motion command
 * (G0/G1/G2/G3) that passes through. After the SVG has been fully parsed,
 * call {@link #getMinX()}, {@link #getMaxX()}, {@link #getMinY()}, and
 * {@link #getMaxY()} to obtain the tight bounding box of the actual cut, then
 * {@link #flushTo(FileLineWriter)} to emit the buffered body to the real
 * output.
 *
 * <p>If no motion commands were observed, {@link #hasBounds()} returns false.
 *
 * <p>Arc commands (G2/G3) are bounded by their true extents, including any
 * bulge beyond the endpoints, so a circle is framed by its full diameter.
 *
 * <p>The parser brackets each shape's output with {@link #beginShape()} and
 * {@link #endShape()}. {@link #reorderInsideOut()} then moves every shape
 * that sits inside another shape's bounding box ahead of it, so holes and
 * slots are cut before the outline that would free the piece. Without this a
 * part can drop or twist on the bed while its inner features are still to be
 * cut.
 */
public class BufferingGCodeWriter extends FileLineWriter {

    /** A run of lines belonging to one shape, with its own bounding box. */
    private static class Block {
        final List<String> lines = new ArrayList<>();
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        boolean haveBounds = false;

        void include(double x, double y) {
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            haveBounds = true;
        }

        /** True if this block's box lies inside other's and is strictly smaller. */
        boolean isInside(Block other) {
            final double eps = 1e-3;
            if (!haveBounds || !other.haveBounds) {
                return false;
            }
            boolean within = minX >= other.minX - eps && maxX <= other.maxX + eps
                          && minY >= other.minY - eps && maxY <= other.maxY + eps;
            boolean same = Math.abs(minX - other.minX) <= eps && Math.abs(maxX - other.maxX) <= eps
                        && Math.abs(minY - other.minY) <= eps && Math.abs(maxY - other.maxY) <= eps;
            return within && !same;
        }
    }

    // Lines before the first shape, the shapes themselves, and lines after
    // endBody(). Only the shapes are ever reordered.
    private final List<String> preamble = new ArrayList<>();
    private List<Block> blocks = new ArrayList<>();
    private final List<String> trailer = new ArrayList<>();
    private Block current = null;
    private boolean inTrailer = false;

    // Last known head position, needed to resolve relative arc centres.
    private double lastX = 0;
    private double lastY = 0;

    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;
    private boolean haveBounds = false;

    /**
     * Constructs a buffering writer. A throwaway temp file is opened to keep
     * the {@link FileLineWriter}/{@link java.io.FileWriter} superclass happy;
     * nothing is ever written to it because all of our overrides intercept
     * before the superclass writes. Use {@link #flushTo(FileLineWriter)} to
     * send the buffered data to the real output writer.
     */
    public BufferingGCodeWriter() throws IOException {
        super(createTempPath());
    }

    private static String createTempPath() throws IOException {
        Path p = Files.createTempFile("jxplottersvg-buffer-", ".tmp");
        p.toFile().deleteOnExit();
        return p.toString();
    }

    @Override
    public void writeLine(String line) {
        // The String passed in may itself contain multiple lines because some
        // GCodeCommand.toString() implementations (e.g. GCodeMove) emit a
        // multi-line block ending in a newline. Split so each motion line is
        // observed individually.
        if (line == null) {
            return;
        }
        for (String part : line.split("\\R", -1)) {
            if (part.isEmpty()) {
                continue;
            }
            target().add(part);
            observe(part);
        }
    }

    @Override
    public void writeLine(GCodeCommand command) {
        writeLine(command.toString());
    }

    private void observe(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        // Trim leading whitespace and pull off the command word.
        String s = line.trim();
        if (s.length() < 2) {
            return;
        }
        char c0 = s.charAt(0);
        if (c0 != 'G' && c0 != 'g') {
            return;
        }
        // Accept G0, G1, G2, G3 (and G00/G01/G02/G03)
        // The character after G must be a digit; we then ensure the numeric
        // value is one of {0,1,2,3}.
        int i = 1;
        int code = 0;
        boolean sawDigit = false;
        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            code = code * 10 + (s.charAt(i) - '0');
            i++;
            sawDigit = true;
        }
        if (!sawDigit || code > 3) {
            return;
        }

        Double x = extractCoord(s, 'X');
        Double y = extractCoord(s, 'Y');
        double endX = (x != null) ? x : lastX;
        double endY = (y != null) ? y : lastY;

        if (code == 2 || code == 3) {
            Double ci = extractCoord(s, 'I');
            Double cj = extractCoord(s, 'J');
            if (ci != null || cj != null) {
                includeArc(lastX, lastY, endX, endY,
                           lastX + (ci != null ? ci : 0), lastY + (cj != null ? cj : 0),
                           code == 2);
            }
        }
        if (x != null || y != null) {
            include(endX, endY);
        }
        lastX = endX;
        lastY = endY;
    }

    private void include(double x, double y) {
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
        haveBounds = true;
        if (current != null) {
            current.include(x, y);
        }
    }

    /**
     * Includes the extremes of an arc from (sx,sy) to (ex,ey) about (cx,cy).
     * Coincident endpoints mean a full circle, as GRBL interprets them.
     */
    private void includeArc(double sx, double sy, double ex, double ey,
                            double cx, double cy, boolean clockwise) {
        double r = Math.hypot(sx - cx, sy - cy);
        double a0 = Math.atan2(sy - cy, sx - cx);
        double a1 = Math.atan2(ey - cy, ex - cx);
        // Sweep measured counter-clockwise from a0 to a1, in (0, 2pi].
        double ccwSweep = a1 - a0;
        while (ccwSweep <= 1e-9) ccwSweep += 2 * Math.PI;
        if (Math.hypot(ex - sx, ey - sy) < 1e-6) {
            ccwSweep = 2 * Math.PI;
        }
        // For a clockwise arc, walk counter-clockwise from the end instead.
        double from = clockwise ? a1 : a0;
        double sweep = clockwise ? (2 * Math.PI - ccwSweep) : ccwSweep;
        if (clockwise && Math.hypot(ex - sx, ey - sy) < 1e-6) {
            sweep = 2 * Math.PI;
        }
        for (int k = 0; k < 4; k++) {
            double axis = k * Math.PI / 2;
            double d = axis - from;
            while (d < 0) d += 2 * Math.PI;
            while (d >= 2 * Math.PI) d -= 2 * Math.PI;
            if (d <= sweep + 1e-9) {
                include(cx + r * Math.cos(axis), cy + r * Math.sin(axis));
            }
        }
    }

    /**
     * Extracts the numeric value following a coordinate letter (X or Y) on a
     * GCode line. Returns null if the letter is not present or the value
     * cannot be parsed.
     */
    private static Double extractCoord(String s, char letter) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == letter || ch == Character.toLowerCase(letter)) {
                // Make sure this isn't a letter inside a word (defensive).
                int start = i + 1;
                int end = start;
                while (end < s.length()) {
                    char c = s.charAt(end);
                    if (Character.isDigit(c) || c == '.' || c == '-' || c == '+') {
                        end++;
                    } else {
                        break;
                    }
                }
                if (end == start) {
                    return null;
                }
                try {
                    return Double.parseDouble(s.substring(start, end));
                } catch (NumberFormatException nfe) {
                    return null;
                }
            }
        }
        return null;
    }

    public boolean hasBounds() { return haveBounds; }
    public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }

    /** Starts a new shape; lines from here to {@link #endShape()} move together. */
    public void beginShape() {
        current = new Block();
        blocks.add(current);
    }

    /** Ends the current shape. Any stray lines join the previous shape. */
    public void endShape() {
        current = null;
    }

    /** Marks the end of the job body; later lines always come last. */
    public void endBody() {
        current = null;
        inTrailer = true;
    }

    private List<String> target() {
        if (inTrailer) {
            return trailer;
        }
        if (current != null) {
            return current.lines;
        }
        if (!blocks.isEmpty()) {
            return blocks.get(blocks.size() - 1).lines;
        }
        return preamble;
    }

    /**
     * Reorders shapes so that each one is cut only after every shape lying
     * inside its bounding box. Apart from that the original document order is
     * kept, which keeps travel moves close to what the drawing intended.
     *
     * <p>Bounding-box containment is a superset of real containment, so this
     * may delay a shape that didn't need delaying, but never cuts an outline
     * before something inside it. Shapes with identical boxes keep their
     * order.
     *
     * @return the number of shapes that moved
     */
    public int reorderInsideOut() {
        List<Block> ordered = new ArrayList<>(blocks.size());
        boolean[] emitted = new boolean[blocks.size()];
        for (int b = 0; b < blocks.size(); b++) {
            emitInsideOut(b, emitted, ordered);
        }
        int moved = 0;
        for (int b = 0; b < blocks.size(); b++) {
            if (ordered.get(b) != blocks.get(b)) {
                moved++;
            }
        }
        blocks = ordered;
        return moved;
    }

    private void emitInsideOut(int b, boolean[] emitted, List<Block> ordered) {
        if (emitted[b]) {
            return;
        }
        emitted[b] = true;
        Block outer = blocks.get(b);
        for (int c = 0; c < blocks.size(); c++) {
            if (!emitted[c] && blocks.get(c).isInside(outer)) {
                emitInsideOut(c, emitted, ordered);
            }
        }
        ordered.add(outer);
    }

    /**
     * Writes every buffered line to the supplied real output writer: the
     * preamble, then the shapes in their current order, then the trailer.
     */
    public void flushTo(FileLineWriter out) throws IOException {
        for (String line : preamble) {
            out.writeLine(line);
        }
        for (Block block : blocks) {
            for (String line : block.lines) {
                out.writeLine(line);
            }
        }
        for (String line : trailer) {
            out.writeLine(line);
        }
    }

    /**
     * Discards all buffered content. The bounds are not reset.
     */
    public void clear() {
        preamble.clear();
        blocks.clear();
        trailer.clear();
        current = null;
    }
}
