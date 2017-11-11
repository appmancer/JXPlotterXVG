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

import java.util.ArrayDeque;

public class TransformationStack implements Cloneable
{
    ArrayDeque<Transformation> mStack;

    public TransformationStack()
    {
        mStack = new ArrayDeque<Transformation>();
    }

    public void push(double a, double b, double c, double d, double e, double f)
    {
        Transformation t = new Transformation();
        t.a = a;
        t.b = b;
        t.c = c;
        t.d = d;
        t.e = e;
        t.f = f;

        push(t);
    }

    public void push(Transformation trans)
    {
        mStack.addLast(trans);
    }

    public void pop()
    {
        mStack.removeFirst();
    }

    public void pushMatrix(Transformation trans)
    {
        mStack.addLast(trans);
    }

    public void pushRotate(double rotation)
    {
        //Convert degrees to radians
        rotation = Math.toRadians(rotation);
        Transformation t = new Transformation();
        //Formatted like the matrix representation
        t.a = Math.cos(rotation);   t.c = -Math.sin(rotation); t.e = 0;
        t.b = Math.sin(rotation);   t.d = Math.cos(rotation);  t.f = 0;

        push(t);
    }

    public void pushRotate(double rotation, double cx, double cy)
    {
        //https://www.w3.org/TR/SVG/coords.html#TransformAttribute
        /* If optional parameters <cx> and <cy> are supplied,
        * the rotate is about the point (cx, cy).
        * The operation represents the equivalent of the following
        * specification: translate(<cx>, <cy>) rotate(<rotate-angle>) translate(-<cx>, -<cy>).*/
        pushTranslate(cx, cy);

        pushRotate(rotation);

        pushTranslate(-cx, -cy);
    }

    public void pushScale(double sx, double sy)
    {
        Transformation t = new Transformation();
        t.a = sx;   t.c = 0;    t.e = 0;
        t.b = 0;    t.d = sy;   t.f = 0;

        push(t);
    }

    public void pushTranslate(double tx, double ty)
    {
        Transformation t = new Transformation();

        t.a = 1;    t.c = 0;    t.e = tx;
        t.b = 0;    t.d = 1;    t.f = ty;

        push(t);
    }

    public void pushSkew(double sx, double sy)
    {
        Transformation t = new Transformation();

        t.a = 1;    t.c = Math.tan(sx); t.e = 0;
        t.b = Math.tan(sy); t.d = 1;    t.f = 0;
    }

    public TransformationStack clone()
    {
        TransformationStack clone = new TransformationStack();
        for(Transformation t : mStack)
        {
            try {
                clone.push(t.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        return clone;
    }

    public Point2D process(double x, double y)
    {
        //Standard layout of a matrix is
        // { a, c, e }  {x}
        // { b, d, f }  {y}

        //Process each transformation in order, newest first
        // x' = a*x + c*y + e
        // y' = b*x + d*y + f

        Point2D result = new Point2D(x, y);
        Transformation[] stack = new Transformation[mStack.size()];
        mStack.toArray(stack);
        for (int i = mStack.size(); i > 0; --i)
        {
            Transformation t = stack[i - 1];
            result.x = t.a * x + t.c * y + t.e;
            result.y = t.b * x + t.d * y + t.f;

            x = result.x;
            y = result.y;
        }

        return result;
    }
}
