package de.bright_side.brightprojectchart.util;

import org.apache.poi.ss.usermodel.*;

import java.util.Date;
import java.util.Locale;

public class ExcelUtil {
    public static boolean isEmpty(Row row, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++){
            Cell cell = row.getCell(i);
            if ((cell != null) && (cell.getStringCellValue() != null) && (!cell.getStringCellValue().isEmpty())){
                return false;
            }
        }
        return true;
    }

    public static Cell getCell(Row row, int columnIndex){
        if (row == null){
            return null;
        }
        return row.getCell(columnIndex);
    }

    public static String readCellAsStringViaEvaluator(Row row, int columnIndex){
        Cell cell = getCell(row, columnIndex);
        if (cell == null){
            return null;
        }

        DataFormatter formatter = new DataFormatter(Locale.getDefault());
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        return formatter.formatCellValue(cell, evaluator);
    }

    public static String readCellAsString(Row row, int columnIndex) {
        Cell cell = getCell(row, columnIndex);
        if (cell == null){
            return null;
        }

        if (cell.getCellType() == CellType.FORMULA){
            switch(cell.getCachedFormulaResultType()) {
                case NUMERIC:
                    return numericToString(cell);
                case STRING:
                    return cell.getRichStringCellValue().getString();
                default:
                    return cell.getStringCellValue();
            }
        } else if (cell.getCellType() == CellType.NUMERIC){
            return numericToString(cell);
        }



        return cell.getStringCellValue();
    }

    private static String numericToString(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return new DataFormatter().formatCellValue(cell);
        }
        double numericValue = cell.getNumericCellValue();
        if (numericValue == Math.floor(numericValue)){
            return "" + ((int)numericValue);
        } else {
            return "" + numericValue;
        }
    }

    public static Long readCellAsDateOrNull(Row row, int columnIndex) {
        Cell cell = getCell(row, columnIndex);
        if (cell == null){
            return null;
        }
        Date date = cell.getDateCellValue();
        if (date == null){
            return null;
        }
        return date.getTime();
    }

    public static Integer readCellAsIntOrNull(Row row, int columnIndex) {
        Cell cell = getCell(row, columnIndex);
        if (cell == null){
            return null;
        }
        if (cell.getCellType() == CellType.BLANK){
            return null;
        }

        return (int)cell.getNumericCellValue();
    }

    public static boolean readCellAsBoolean(Row row, int columnIndex, boolean defaultValue) {
        Cell cell = getCell(row, columnIndex);
        if (cell == null){
            return defaultValue;
        }
        try{
            return cell.getBooleanCellValue();
        } catch (Throwable e){
            return defaultValue;
        }
    }
}
