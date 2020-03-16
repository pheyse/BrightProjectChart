package de.bright_side.brightprojectchart.dao;

import de.bright_side.brightprojectchart.model.ProjectColor;
import de.bright_side.brightprojectchart.model.ProjectPlan;
import de.bright_side.brightprojectchart.model.ProjectPlan.ChartSetting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartSettingsDAO {
    private static final int BAR_HEIGHT_IN_PX = 14;
    private static final int ROW_HEIGHT_IN_PX = 24;
    private static final int DAY_WIDTH_IN_PX = 3;
    private static final int LABELS_WIDTH_IN_PX = 150;
    private static final int TEXT_PADDING_TOP = 3;
    private static final int TEXT_PADDING_LEFT = 5;
    private static final ProjectColor SECTION_BORDER_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor AREAS_BORDER_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor SECTION_LABEL_COLOR = new ProjectColor(0, 0, 0);
    private static final int HEADER_BAR_HEIGHT = 20;
    private static final String FONT_NAME = "Arial";
    private static final ProjectColor HEADER_BACKGROUND_COLOR = new ProjectColor(255, 255, 255);
    private static final int HEADER_BAR_REGULAR_TEXT_SIZE = 12;
    private static final int HEADER_BAR_SMALL_TEXT_SIZE = 10;
    private static final ProjectColor HEADER_TEXT_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor PLAN_ITEM_LABEL_COLOR = new ProjectColor(0, 0, 0);
    private static final Long CHART_START_DATE = null;
    private static final Long CHART_END_DATE = null;

    private Set<ChartSetting> DATE_SETTINGS = new TreeSet<ChartSetting>(Arrays.asList(ChartSetting.CHART_START_DATE, ChartSetting.CHART_END_DATE));
    private Set<ChartSetting> STRING_SETTINGS = new TreeSet<ChartSetting>(Arrays.asList(ChartSetting.FONT_NAME));
    private Set<ChartSetting> COLOR_SETTINGS = new TreeSet<ChartSetting>(Arrays.asList(ChartSetting.SECTION_BORDER_COLOR
            , ChartSetting.AREAS_BORDER_COLOR, ChartSetting.SECTION_LABEL_COLOR, ChartSetting.HEADER_BACKGROUND_COLOR
            , ChartSetting.HEADER_TEXT_COLOR, ChartSetting.PLAN_ITEM_LABEL_COLOR));
    private Set<ChartSetting> INT_SETTINGS = new TreeSet<ChartSetting>(Arrays.asList(ChartSetting.BAR_HEIGHT_IN_PX
            , ChartSetting.ROW_HEIGHT_IN_PX, ChartSetting.DAY_WIDTH_IN_PX, ChartSetting.LABELS_WIDTH_IN_PX
            , ChartSetting.TEXT_PADDING_TOP, ChartSetting.TEXT_PADDING_LEFT, ChartSetting.HEADER_BAR_HEIGHT
            , ChartSetting.HEADER_BAR_REGULAR_TEXT_SIZE, ChartSetting.HEADER_BAR_SMALL_TEXT_SIZE));

    public EnumMap<ChartSetting, Object> getDefaultChartSettings(){
        EnumMap<ChartSetting, Object> result = new EnumMap<ChartSetting, Object>(ChartSetting.class);
        result.put(ChartSetting.BAR_HEIGHT_IN_PX, BAR_HEIGHT_IN_PX);
        result.put(ChartSetting.ROW_HEIGHT_IN_PX, ROW_HEIGHT_IN_PX);
        result.put(ChartSetting.DAY_WIDTH_IN_PX, DAY_WIDTH_IN_PX);
        result.put(ChartSetting.LABELS_WIDTH_IN_PX, LABELS_WIDTH_IN_PX);
        result.put(ChartSetting.TEXT_PADDING_TOP, TEXT_PADDING_TOP);
        result.put(ChartSetting.TEXT_PADDING_LEFT, TEXT_PADDING_LEFT);
        result.put(ChartSetting.SECTION_BORDER_COLOR, SECTION_BORDER_COLOR);
        result.put(ChartSetting.AREAS_BORDER_COLOR, AREAS_BORDER_COLOR);
        result.put(ChartSetting.SECTION_LABEL_COLOR, SECTION_LABEL_COLOR);
        result.put(ChartSetting.HEADER_BAR_HEIGHT, HEADER_BAR_HEIGHT);
        result.put(ChartSetting.FONT_NAME, FONT_NAME);
        result.put(ChartSetting.HEADER_BACKGROUND_COLOR, HEADER_BACKGROUND_COLOR);
        result.put(ChartSetting.HEADER_BAR_REGULAR_TEXT_SIZE, HEADER_BAR_REGULAR_TEXT_SIZE);
        result.put(ChartSetting.HEADER_BAR_SMALL_TEXT_SIZE, HEADER_BAR_SMALL_TEXT_SIZE);
        result.put(ChartSetting.HEADER_TEXT_COLOR, HEADER_TEXT_COLOR);
        result.put(ChartSetting.PLAN_ITEM_LABEL_COLOR, PLAN_ITEM_LABEL_COLOR);
        result.put(ChartSetting.CHART_START_DATE, CHART_START_DATE);
        result.put(ChartSetting.CHART_END_DATE, CHART_END_DATE);
        return result;
    }

    public static Map<String, ProjectPlan.ChartSetting> getSettingNameToCharSettingMap(){
        Map<String, ProjectPlan.ChartSetting> result = new TreeMap<>();

        for (ProjectPlan.ChartSetting i: ProjectPlan.ChartSetting.values()){
            result.put(i.name().toLowerCase().replace("_", "-"), i);
        }

        return result;
    }

    public String applyStringSetting(ProjectPlan plan, ChartSetting setting, String stringValue) {
        try{
            Object value = null;
            if (DATE_SETTINGS.contains(setting)){
                value = new SimpleDateFormat("yyyy-MM-dd").parse(stringValue).getTime();
            } else if (STRING_SETTINGS.contains(setting)){
                value = stringValue;
            } else if (COLOR_SETTINGS.contains(setting)){
                DAOUtil.ReadColorResult readColorResult = DAOUtil.readColor(stringValue);
                if (readColorResult.errorMessage != null){
                    throw new Exception(readColorResult.errorMessage);
                }
                value = readColorResult.color;
            } else if (INT_SETTINGS.contains(setting)){
                value = Integer.valueOf(stringValue);
            }
            plan.getChartSettings().put(setting, value);
        } catch (Exception e){
            return e.getMessage();
        }
        return null;
    }
}
