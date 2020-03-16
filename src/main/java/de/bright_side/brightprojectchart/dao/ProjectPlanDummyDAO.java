package de.bright_side.brightprojectchart.dao;

import de.bright_side.brightprojectchart.model.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProjectPlanDummyDAO {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final ProjectColor DUMMY_COLOR_1 = new ProjectColor(195, 207, 225);
    private static final ProjectColor DUMMY_COLOR_2 = new ProjectColor(255, 255, 255);
    private static final ProjectColor DUMMY_COLOR_3 = new ProjectColor(240, 240, 240);
    private static final ProjectColor DUMMY_COLOR_4 = new ProjectColor(200, 200, 140);
    private static final int DUMMY_TEXT_SIZE_REGULAR = 14;
    private static final int DUMMY_TEXT_SIZE_HEADING = 18;


    public ProjectPlan createDummyProjectPlan() throws Exception {
        ProjectPlan result = new ProjectPlan();

        result.setShowYears(false);
        result.setShowMonths(true);
        result.setShowWeeks(true);

        result.setSections(new ArrayList<>());
        result.setShownDateSpan(new DateSpan(toDate("2020-01-01"), toDate("2020-12-31")));

        result.getSections().add(createDummySection("My Section 1", true, DUMMY_COLOR_1, null, null, null, null));
        result.getSections().add(createDummySection("My Section 1.1", false, DUMMY_COLOR_2, "2020-03-01", "2020-08-01", "2020-10-01", "2020-11-20"));
        result.getSections().add(createDummySection("My Section 1.2", false, DUMMY_COLOR_3, "2020-02-01", "2020-03-01", "2020-05-01", "2020-06-01"));
        result.getSections().add(createDummySection("My Section 1.3", false, DUMMY_COLOR_2, "2020-03-01", "2020-04-01", "2020-06-01", "2020-07-01"));
        result.getSections().add(createDummySection("My Section 1.4", false, DUMMY_COLOR_3, "2020-04-01", "2020-05-01", "2020-07-01", "2020-08-01"));
        result.getSections().add(createDummySection("My Section A", true, DUMMY_COLOR_1, null, null, null, null));
        result.getSections().add(createDummySection("My Section A.1", false, DUMMY_COLOR_2, "2020-03-01", "2020-08-01", "2020-10-01", "2020-11-20"));
        result.getSections().add(createDummySection("My Section A.2", false, DUMMY_COLOR_3, "2020-02-01", "2020-03-01", "2020-05-01", "2020-06-01"));

        return result;
    }

    private long toDate(String text) throws Exception {
        if (text == null){
            return 0;
        }
        return DATE_FORMAT.parse(text).getTime();
    }

    private Section createDummySection(String label, boolean heading, ProjectColor color, String activity1StartText, String activity1EndText, String activity2StartText, String activity2EndText) throws Exception {
        return createDummySection(label, heading, color, toDate(activity1StartText), toDate(activity1EndText), toDate(activity2StartText), toDate(activity2EndText));

    }

    private Section createDummySection(String label,
                                       boolean heading,
                                       ProjectColor color,
                                       long activity1Start,
                                       long activity1End,
                                       long activity2Start,
                                       long activity2End){
        Section result = new Section();
        result.setColor(color);
        result.setLabel(label);
        if (heading) {
            result.setTextStyle(new TextStyle(DUMMY_TEXT_SIZE_HEADING, true, false, 0, null));
        } else {
            result.setTextStyle(new TextStyle(DUMMY_TEXT_SIZE_REGULAR, false, false, 20, null));
        }

        result.setPlanItems(new ArrayList<>());
        if ((activity1Start != 0) && (activity1End != 0)){
            result.getPlanItems().add(createDummyBar(activity1Start, activity1End));
        }
        if ((activity2Start != 0) && (activity2End != 0)){
            result.getPlanItems().add(createDummyBar(activity2Start, activity2End));
        }

        return result;
    }

    private PlanItem createDummyBar(long start, long end) {
        PlanItem result = new PlanItem();
        result.setColor(new ProjectColor(76, 108, 156));
        result.setDateSpan(new DateSpan(start, end));
        return result;
    }

}
