package de.bright_side.brightprojectchart.dao;

import de.bright_side.brightprojectchart.model.*;
import de.bright_side.brightprojectchart.model.ProjectPlan.ChartSetting;
import de.bright_side.brightprojectchart.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class ProjectPlanExcelDAO {
    private final static Logger LOGGER = Logger.getLogger(ProjectPlanExcelDAO.class.getName());

    private static final int NUMBER_OF_COLUMNS = 12;
    private static final String TYPE_STRING_SECTION = "section";
    private static final String TYPE_STRING_BAR = "bar";
    private static final String TYPE_STRING_MILESTONE = "milestone";
    private static final String TYPE_STRING_SETTING = "setting";
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final ProjectColor DEFAULT_SECTION_COLOR = new ProjectColor(255, 255, 255);
    private static final ProjectColor DEFAULT_BAR_COLOR = new ProjectColor(76, 108, 156);
    private static final ProjectColor DEFAULT_MILESTONE_COLOR = new ProjectColor(255, 192, 0);
    private static final Map<String, ChartSetting> SETTING_NAME_TO_ENUM_MAP = ChartSettingsDAO.getSettingNameToCharSettingMap();

    public ProjectPlan readProjectPlan(File file, String sheetName) {
        ProjectPlan result = new ProjectPlan();

        setDefaultValues(result);

        try (Workbook workbook = new XSSFWorkbook(file)){
            Sheet sheet = null;
            if (sheetName != null){
                sheet = workbook.getSheet(sheetName);
                if (sheet == null){
                    DAOUtil.addError(result, "There is no sheet with name '" + sheetName + "'");
                    return result;
                }
            } else {
                if (workbook.getNumberOfSheets() > 1){
                    DAOUtil.addError(result, "There are multiple sheets in the workbook. Please specify the sheet name");
                    return result;
                }
            }
            sheet = workbook.getSheetAt(0);
            readProjectPlanFromSheet(sheet, result);
        } catch (Exception e){
            e.printStackTrace();
            DAOUtil.addError(result, "" + e);
            return result;
        }

        setShownDateSpan(result);


        return result;
    }

    private void setShownDateSpan(ProjectPlan result) {
        if (result.getShownDateSpan() == null){
            result.setShownDateSpan(determineDateSpan(result));
        }
        Long settingsStartDate = (Long)result.getChartSettings().get(ChartSetting.CHART_START_DATE);
        Long settingsEndDate = (Long)result.getChartSettings().get(ChartSetting.CHART_END_DATE);

        if (settingsStartDate != null){
            result.getShownDateSpan().setStart(settingsStartDate);
        }
        if (settingsEndDate != null){
            result.getShownDateSpan().setEnd(settingsEndDate);
        }
    }

    private DateSpan determineDateSpan(ProjectPlan plan) {
        if (plan.getSections() == null){
            return getDateSpanCurrentYear();
        }

        long start = -1;
        long end = -1;
        for (Section section: plan.getSections()){
            if (section.getPlanItems() != null){
                for (PlanItem planItem: section.getPlanItems()){
                    if (start == -1){
                        start = planItem.getDateSpan().getStart();
                    }
                    if (end == -1){
                        end = planItem.getDateSpan().getStart();
                    }

                    start = Math.min(planItem.getDateSpan().getStart(), start);
                    end = Math.max(planItem.getDateSpan().getEnd(), end);
                }
            }
        }

        return new DateSpan(start, end);
    }

    private DateSpan getDateSpanCurrentYear() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        long start = cal.getTimeInMillis();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        long end = cal.getTimeInMillis();
        return new DateSpan(start, end);
    }

    private void setDefaultValues(ProjectPlan result) {
        result.setShowWeeks(true);
        result.setShowMonths(true);
        result.setShowYears(true);
        result.setChartSettings(new ChartSettingsDAO().getDefaultChartSettings());
    }

    private void readProjectPlanFromSheet(Sheet sheet, ProjectPlan result) {
        checkColumnNames(sheet, result);
        if (result.getErrors() != null){
            return;
        }

        readSheetData(sheet, result);
    }

    private void readSheetData(Sheet sheet, ProjectPlan plan) {
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex ++){
            Row row = sheet.getRow(rowIndex);
            if ((row != null) && (!ExcelUtil.isEmpty(row, NUMBER_OF_COLUMNS))){
                InputRowData rowData = readInputRowData(rowIndex, row, plan);
                if (plan.getErrors() != null){
                    plan.getErrors().add(0, "Error while reading row " + (rowIndex + 1));
                    return;
                }

                readRowData(rowData, plan, rowIndex);
                if (plan.getErrors() != null){
                    plan.getErrors().add(0, "Error while processing row " + (rowIndex + 1));
                    return;
                }
            }
        }
    }

    private void readRowData(InputRowData rowData, ProjectPlan plan, int rowIndex) {
        switch (rowData.getRowType()){
            case SETTING:
                readSetting(rowData, plan, rowIndex);
                break;
            case SECTION:
                readSection(rowData, plan, rowIndex);
                break;
            case BAR:
            case MILESTONE:
                readBarOrMilestone(rowData, plan, rowIndex);
                break;
            default:
                DAOUtil.addError(plan, "Unknown row type: " + rowData.getRowType());
        }
    }

    private void readBarOrMilestone(InputRowData rowData, ProjectPlan plan, int rowIndex) {
        PlanItem item = new PlanItem();
        ProjectColor defaultColor = null;
        String typeName = "";
        if (rowData.getRowType() == InputRowData.RowType.BAR){
            defaultColor = DEFAULT_BAR_COLOR;
            item.setType(PlanItem.PlanItemType.BAR);
            typeName = "bars";
        } else if (rowData.getRowType() == InputRowData.RowType.MILESTONE){
            defaultColor = DEFAULT_MILESTONE_COLOR;
            item.setType(PlanItem.PlanItemType.MILESTONE);
            typeName = "milestones";
        } else {
            DAOUtil.addError(plan, "Unexpected row type: " + rowData.getRowType());
            return;
        }

        if (hasValue(rowData.getSetting())){
            DAOUtil.addError(plan, "There should be no entry for 'setting' for " + typeName);
        }
        if (hasValue(rowData.getValue())){
            DAOUtil.addError(plan, "There should be no entry for 'value' for " + typeName);
        }

        item.setLabel(getLabel(rowData));
        item.setColor(nvl(rowData.getColor(), defaultColor));

        if ((item.getLabel() != null) && (!item.getLabel().isEmpty())){
            LOGGER.fine("readBarOrMilestone. label = >>" + item.getLabel() + "<<, text style = " + getTextStyle(rowData));
        }
        item.setTextStyle(getTextStyle(rowData));



        if (rowData.getStart() == null){
            DAOUtil.addError(plan, "The value for 'start' may not be empty");
            return;
        }

        long useEnd = 0;
        if (rowData.getRowType() == InputRowData.RowType.MILESTONE){
            if ((rowData.getEnd() != null) && (!rowData.getEnd().equals(rowData.getStart()))){
                DAOUtil.addError(plan, "There should no entry for 'end' for milestones (or it should be equal to the start)");
                return;
            }
            useEnd = rowData.getStart();
        } else {
            useEnd = rowData.getEnd();
        }
        item.setDateSpan(new DateSpan(rowData.getStart(), useEnd));

        if (plan.getSections() == null){
            addDefaultSection(plan);
        }
        Section section = plan.getSections().get(plan.getSections().size() - 1);
        if (section.getPlanItems() == null){
            section.setPlanItems(new ArrayList<>());
        }

        section.getPlanItems().add(item);
    }

    private void addDefaultSection(ProjectPlan plan) {
        plan.setSections(new ArrayList<>());
        Section section = new Section();
        section.setLabel("");
        section.setTextStyle(new TextStyle(DEFAULT_TEXT_SIZE, false, false, 0, null));
        section.setColor(DEFAULT_SECTION_COLOR);
        plan.getSections().add(section);
    }

    private boolean hasValue(String string){
        return !nvl(string, "").trim().isEmpty();
    }

    private void readSetting(InputRowData rowData, ProjectPlan plan, int rowIndex) {
        if (rowData.getSetting() == null){
            DAOUtil.addError(plan, "'" + TYPE_STRING_SETTING + "' was set as a type but the setting column is empty.");
            return;
        }

        ChartSetting setting = SETTING_NAME_TO_ENUM_MAP.get(rowData.getSetting().toLowerCase());
        if (setting == null) {
            DAOUtil.addError(plan, "Unknown setting '" + rowData.getSetting() + "'. Possible values: " + SETTING_NAME_TO_ENUM_MAP.keySet());
            return;
        }

        String errorMessage = new ChartSettingsDAO().applyStringSetting(plan, setting, rowData.getValue());
        if (errorMessage != null) {
            DAOUtil.addError(plan, errorMessage);
            return;
        }
    }

    private void readSection(InputRowData rowData, ProjectPlan plan, int rowIndex) {
        if (plan.getSections() == null){
            plan.setSections(new ArrayList<>());
        }

        if (hasValue(rowData.getSetting())){
            DAOUtil.addError(plan, "There should be no entry for 'setting' for sections");
        }
        if (hasValue(rowData.getValue())){
            DAOUtil.addError(plan, "There should be no entry for 'value' for sections");
        }

        if (rowData.getTextPos() != null){
            DAOUtil.addError(plan, "There should be no entry for 'text-pos' value for sections");
        }

        Section section = new Section();
        section.setLabel(getLabel(rowData));
        section.setTextStyle(getTextStyle(rowData));
        section.setColor(nvl(rowData.getColor(), DEFAULT_SECTION_COLOR));
        section.setPlanItems(new ArrayList<>());

        plan.getSections().add(section);
    }

    private ProjectColor nvl(ProjectColor color, ProjectColor defaultColor) {
        if (color == null){
            return defaultColor;
        }
        return color;
    }

    private String getLabel(InputRowData rowData){
        return nvl(rowData.getLabel(), "").trim();
    }

    private TextStyle getTextStyle(InputRowData rowData) {
        return new TextStyle(nvl(rowData.getTextSize(), DEFAULT_TEXT_SIZE),
                rowData.isBold(),
                rowData.isItalic(),
                nvl(rowData.getIndent(), 0),
                rowData.getTextPos());
    }

    private String nvl(String text, String valueIfNull) {
        if (text == null){
            return valueIfNull;
        }
        return text;
    }

    private int nvl(Integer value, int valueIfNull) {
        if (value == null){
            return valueIfNull;
        }
        return value;
    }

    private InputRowData readInputRowData(int rowIndex, Row row, ProjectPlan plan) {
        InputRowData result = new InputRowData();
        int column = 0;
        String typeString = ExcelUtil.readCellAsString(row, column ++);
        result.setRowType(readRowType(typeString, plan));
        result.setLabel(ExcelUtil.readCellAsString(row, column ++));
        result.setStart(ExcelUtil.readCellAsDateOrNull(row, column ++));
        result.setEnd(ExcelUtil.readCellAsDateOrNull(row, column ++));
        String colorString = ExcelUtil.readCellAsString(row, column ++);
        result.setColor(readColor(colorString, plan));
        result.setTextSize(ExcelUtil.readCellAsIntOrNull(row, column ++));
        String textPosString = ExcelUtil.readCellAsString(row, column ++);
        result.setTextPos(readTextPos(textPosString, plan));

        result.setBold(ExcelUtil.readCellAsBoolean(row, column ++, false));
        result.setItalic(ExcelUtil.readCellAsBoolean(row, column ++, false));
        result.setIndent(ExcelUtil.readCellAsIntOrNull(row, column ++));
        result.setSetting(ExcelUtil.readCellAsString(row, column ++));
        result.setValue(ExcelUtil.readCellAsString(row, column ++));

        return result;
    }

    protected ProjectColor readColor(String colorString, ProjectPlan plan) {
        DAOUtil.ReadColorResult readColorResult = DAOUtil.readColor(colorString);
        if (readColorResult.errorMessage != null){
            DAOUtil.addError(plan, readColorResult.errorMessage);
            return null;
        }
        return readColorResult.color;

//
//        if ((colorString == null) || (colorString.trim().isEmpty())){
//            return null;
//        }
//
//        try{
//            String rest = colorString.trim();
//            int pos = rest.indexOf(",");
//            if (pos < 0){
//                throw new Exception("Wrong format");
//            }
//
//            int red = DAOUtil.readInt(rest.substring(0, pos).trim(), 0, 255);
//
//            rest = rest.substring(pos + 1);
//            pos = rest.indexOf(",");
//            if (pos < 0){
//                throw new Exception("Wrong format");
//            }
//
//            int green = DAOUtil.readInt(rest.substring(0, pos).trim(), 0, 255);
//            rest = rest.substring(pos + 1);
//
//            int blue = DAOUtil.readInt(rest.trim(), 0, 255);
//
//            return new ProjectColor(red, green, blue);
//        } catch (Exception e){
//            addError(plan, "Could not read color value from text: '" + colorString
//                    + "'. Colors must have the format '128,255,0' where the first value is red, the second green and " +
//                    "the last is blue. Each value must be between 0 and 255");
//            return null;
//        }
    }

    private InputRowData.RowType readRowType(String typeString, ProjectPlan plan) {
        String possibleRowTypesInfo = "Possible values: '" + TYPE_STRING_SETTING + "', '" + TYPE_STRING_SECTION + "', '" + TYPE_STRING_BAR + "', '"
                + TYPE_STRING_MILESTONE + "'";
        if ((typeString == null) || (typeString.isEmpty())){
            DAOUtil.addError(plan, "Missing type value. " + possibleRowTypesInfo);
            return null;
        }

        if (typeString.equalsIgnoreCase(TYPE_STRING_SETTING)) {
            return InputRowData.RowType.SETTING;
        } else if (typeString.equalsIgnoreCase(TYPE_STRING_SECTION)) {
            return InputRowData.RowType.SECTION;
        } else if (typeString.equalsIgnoreCase(TYPE_STRING_BAR)){
            return InputRowData.RowType.BAR;
        } else if (typeString.equalsIgnoreCase(TYPE_STRING_MILESTONE)){
            return InputRowData.RowType.MILESTONE;
        }

        DAOUtil.addError(plan, "Unexpected value for type. " + possibleRowTypesInfo);
        return null;
    }

    private String textPosOptionsToString(){
        String result = "";
        for (TextStyle.TextPos i: TextStyle.TextPos.values()){
            if (!result.isEmpty()){
                result += ", ";
            }
            result += i.name().toLowerCase();
        }
        return result;
    }

    private TextStyle.TextPos readTextPos(String posString, ProjectPlan plan) {
        String possibleRowTypesInfo = "Possible values: " + textPosOptionsToString();
        if ((posString == null) || (posString.isEmpty())){
            return null;
        }

        try{
            return TextStyle.TextPos.valueOf(posString.toUpperCase());
        } catch (Exception e){
            DAOUtil.addError(plan, "Unexpected value for text-pos. " + possibleRowTypesInfo);
            return null;
        }
    }

    private void checkColumnNames(Sheet sheet, ProjectPlan result) {
        Row row = sheet.getRow(0);
        List<String> columns = Arrays.asList("type", "label", "start", "end", "color", "text-size", "text-pos", "bold", "italic", "indent", "setting", "value");
        int index = 0;
        for (String i: columns){
            verifyCellText(result, row, index, i);
            index ++;
        }
    }

    private void verifyCellText(ProjectPlan plan, Row row, int column, String text) {
        Cell cell = row.getCell(column);
        if (cell == null){
            DAOUtil.addError(plan, "There is no cell in row 1 at column " + (column + 1));
            return;
        }
        if (!cell.getStringCellValue().equalsIgnoreCase(text)){
            DAOUtil.addError(plan, "The text of cell in row 1 at column " + (column + 1)
                    + " must have the text '" + text + "', but is '" + cell.getStringCellValue() + "'");
            return;
        }
    }

}