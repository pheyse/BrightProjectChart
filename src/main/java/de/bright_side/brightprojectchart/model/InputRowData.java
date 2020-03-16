package de.bright_side.brightprojectchart.model;

public class InputRowData {
    public enum RowType {SETTING, SECTION, BAR, MILESTONE}

    private RowType rowType;
    private String setting;
    private String value;
    private String label;
    private Long start;
    private Long end;
    private ProjectColor color;
    private Integer textSize;
    private TextStyle.TextPos textPos;
    private boolean bold;
    private boolean italic;
    private Integer indent;

    public RowType getRowType() {
        return rowType;
    }

    public void setRowType(RowType rowType) {
        this.rowType = rowType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public ProjectColor getColor() {
        return color;
    }

    public void setColor(ProjectColor color) {
        this.color = color;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }
    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public Integer getIndent() {
        return indent;
    }

    public void setIndent(Integer indent) {
        this.indent = indent;
    }

    public TextStyle.TextPos getTextPos() {
        return textPos;
    }

    public void setTextPos(TextStyle.TextPos textPos) {
        this.textPos = textPos;
    }

}
