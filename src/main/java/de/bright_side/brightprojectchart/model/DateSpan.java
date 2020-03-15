package de.bright_side.brightprojectchart.model;

public class DateSpan {
    private long start;
    private long end;

    public DateSpan(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
