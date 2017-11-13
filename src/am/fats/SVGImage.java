package am.fats;

import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

public class SVGImage extends SVGElement
{
    TransformationStack mTrans;
    FileLineWriter mGCode;

    public SVGImage()
    {
        mElementName = "image";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        mTrans = trans.clone();
        mGCode = gcode;

        //Get the base64 encoded image
        double attX = Double.parseDouble(atts.getValue("x"));
        double attY = Double.parseDouble(atts.getValue("y"));
        double width = Double.parseDouble(atts.getValue("width"));
        double height = Double.parseDouble(atts.getValue("height"));

        String base64 = atts.getValue("xlink:href");

        //Decode image

        //Thanks to https://stackoverflow.com/questions/23979842/convert-base64-string-to-image
        // tokenize the data
        String base64Image = base64.split(",")[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

        Raster r = image.getRaster();
        int pixWidth = r.getWidth();
        int pixHeight = r.getHeight();

        double horizontalPixelSizeMM = width / pixWidth;
        double verticalPixelSizeMM = height / pixHeight;

        IntBuffer greyMap = ByteBuffer.allocate(pixWidth * pixHeight).asIntBuffer();

        for(int i=0; i<height; i++)
        {
            for(int j=0; j<width; j++)
            {
                Color c = new Color(image.getRGB(j, i));
                int red = (int)(c.getRed() * 0.299); //76.245
                int green = (int)(c.getGreen() * 0.587); //149.785
                int blue = (int)(c.getBlue() *0.114);//29.07
                int shadeVal = red + green + blue;
                greyMap.put(shadeVal);
            }
        }

        int currentShade = 0;
        double startX = 0;
        double endX = 0;
        //OK, we have a byte buffer with all of the pixel data, and the pixel size in millimeters.  How hard can it be?
        for(int i=0; i<height; i++) {
            //Move to start of line

            GCodeMove move = new GCodeMove(attX, attY + i * verticalPixelSizeMM);
            move.setTransformationStack(trans);
            gcode.writeLine(move.toString());

            for (int j = 0; j < width; j++) {
                int nextShade = greyMap.get(i*pixHeight + j);
                if (nextShade != currentShade) {
                    endX = j;
                    currentShade = nextShade;
                    if (startX < endX) {
                        //Draw a line!
                        drawPixels(attX + endX * horizontalPixelSizeMM, attY + i * verticalPixelSizeMM, currentShade);
                        startX = endX;
                    }
                }
            } //End of line
            //Do we need to draw to end of line?
            if (startX < endX) {
                drawPixels(attX + pixWidth * horizontalPixelSizeMM, attY + pixHeight * verticalPixelSizeMM, currentShade);
                startX = endX;
            }
        }
    }

    private void drawPixels(double endx, double endy, int power) throws IOException {
        //Draw a line!
        if(Tool.currentTool() == Tool.TOOL_LASER)
        {
            Tool.setPower(power);
            GCodeLaserOn on = new GCodeLaserOn();
            mGCode.writeLine(on.toString());
        }
        GCodeLine line = new GCodeLine(endx, endy);
        line.setTransformationStack(mTrans);
        mGCode.writeLine(line.toString());
    }
}
