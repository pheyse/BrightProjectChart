package de.bright_side.brightprojectchart.dao;

import de.bright_side.brightprojectchart.model.ProjectColor;
import de.bright_side.brightprojectchart.model.ProjectPlan;

import java.util.ArrayList;

public class DAOUtil {

    private ReadColorResult createReadColorResult(){
        return new ReadColorResult();
    }

    public class ReadColorResult{
        public String errorMessage;
        public ProjectColor color;
    }

    public static ReadColorResult readColor(String colorString) {
        ReadColorResult result = new DAOUtil().createReadColorResult();

        if ((colorString == null) || (colorString.trim().isEmpty())){
            return result;
        }

        try{
            String rest = colorString.trim();
            int pos = rest.indexOf(",");
            if (pos < 0){
                throw new Exception("Wrong format");
            }

            int red = readInt(rest.substring(0, pos).trim(), 0, 255);

            rest = rest.substring(pos + 1);
            pos = rest.indexOf(",");
            if (pos < 0){
                throw new Exception("Wrong format");
            }

            int green = readInt(rest.substring(0, pos).trim(), 0, 255);
            rest = rest.substring(pos + 1);

            int blue = readInt(rest.trim(), 0, 255);

            result.color = new ProjectColor(red, green, blue);
        } catch (Exception e){
            result.errorMessage = "Could not read color value from text: '" + colorString
                    + "'. Colors must have the format '128,255,0' where the first value is red, the second green and " +
                    "the last is blue. Each value must be between 0 and 255";
        }
        return result;
    }

    public static int readInt(String text, int min, int max) throws Exception {
        int result = Integer.valueOf(text.trim());
        if (result < min){
            throw new Exception("value lower than " + min);
        }
        if (result > max){
            throw new Exception("value greater than " + max);
        }
        return result;
    }

    public static void addError(ProjectPlan plan, String message){
        if (plan.getErrors() == null){
            plan.setErrors(new ArrayList<>());
        }
        plan.getErrors().add(message);
    }


}
