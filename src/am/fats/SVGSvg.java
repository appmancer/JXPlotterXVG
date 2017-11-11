package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGSvg extends SVGElement
{
    public SVGSvg()
    {
        mElementName = "svg";
    }

    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        String[] dimensions = atts.getValue("viewBox").split(" ");
        if(dimensions.length == 4) {
            double rectWidth = Double.parseDouble(dimensions[2]);
            double rectHeight = Double.parseDouble(dimensions[3]);

            Point2D viewBox = new Point2D(rectWidth, rectHeight);

            PlotterState.setViewBox(viewBox);
        }
    }
}
