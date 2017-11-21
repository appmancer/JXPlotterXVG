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
    public void process(Attributes atts, FileLineWriter gcode) throws IOException
    {
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

        String header = String.format("Image w:%d h:%d", pixWidth, pixHeight);
        GCodeComment imageHeader = new GCodeComment(header);
        mGCode.writeLine(imageHeader);

        if(horizontalPixelSizeMM < 0.25 || verticalPixelSizeMM < 0.25)
        {
            String warnText = String.format("Warning: Image resolution is low h:%.4f v:.%4f",
                    horizontalPixelSizeMM, verticalPixelSizeMM);
            GCodeComment warning = new GCodeComment(warnText);
        }

        IntBuffer greyMap = ByteBuffer.allocate(pixWidth * pixHeight * 4).asIntBuffer();

        for(int i=0; i<pixHeight; i++)
        {
            for(int j=0; j<pixWidth; j++)
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
        GCodeComment startLR = new GCodeComment("Starting left to right");
        GCodeComment startRL = new GCodeComment("Starting right to left");
        //OK, we have a byte buffer with all of the pixel data, and the pixel size in millimeters.  How hard can it be?
        for(int i=0; i<pixHeight; i++) {
            //Move to start of line

            startX = 0;
            endX = 0;

            gcode.writeLine(startLR);

            GCodeLaserOff laserOff = new GCodeLaserOff();
            gcode.writeLine(laserOff.toString());

            GCodeMove move = new GCodeMove(attX, attY + i * verticalPixelSizeMM);
            move.setTransformationStack(mTrans);
            gcode.writeLine(move.toString());

            //Left to right
            for (int j = 0; j < pixWidth; j++) {
                int nextShade = greyMap.get(i*pixWidth + j);
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
            if (endX < pixWidth) {
                drawPixels(attX + width, attY + i * verticalPixelSizeMM, currentShade);
            }

            //move on another line
            i++;
            if(i == pixHeight)
                break;

            gcode.writeLine(startRL);

            gcode.writeLine(laserOff.toString());
            move = new GCodeMove(attX+width, attY + i * verticalPixelSizeMM);
            move.setTransformationStack(mTrans);
            gcode.writeLine(move.toString());

            startX = 0;
            endX = pixWidth;

            //Right to left
            for (int j = pixWidth-1; j >= 0; j--) {
                int nextShade = greyMap.get(i*pixWidth + j);
                if (nextShade != currentShade) {
                    endX = j;
                    currentShade = nextShade;
                    if (startX != endX) {
                        //Draw a line!
                        drawPixels(attX + endX * horizontalPixelSizeMM, attY + i * verticalPixelSizeMM, currentShade);
                        startX = endX;
                    }
                }
            } //End of line
            //Do we need to draw to end of line?
            if (endX > 0) {
                drawPixels(attX , attY + i * verticalPixelSizeMM, currentShade);
            }
        }

        GCodeComment finished = new GCodeComment("Raster image is complete");
        gcode.writeLine(finished);
    }

    private void drawPixels(double endx, double endy, int power) throws IOException {
        //Draw a line!
        if(Tool.currentTool() == Tool.TOOL_LASER)
        {
            Tool.setPower(255 - power);
            GCodeLaserOn on = new GCodeLaserOn();
            mGCode.writeLine(on.toString());
        }
        GCodeLine line = new GCodeLine(endx, endy);
        line.setTransformationStack(mTrans);
        mGCode.writeLine(line.toString());
    }
}
