/*  XPlotterSVG - Convert SVG to GCode
    Copyright (C) 2017  Samuel Pickard
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

public class GCodeRect extends GCodePath
{
    protected double startX      = 0;
    protected double startY      = 0;
    protected double rectWidth   = 0;
    protected double rectHeight  = 0;
    protected double cornerRadius = 0;

    public GCodeRect(double x, double y, double rectWidth, double rectHeight)
    {
        startX = x;
        startY = y;
        this.rectHeight = rectHeight;
        this.rectWidth = rectWidth;
        this.cornerRadius = 0;
    }
    
    public GCodeRect(double x, double y, double rectWidth, double rectHeight, double radius)
    {
        startX = x;
        startY = y;
        this.rectHeight = rectHeight;
        this.rectWidth = rectWidth;
        this.cornerRadius = radius;
    }
    
    public void setCornerRadius(double radius) {
        this.cornerRadius = radius;
    }

    @Override
    public String toString()
    {
        StringBuilder gcode = new StringBuilder();
        gcode.append(toolUp());
        
        if (cornerRadius <= 0) {
            // Process as a regular rectangle
            gcode.append(move(startX, startY));
            gcode.append(toolDown());
            gcode.append(line(startX + rectWidth, startY));
            gcode.append(line(startX + rectWidth, startY + rectHeight));
            gcode.append(line(startX, startY + rectHeight));
            gcode.append(line(startX, startY));
        } else {
            // Process as a rounded rectangle
            // Start at top left corner plus radius on the x-axis
            gcode.append(move(startX + cornerRadius, startY));
            gcode.append(toolDown());
            
            // Top edge
            gcode.append(line(startX + rectWidth - cornerRadius, startY));
            
            // Top right corner
            drawArcTo(gcode, startX + rectWidth, startY + cornerRadius, false);
            
            // Right edge
            gcode.append(line(startX + rectWidth, startY + rectHeight - cornerRadius));
            
            // Bottom right corner
            drawArcTo(gcode, startX + rectWidth - cornerRadius, startY + rectHeight, false);
            
            // Bottom edge
            gcode.append(line(startX + cornerRadius, startY + rectHeight));
            
            // Bottom left corner
            drawArcTo(gcode, startX, startY + rectHeight - cornerRadius, false);
            
            // Left edge
            gcode.append(line(startX, startY + cornerRadius));
            
            // Top left corner
            drawArcTo(gcode, startX + cornerRadius, startY, false);
        }
        
        gcode.append(toolUp());
        // Explicitly move back to the starting point to ensure perfect positioning for repeated shapes
        if (cornerRadius <= 0) {
            gcode.append(move(startX, startY));
        } else {
            gcode.append(move(startX + cornerRadius, startY));
        }

        return gcode.toString();
    }
    
    // Helper method to draw an arc to the specified point
    private String drawArcTo(StringBuilder gcode, double endX, double endY, boolean clockwise) {
        // For simplicity, we'll use a series of small line segments to approximate the arc
        // In a real implementation, you would use G2/G3 arc commands or bezier curves
        
        Point2D start = PlotterState.getLogicalPosition();
        double startX = start.x;
        double startY = start.y;
        
        // Calculate center of the arc
        double centerX, centerY;
        if (endX > startX && endY > startY) {
            // Bottom right arc
            centerX = startX;
            centerY = endY;
        } else if (endX < startX && endY > startY) {
            // Bottom left arc
            centerX = endX;
            centerY = startY;
        } else if (endX < startX && endY < startY) {
            // Top left arc
            centerX = startX;
            centerY = endY;
        } else {
            // Top right arc
            centerX = endX;
            centerY = startY;
        }
        
        // Approximate a quarter-circle from the current point to (endX,endY).
        // The old implementation guessed angles based on end/start quadrants,
        // which can produce arcs that don't end on the requested end point.
        
        double startAngle = Math.atan2(startY - centerY, startX - centerX);
        double endAngle = Math.atan2(endY - centerY, endX - centerX);
        
        // For a rounded rectangle we always want the short 90-degree sweep.
        double delta = endAngle - startAngle;
        while (delta <= -Math.PI) delta += 2 * Math.PI;
        while (delta > Math.PI) delta -= 2 * Math.PI;
        
        if (clockwise) {
            if (delta > 0) delta -= 2 * Math.PI;
        } else {
            if (delta < 0) delta += 2 * Math.PI;
        }
        
        // If numeric issues leave us with the long way around,
        // clamp to the expected quarter turn.
        if (Math.abs(delta) > (Math.PI / 2 + 1e-6)) {
            delta = (delta < 0) ? -Math.PI / 2 : Math.PI / 2;
        }
        
        // 10 segments per 90 degrees.
        int segments = 10;
        double angleStep = delta / segments;
        for (int i = 1; i <= segments; i++) {
            double angle = startAngle + i * angleStep;
            double x = centerX + cornerRadius * Math.cos(angle);
            double y = centerY + cornerRadius * Math.sin(angle);
            gcode.append(line(x, y));
        }
        
        // Ensure we land exactly on the requested end point.
        gcode.append(line(endX, endY));
        return "";
    }

}
