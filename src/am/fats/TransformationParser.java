package am.fats;

import java.util.ArrayDeque;

public class TransformationParser
{
    public TransformationParser()
    {}

    public TransformationStack process(String input, TransformationStack trans)
    {
        TransformationStack newStack = trans.clone();

        ArrayDeque<String> tforms = new ArrayDeque<String>();
        StringBuilder current = new StringBuilder();
        int c = 0;
        while(c < input.length())
        {
            Character nextChar = input.charAt(c++);
            current.append(nextChar);
            if(nextChar == ')')
            {
                //Add this to the current and push to the array
                tforms.addLast(current.toString().trim());
                current = new StringBuilder();
            }
        }

        //Each transformation has a name, and then 1 or more parameters, separated by commas
        for(String tform : tforms) {
            String command = "";
            ArrayDeque<Double> parameters = new ArrayDeque<Double>();
            c = 0;
            StringBuilder buffer = new StringBuilder();

            while (c < tform.length()) {
                Character nextChar = tform.charAt(c++);
                if (nextChar == '(') {
                    //We've got all the command data
                    command = buffer.toString();
                    buffer = new StringBuilder();
                } else if (nextChar == ')' || nextChar == ' ' || nextChar == ',') {
                    //End of parameter
                    parameters.addLast(Double.parseDouble(buffer.toString()));
                    buffer = new StringBuilder();
                } else {
                    //add to the buffer
                    buffer.append(nextChar);
                }
            }

            Double[] params = new Double[parameters.size()];
            parameters.toArray(params);
            if (command.contentEquals("rotate")) {
                //We should have 1,2 or 3 values
                switch (params.length) {
                    case 1: {
                        newStack.pushRotate(params[0]);
                    }
                    break;
                    case 2:
                        newStack.pushRotate(params[0], params[1], params[1]);
                        break;
                    case 3:
                        newStack.pushRotate(params[0], params[1], params[2]);
                        break;
                }
            }
            else if (command.contentEquals("translate"))
            {
                //We should have 1 or 2 params
                switch (params.length) {
                    case 1:
                        newStack.pushTranslate(params[0], 0);
                        break;
                    case 2:
                        newStack.pushTranslate(params[0], params[1]);
                        break;
                }
            }
            else if (command.contentEquals("scale"))
            {
                //We should have 1 or 2 params
                switch (params.length) {
                    case 1:
                        newStack.pushScale(params[0], params[0]);
                        break;
                    case 2:
                        newStack.pushScale(params[0], params[1]);
                        break;
                }
            }
            else if (command.contentEquals("skewX"))
            {
                if (params.length == 1) {
                    newStack.pushSkew(params[0], 0);
                }
            }
            else if (command.contentEquals("skewY"))
            {
                if (params.length == 1) {
                    newStack.pushSkew(0, params[0]);
                }
            }
            else if (command.contentEquals("matrix"))
            {
                if (params.length == 6) {
                    newStack.push(params[0], params[1], params[2], params[3], params[4], params[5]);
                }
            }

        }

        return newStack;
    }
}

