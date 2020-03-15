package de.bright_side.brightprojectchart.logic;

import de.bright_side.brightprojectchart.model.ProjectPlan;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProjectPlanLogic {
//    public int countRows(ProjectPlan projectPlan){
//        int result = 0;
//        for (Section section: projectPlan.getSections()){
//            int activities = section.getPlanItems().size();
//            result += Math.max(1, activities);
//        }
//        return result;
//    }

    public int subtractDays(long a, long b){
        return countDays(b, a);
    }

    public int countDays(long startDate, long endDate){
        int result = 0;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(startDate);

        if (calendar.getTimeInMillis() == endDate) {
            return 0;
        }

        if (calendar.getTimeInMillis() < endDate) {
            while (calendar.getTimeInMillis() < endDate) {
                result++;
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
        } else  {
            while (calendar.getTimeInMillis() > endDate) {
                result--;
                calendar.add(Calendar.DAY_OF_WEEK, -1);
            }
        }

        return result;
    }

    public int countDays(ProjectPlan projectPlan) {
        return countDays(projectPlan.getDateSpan().getStart(), projectPlan.getDateSpan().getEnd());
    }

    public long getEndOfMonth(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(getStartOfNextMonth(startDate));
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTimeInMillis();
    }

    public long getEndOfYear(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(getStartOfNextYear(startDate));
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTimeInMillis();
    }

    public long getStartOfNextMonth(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(startDate);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.MONTH, 1);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public long getStartOfNextYear(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(startDate);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.YEAR, 1);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public long getEndOfWeek(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(getStartOfNextWeek(startDate));
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTimeInMillis();
    }

    public long getStartOfNextWeek(long startDate){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(startDate);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

}
