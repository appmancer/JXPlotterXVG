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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rewrites GCode on its way to the real output so that every line has a form
 * the Creality Falcon runs straight through from its card.
 *
 * <p>The Falcon's offline controller stops at lines it doesn't expect and
 * waits for the button before carrying on. Creality's own files only ever
 * contain motion lines, a handful of setup words and {@code ;} comments, so
 * this writer:
 * <ul>
 *   <li>drops blank lines,</li>
 *   <li>turns {@code ( comment )} into {@code ; comment},</li>
 *   <li>drops feed-only lines such as {@code G1 F450} (every motion line
 *       already carries its own feed), and</li>
 *   <li>folds power-only lines such as {@code S800} into the next motion
 *       line, as {@code G1 X.. Y.. S800 F450}.</li>
 * </ul>
 * Call {@link #finish()} once the job is written, in case a power change is
 * still waiting for a motion line.
 */
public class FalconGCodeWriter extends FileLineWriter {

    private static final Pattern COMMENT = Pattern.compile("^\\(\\s*(.*?)\\s*\\)$");
    private static final Pattern FEED_ONLY = Pattern.compile("^G0?1\\s*F[\\d.]+$");
    private static final Pattern POWER_ONLY = Pattern.compile("^S[\\d.]+$");
    private static final Pattern MOTION = Pattern.compile("^G0?[0-3](\\D.*)?$");
    private static final Pattern FEED_WORD = Pattern.compile("\\sF[\\d.]+");

    private final FileLineWriter out;
    private String pendingPower = null;

    /**
     * @param out the real output; lines are passed on as soon as they can be
     */
    public FalconGCodeWriter(FileLineWriter out) throws IOException {
        // FileLineWriter insists on a file. Nothing is ever written to it.
        super(scratchPath());
        this.out = out;
    }

    private static String scratchPath() throws IOException {
        Path p = Files.createTempFile("jxplottersvg-falcon-", ".tmp");
        p.toFile().deleteOnExit();
        return p.toString();
    }

    @Override
    public void writeLine(String line) throws IOException {
        if (line == null) {
            return;
        }
        // Some commands render as several lines at once
        for (String part : line.split("\\R")) {
            rewrite(part.trim());
        }
    }

    @Override
    public void writeLine(GCodeCommand command) throws IOException {
        writeLine(command.toString());
    }

    private void rewrite(String line) throws IOException {
        if (line.isEmpty() || FEED_ONLY.matcher(line).matches()) {
            return;
        }
        if (POWER_ONLY.matcher(line).matches()) {
            // A later power change before any motion supersedes this one
            pendingPower = line;
            return;
        }
        Matcher comment = COMMENT.matcher(line);
        if (comment.matches()) {
            out.writeLine("; " + comment.group(1));
            return;
        }
        if (pendingPower != null) {
            if (MOTION.matcher(line).matches()) {
                Matcher feed = FEED_WORD.matcher(line);
                line = feed.find()
                        ? line.substring(0, feed.start()) + " " + pendingPower + line.substring(feed.start())
                        : line + " " + pendingPower;
                pendingPower = null;
            } else {
                finish();
            }
        }
        out.writeLine(line);
    }

    /** Writes any power change still waiting for a motion line. */
    public void finish() throws IOException {
        if (pendingPower != null) {
            out.writeLine("G1 " + pendingPower);
            pendingPower = null;
        }
    }
}
