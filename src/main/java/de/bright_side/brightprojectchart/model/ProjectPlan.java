package de.bright_side.brightprojectchart.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProjectPlan {
    public enum ChartSetting{BAR_HEIGHT_IN_PX, ROW_HEIGHT_IN_PX, DAY_WIDTH_IN_PX, LABELS_WIDTH_IN_PX, TEXT_PADDING_TOP
        , TEXT_PADDING_LEFT, SECTION_BORDER_COLOR, AREAS_BORDER_COLOR, SECTION_LABEL_COLOR, HEADER_BAR_HEIGHT
        , FONT_NAME, HEADER_BACKGROUND_COLOR, HEADER_BAR_REGULAR_TEXT_SIZE, HEADER_BAR_SMALL_TEXT_SIZE
        , HEADER_TEXT_COLOR, PLAN_ITEM_LABEL_COLOR, CHART_START_DATE, CHART_END_DATE}

    private List<String> errors;
    private DateSpan shownDateSpan;
    private List<Section> sections;
    private boolean showYears;
    private boolean showMonths;
    private boolean showWeeks;
    private EnumMap<ChartSetting, Object> chartSettings;



    public Map<ChartSetting, Object> getChartSettings() {
        return chartSettings;
    }

    public void setChartSettings(EnumMap<ChartSetting, Object> chartSettings) {
        this.chartSettings = chartSettings;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public DateSpan getShownDateSpan() {
        return shownDateSpan;
    }

    public void setShownDateSpan(DateSpan shownDateSpan) {
        this.shownDateSpan = shownDateSpan;
    }

    public boolean isShowYears() {
        return showYears;
    }

    public void setShowYears(boolean showYears) {
        this.showYears = showYears;
    }

    public boolean isShowMonths() {
        return showMonths;
    }

    public void setShowMonths(boolean showMonths) {
        this.showMonths = showMonths;
    }

    public boolean isShowWeeks() {
        return showWeeks;
    }

    public void setShowWeeks(boolean showWeeks) {
        this.showWeeks = showWeeks;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
