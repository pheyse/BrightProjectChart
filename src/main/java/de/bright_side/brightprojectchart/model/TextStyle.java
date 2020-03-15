package de.bright_side.brightprojectchart.model;

public class TextStyle {

    public enum TextPos {BEFORE, BEGINNING, CENTER, END, AFTER}

    private boolean bold;
    private boolean italic;
    private int textSize;
    private int indent;
    private TextPos textPos;

    public TextStyle(int textSize, boolean bold, boolean italic, int indent, TextPos textPos) {
        this.textSize = textSize;
        this.bold = bold;
        this.italic = italic;
        this.indent = indent;
        this.textPos = textPos;
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

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public TextPos getTextPos() {
        return textPos;
    }

    public void setTextPos(TextPos textPos) {
        this.textPos = textPos;
    }

    @Override
    public String toString() {
        return "TextStyle{" +
                "bold=" + bold +
                ", italic=" + italic +
                ", textSize=" + textSize +
                ", indent=" + indent +
                ", textPos=" + textPos +
                '}';
    }
}
