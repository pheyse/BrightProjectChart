package de.bright_side.brightprojectchart.model;

public class PlanItem {
    public enum PlanItemType{BAR, MILESTONE}

    private PlanItemType type;
    private DateSpan dateSpan;
    private ProjectColor color;
    private String label;
    private TextStyle textStyle;

    public ProjectColor getColor() {
        return color;
    }

    public void setColor(ProjectColor color) {
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DateSpan getDateSpan() {
        return dateSpan;
    }

    public void setDateSpan(DateSpan dateSpan) {
        this.dateSpan = dateSpan;
    }

    public PlanItemType getType() {
        return type;
    }

    public void setType(PlanItemType type) {
        this.type = type;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

}
