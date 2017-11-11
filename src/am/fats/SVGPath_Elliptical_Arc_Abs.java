package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Elliptical_Arc_Abs extends SVGPath_Command
{
    protected FileLineWriter mGCode;
    protected TransformationStack mTrans;

    public SVGPath_Elliptical_Arc_Abs()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        mGCode = gcode;
        mTrans = trans.clone();
        // SVG Params
        //(rx ry x-axis-rotation large-arc-flag sweep-flag x y)+
        double rx;
        double ry;
        double xAxisRotation;
        double largeArcFlag;
        double sweepFlag;

        //These are endpoint of the ellipse, and come from data
        double x;
        double y;

        comment("Starting elliptical arc", gcode);

        while(mIsOk)
        {
            //Read x value
            rx = readDouble(commandQueue);
            ry = readDouble(commandQueue);
            xAxisRotation = readDouble(commandQueue);
            largeArcFlag = readDouble(commandQueue);
            sweepFlag = readDouble(commandQueue);
            x = readDouble(commandQueue);
            y = readDouble(commandQueue);
            if(mIsOk)
            {
                drawArc(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y);
            }
        }
    }

    protected void drawArc(double rx, double ry, double xAxisRotation, double largeArc, double sweep, double x, double y) throws IOException {
        //Straight port; http://svn.apache.org/repos/asf/xmlgraphics/batik/branches/svg11/sources/org/apache/batik/ext/awt/geom/ExtendedGeneralPath.java
        //
        // Elliptical arc implementation based on the SVG specification notes
        //
        Point2D startPoint = PlotterState.getLogicalPosition();
        double x0 = startPoint.x;
        double y0 = startPoint.y;
    
        // Compute the half distance between the current and the final point
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        // Convert angle from degrees to radians
        double angle = Math.toRadians(xAxisRotation % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
    
        //
        // Step 1 : Compute (x1, y1)
        //
        double x1 =  (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
    
        Point2D p1Prime = new Point2D(x1, y1);
    
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        // check that radii are large enough
        double radiiCheck = Px1/Prx + Py1/Pry;
        if (radiiCheck > 1)
        {
            double mpl = Math.sqrt(radiiCheck);
            rx = mpl * rx;
            ry = mpl * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }
    
        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArc == sweep) ? -1 : 1;
        double coNum = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1));
        if(coNum < 0)
            coNum = 0;
        double sq = coNum / ((Prx*Py1)+(Pry*Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);
    
        Point2D centerPrime = new Point2D(cx1, cy1);
    
        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);
    
        Point2D centerPoint = new Point2D(cx, cy);

        //From here on there is a difference of opinion.  The above code is specified in the
        //notes at https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
        //but it is what you do with it.
        drawArc_nc(p1Prime, centerPrime, centerPoint, rx, ry, sweep > 0, angle);
    }

    // Draws the arc using the method from https://github.com/mythagel/nc_tools/blob/master/src/nc_svgpath/svgpath.cpp
    protected void drawArc_nc(Point2D p1Prime, Point2D centerPrime, Point2D center, double rx, double ry, boolean sweep, double phi) throws IOException {
        Point2D u = new Point2D(1, 0);

        Point2D v = new Point2D();
        v.x = (p1Prime.x - centerPrime.x) / rx;
        v.y = (p1Prime.y - centerPrime.y) / ry;

        double theta1 = deltaAngle(u, v);

        u.x = (p1Prime.x - centerPrime.x) / rx;
        u.y = (p1Prime.y - centerPrime.y) / ry;

        v.x = (-p1Prime.x - centerPrime.x) / rx;
        v.y = (-p1Prime.y - centerPrime.y) / ry;

        double deltaTheta = deltaAngle(u, v);

        if(!sweep && deltaTheta > 0)
            deltaTheta -= 2 * Math.PI;
        else if(sweep && deltaTheta < 0)
            deltaTheta += 2 * Math.PI;

        //Calculate an appropriate number of steps
        //deltaTheta is the amount of the ellipse being drawn, in radians
        //there are 2pi radians in a circle (6.28ish)
        //We can use this to estimate the length of the arc using the mean
        //radius, measured in millimetres.
        int steps = (int)Math.ceil(((rx + ry) / 2) * Math.abs(deltaTheta));
        double step = deltaTheta / steps;
        double theta = theta1;

        for(int i = 0; i < steps; ++i, theta += step)
        {
            Point2D m1 = new Point2D();
            m1.x = rx * Math.cos(theta);
            m1.y = ry * Math.sin(theta);

            Point2D p = new Point2D();
            p.x = (Math.cos(phi) * m1.x) + (-Math.sin(phi) * m1.y) + center.x;
            p.y = (Math.sin(phi) * m1.x) + ( Math.cos(phi) * m1.y) + center.y;

            GCodeLine line = new GCodeLine(p.x, p.y);
            //Make sure we don't try to reapply transformations
            line.setTransformationStack(mTrans);
            mGCode.writeLine(line.toString());
        }
    }


    //Direct port from https://github.com/mythagel/nc_tools/blob/master/src/nc_svgpath/svgpath.cpp
    protected double deltaAngle(Point2D u, Point2D v)
    {
        double sign = u.x * v.y - u.y * v.x;
        double num = (u.x * v.x) + (u.y * v.y);
        double normu = Math.sqrt((u.x * u.x) + (u.y * u.y));
        double normv = Math.sqrt((v.x * v.x) + (v.y * v.y));
        double den = normu * normv;

        double cosStep = Math.acos(num/den);
        //Remove the sign from cosStep
        cosStep = Math.abs(cosStep);

        //And add it again if necessary
        if(sign < 0)
            cosStep *= -1;

        return cosStep;
    }


}
