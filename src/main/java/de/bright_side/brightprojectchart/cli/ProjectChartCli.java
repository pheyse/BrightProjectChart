package de.bright_side.brightprojectchart.cli;

import de.bright_side.brightprojectchart.bl.ChartCreator;
import de.bright_side.brightprojectchart.dao.ProjectPlanExcelDAO;
import de.bright_side.brightprojectchart.model.ProjectPlan;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class ProjectChartCli {
    private static final String INFO = "BrightProjectChart - Philip Heyse - Version 1.0.0";

    private static final String SWITCH_NAME_INPUT_FILE = "-i";
    private static final String SWITCH_NAME_OUTPUT_FILE = "-o";

    private enum ParamType {INPUT_FILE, OUTPUT_FILE}

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.INFO);
        try {
            System.out.println(INFO);
            Map<ParamType, String> params = readParamsAndCheck(args);
            if (params == null){
                showHelpAndExitWithError();
                return;
            }
            File inputFile = new File(params.get(ParamType.INPUT_FILE));
//            File projectPlanFile = new File("data/Test_A_PP_001.xlsx");
            ProjectPlan projectPlan = readProjectPlan(inputFile);
            if (projectPlan == null){
                System.exit(-1);
                return;
            }

            File outputFile = new File(params.get(ParamType.OUTPUT_FILE));

            try{
                ChartCreator chartCreator = new ChartCreator();
                chartCreator.createChart(outputFile, projectPlan);
            } catch (Exception e){
                System.out.println("Internal error while creating chart: " + e);
                e.printStackTrace();
                System.exit(-1);
                return;
            }

            System.out.println("Chart written to '" + outputFile.getAbsolutePath() + "'");
            System.out.println("Finished successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ProjectPlan readProjectPlan(File projectPlanFile) {
        ProjectPlan result = null;
        if (!projectPlanFile.exists()){
            System.out.println("Input file '" + projectPlanFile.getAbsolutePath() + "' does not exist");
            showHelpAndExitWithError();
            return null;
        }
        try{
            result = new ProjectPlanExcelDAO().readProjectPlan(projectPlanFile, null);
            if (result.getErrors() != null){
                System.out.println("Could not read file '" + projectPlanFile.getAbsolutePath() + "':");
                for (String i: result.getErrors()){
                    System.out.println(" - " + i);
                }
                return null;
            }
            return result;
        } catch (Throwable t){
            System.out.println("Internal error while reading file '" + projectPlanFile.getAbsolutePath() + "'.");
            t.printStackTrace();
            return null;
        }
    }

    private static Map<ParamType, String> readParamsAndCheck(String[] args) {
        if (args.length == 0){
            return null;
        }
        Map<ParamType, String> result = readParamsOrNull(args);
        if (result == null){
            return null;
        }
        if (!result.containsKey(ParamType.INPUT_FILE)){
            System.out.println("No input file specified");
            return null;
        }
        if (!result.containsKey(ParamType.OUTPUT_FILE)){
            System.out.println("No output file specified");
            return null;
        }
        return result;
    }

    private static void showHelpAndExitWithError() {
        System.out.println("");
        System.out.println("usage:");
        System.out.println("  parameters: " + SWITCH_NAME_INPUT_FILE + " <path to input file> " + SWITCH_NAME_OUTPUT_FILE + " <path to output file>");
        System.out.println("  parameters example: " + SWITCH_NAME_INPUT_FILE + " \"C:\\myplan.xlsx\" " + SWITCH_NAME_OUTPUT_FILE + " \"C:\\mychart.png\"");
        System.out.println("");
        System.exit(-1);
    }

    private static EnumMap<ParamType, String> readParamsOrNull(String[] args) {
        EnumMap<ParamType, String> result = new EnumMap<ParamType, String>(ParamType.class);
        int pos = 0;
        while (pos < args.length){
            String argument = args[pos];
            ParamType type = readType(argument);
            if (type == null){
                System.out.println("Unknown parameter: '" + argument + "'");
                return null;
            }
            if (Arrays.asList(ParamType.INPUT_FILE, ParamType.OUTPUT_FILE).contains(type)){
                String nextValue = readNextValue(args, pos);
                pos ++;
                if (nextValue == null){
                    System.out.println("missing value for parameter '" + argument + "'");
                    return null;
                }
                result.put(type, nextValue);
            }

            pos ++;
        }
        return result;
    }

    private static String readNextValue(String[] args, int pos) {
        if (pos + 1 >= args.length){
            return null;
        }
        return args[pos + 1];
    }

    private static ParamType readType(String argument) {
        switch (argument){
            case SWITCH_NAME_INPUT_FILE:
                return ParamType.INPUT_FILE;
            case SWITCH_NAME_OUTPUT_FILE:
                return ParamType.OUTPUT_FILE;
            default:
                return null;
        }
    }
}
