package de.bright_side.brightprojectchart.model;

import java.util.List;

public class ProjectPlan {
    private List<String> errors;
    private DateSpan dateSpan;
    private List<Section> sections;
    private boolean showYears;
    private boolean showMonths;
    private boolean showWeeks;

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public DateSpan getDateSpan() {
        return dateSpan;
    }

    public void setDateSpan(DateSpan dateSpan) {
        this.dateSpan = dateSpan;
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
