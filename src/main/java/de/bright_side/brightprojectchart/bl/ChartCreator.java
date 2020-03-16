package de.bright_side.brightprojectchart.bl;

import de.bright_side.brightprojectchart.logic.ProjectPlanLogic;
import de.bright_side.brightprojectchart.model.*;
import de.bright_side.brightprojectchart.model.ProjectPlan.ChartSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;

public class ChartCreator {
    private static final Logger LOGGER = LogManager.getLogger(ChartCreator.class);
    private static final String SAMPLE_TEXT_TO_DETERMINE_HEIGHT = "THE QUICK FOX JUMPS OVER THE LAZY DOG, the quick fox jumps over the lazy dog. ÄÖÜß";

    private ProjectPlanLogic projectPlanLogic = new ProjectPlanLogic();

    public void createChart(File path, ProjectPlan plan) throws Exception {
        LOGGER.debug("createChart: project time span: " + dateToString(plan.getShownDateSpan().getStart()) + " - " + dateToString(plan.getShownDateSpan().getEnd()));

        int height = plan.getSections().size() * getIntSetting(ChartSetting.ROW_HEIGHT_IN_PX, plan);
        int width = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan) + projectPlanLogic.countDays(plan) * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan);

        height = addHeadingHeight(plan, height);

        LOGGER.debug("createChart: height = " + height);
        LOGGER.debug("createChart: width = " + width);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);

        int posY = 0;
        posY = drawHeadings(plan, width, g, posY);

        int headingsHeight = posY;
        for (Section i: plan.getSections()){
            drawSection(g, posY, width, i, plan.getShownDateSpan(), plan);
            posY += getIntSetting(ChartSetting.ROW_HEIGHT_IN_PX, plan);
        }

        drawLineAroundChartAreas(height, width, g, headingsHeight, plan);

        ImageIO.write(image, "png", path);
    }

    private int getIntSetting(ChartSetting setting, ProjectPlan plan){
        return ((Integer)plan.getChartSettings().get(setting)).intValue();
    }

    private ProjectColor getColorSetting(ChartSetting setting, ProjectPlan plan){
        return (ProjectColor)plan.getChartSettings().get(setting);
    }

    private String getStringSetting(ChartSetting setting, ProjectPlan plan){
        return (String)plan.getChartSettings().get(setting);
    }

    private int addHeadingHeight(ProjectPlan plan, int height) {
        int headerBarHeight = getIntSetting(ChartSetting.HEADER_BAR_HEIGHT, plan);
        if (plan.isShowYears()){
            height += headerBarHeight;
        }
        if (plan.isShowMonths()){
            height += headerBarHeight;
        }
        if (plan.isShowWeeks()){
            height += headerBarHeight;
        }
        return height;
    }

    private void drawLineAroundChartAreas(int height, int width, Graphics2D g, int headingsHeight, ProjectPlan plan) {
        g.setColor(toColor(getColorSetting(ChartSetting.AREAS_BORDER_COLOR, plan)));
        int labelsWidth = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan);
        g.drawRect(0, headingsHeight, labelsWidth, height - headingsHeight - 1); //: labels
        g.drawRect(labelsWidth, 0, width - labelsWidth - 1, headingsHeight); //: heading
        g.drawRect(labelsWidth, headingsHeight, width - labelsWidth - 1, height - headingsHeight - 1); //: plan items
    }

    private int drawHeadings(ProjectPlan plan, int width, Graphics2D g, int posY) {
        int headerBarHeight = getIntSetting(ChartSetting.HEADER_BAR_HEIGHT, plan);
        if (plan.isShowMonths()){
            drawYears(g, posY, width, plan.getShownDateSpan(), plan);
            posY += headerBarHeight;
        }
        if (plan.isShowMonths()){
            drawMonths(g, posY, width, plan.getShownDateSpan(), plan);
            posY += headerBarHeight;
        }
        if (plan.isShowWeeks()){
            drawWeeks(g, posY, width, plan.getShownDateSpan(), plan);
            posY += headerBarHeight;
        }
        return posY;
    }

    private String dateToString(long date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getMonthLabel(long month){
        return new SimpleDateFormat("MMM").format(month);
    }

    public String getYearLabel(long month){
        return new SimpleDateFormat("yyyy").format(month);
    }

    public String getWeekLabel(long month){
        return new SimpleDateFormat("ww").format(month);
    }

    private void drawYears(Graphics2D g, int top, int width, DateSpan projectDateSpan, ProjectPlan plan) {
        int posX = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan);
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
            long endOfYear = projectPlanLogic.getEndOfYear(currentDay);
            long endOfYearOrProject = Math.min(endOfYear, projectDateSpan.getEnd());
            int cellWidth = projectPlanLogic.subtractDays(endOfYearOrProject, currentDay) * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan);
            Font font = new Font(getStringSetting(ChartSetting.FONT_NAME, plan), Font.BOLD
                    , getIntSetting(ChartSetting.HEADER_BAR_REGULAR_TEXT_SIZE, plan));
            String text = getYearLabel(currentDay);

            drawLabelCell(g,
                    text,
                    top,
                    posX,
                    cellWidth,
                    getIntSetting(ChartSetting.HEADER_BAR_HEIGHT, plan),
                    font,
                    getColorSetting(ChartSetting.HEADER_TEXT_COLOR, plan),
                    getColorSetting(ChartSetting.HEADER_BACKGROUND_COLOR, plan),
                    getColorSetting(ChartSetting.SECTION_BORDER_COLOR, plan));

            LOGGER.debug("drawYears: currentDay =         " + dateToString(currentDay));
            currentDay = projectPlanLogic.getStartOfNextYear(currentDay);
            LOGGER.debug("drawYears: start of next year = " + dateToString(currentDay));
            posX += cellWidth;
        }
    }

    private void drawMonths(Graphics2D g, int top, int width, DateSpan projectDateSpan, ProjectPlan plan) {
        int posX = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan);
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
            long endOfMonth = projectPlanLogic.getEndOfMonth(currentDay);
            long endOfMonthOrProject = Math.min(endOfMonth, projectDateSpan.getEnd());
            int cellWidth = projectPlanLogic.subtractDays(endOfMonthOrProject, currentDay) * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan);
            Font font = new Font(getFontNameSetting(plan), Font.BOLD, getIntSetting(ChartSetting.HEADER_BAR_REGULAR_TEXT_SIZE, plan));
            String text = getMonthLabel(currentDay);

            drawLabelCell(g,
                    text,
                    top,
                    posX,
                    cellWidth,
                    getIntSetting(ChartSetting.HEADER_BAR_HEIGHT, plan),
                    font,
                    getColorSetting(ChartSetting.HEADER_TEXT_COLOR, plan),
                    getColorSetting(ChartSetting.HEADER_BACKGROUND_COLOR, plan),
                    getColorSetting(ChartSetting.SECTION_BORDER_COLOR, plan));

            currentDay = projectPlanLogic.getStartOfNextMonth(currentDay);
            posX += cellWidth;
        }
    }

    private String getFontNameSetting(ProjectPlan plan) {
        return getStringSetting(ChartSetting.FONT_NAME, plan);
    }

    private void drawWeeks(Graphics2D g, int top, int width, DateSpan projectDateSpan, ProjectPlan plan) {
        int posX = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan);
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
            long endOfWeek = projectPlanLogic.getEndOfWeek(currentDay);
            int cellWidth = projectPlanLogic.subtractDays(endOfWeek, currentDay) * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan);
            Font font = new Font(getStringSetting(ChartSetting.FONT_NAME, plan), Font.BOLD, getIntSetting(ChartSetting.HEADER_BAR_SMALL_TEXT_SIZE, plan));
            String text = getWeekLabel(currentDay);
            drawLabelCell(g,
                    text,
                    top,
                    posX,
                    cellWidth,
                    getIntSetting(ChartSetting.HEADER_BAR_HEIGHT, plan),
                    font,
                    getColorSetting(ChartSetting.HEADER_TEXT_COLOR, plan),
                    getColorSetting(ChartSetting.HEADER_BACKGROUND_COLOR, plan),
                    getColorSetting(ChartSetting.SECTION_BORDER_COLOR, plan));

            currentDay = projectPlanLogic.getStartOfNextWeek(currentDay);
            posX += cellWidth;
        }
    }

    private void drawLabelCell(Graphics2D g, String text, int top, int left, int width, int height, Font font, ProjectColor textColor, ProjectColor backgroundColor, ProjectColor borderColor) {
        //: draw background
        setColor(g, backgroundColor);
        g.fillRect(left, top, width, height);

        //: draw text
        setColor(g, textColor);
        g.setFont(font);
        int textWidth = getTextWidth(g, text);
        if (textWidth < width){
            drawCentered(g, text, top, left, width, height);
        }

        //: draw border
        setColor(g, borderColor);
        g.drawRect(left, top, width, height);
    }

    private void drawCentered(Graphics2D g, String text, int top, int left, int width, int height) {
        int textOffsetY = getTextOffsetY(g);
        int textHeight = getStandardTextHeight(g);
        int cellCenterX = left + (width / 2);
        int cellCenterY = top + (height / 2);
        g.drawString(text, cellCenterX - getTextWidth(g, text) / 2, cellCenterY - (textHeight / 2) + textOffsetY);
    }

    /**
     *
     * @return new posY
     */
    private void drawSection(Graphics2D g, int posY, int chartWidth, Section section, DateSpan projectDateSpan, ProjectPlan plan) throws Exception {
        setFont(g, section.getTextStyle(), plan);
        g.setColor(Color.BLACK);

        int textOffsetY = getTextOffsetY(g);

        //: draw section background
        setColor(g, section.getColor());
        g.fillRect(0, posY, chartWidth, getIntSetting(ChartSetting.ROW_HEIGHT_IN_PX, plan));


        //: draw section label
        setColor(g, getColorSetting(ChartSetting.SECTION_LABEL_COLOR, plan));
        g.drawString(section.getLabel(),
                getIntSetting(ChartSetting.TEXT_PADDING_LEFT, plan) + section.getTextStyle().getIndent(),
                posY + textOffsetY + getIntSetting(ChartSetting.TEXT_PADDING_TOP, plan));

        //: draw section bars
        if (!section.getPlanItems().isEmpty()) {
            for (PlanItem i : section.getPlanItems()) {
                drawPlanItem(g, i, posY, projectDateSpan, plan);
            }
        }

        //: draw border
//        setColor(g, SECTION_BORDER_COLOR);
//        g.drawRect(0, posY, chartWidth, ROW_HEIGHT_IN_PX);
//        g.drawRect(0, posY, LABELS_WIDTH_IN_PX, ROW_HEIGHT_IN_PX);

        return;
    }

    private void setFont(Graphics2D g, TextStyle textStyle, ProjectPlan plan) {
        int fontStyle = Font.PLAIN;
        if (textStyle.isBold()) {
            fontStyle = Font.BOLD;
        } else if (textStyle.isItalic()){
            fontStyle = Font.ITALIC;
        }
        Font font = new Font(getStringSetting(ChartSetting.FONT_NAME, plan), fontStyle, textStyle.getTextSize());
        g.setFont(font);
    }

    private int getTextOffsetY(Graphics2D g) {
        return (int) (g.getFontMetrics().getStringBounds(SAMPLE_TEXT_TO_DETERMINE_HEIGHT, g).getHeight() * 0.80);
    }

    private int getStandardTextHeight(Graphics2D g) {
        return getTextOffsetY(g);
    }


    private int getTextWidth(Graphics2D g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }

    private void drawPlanItem(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan, ProjectPlan plan) throws Exception {
        //: bar starts after project ends
        if (projectPlanLogic.subtractDays(projectDateSpan.getEnd(), planItem.getDateSpan().getStart()) <= 0){
            return;
        }
        //: bar ends after project starts
        if (projectPlanLogic.subtractDays(projectDateSpan.getStart(), planItem.getDateSpan().getEnd()) >= 0){
            return;
        }

        setColor(g, planItem.getColor());
        switch (planItem.getType()){
            case BAR:
                drawBar(g, planItem, posY, projectDateSpan, plan);
                break;
            case MILESTONE:
                drawMilestone(g, planItem, posY, projectDateSpan, plan);
                break;
            default:
                throw new Exception("Unexpected plan item type: " + planItem.getType());
        }
    }

    private void drawBar(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan, ProjectPlan plan) throws Exception {
        int barStartDay = projectPlanLogic.subtractDays(planItem.getDateSpan().getStart(), projectDateSpan.getStart());
        int barEndDay = projectPlanLogic.subtractDays(planItem.getDateSpan().getEnd(), projectDateSpan.getStart());
        barStartDay = putInProjectRange(barStartDay, projectDateSpan);
        barEndDay = putInProjectRange(barEndDay, projectDateSpan);

        int barLength = barEndDay - barStartDay;
        if (barLength <= 0){
            return;
        }
        int barPadding = (getIntSetting(ChartSetting.ROW_HEIGHT_IN_PX, plan) - getIntSetting(ChartSetting.BAR_HEIGHT_IN_PX, plan)) / 2;
        int shapeLeft = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan) + (barStartDay * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan));
        int shapeWidth = barLength * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan);
        Rectangle shape = new Rectangle(shapeLeft, posY + barPadding, shapeWidth, getIntSetting(ChartSetting.BAR_HEIGHT_IN_PX, plan));
//        g.fillRect(shapeLeft, posY + barPadding, shapeWidth, BAR_HEIGHT_IN_PX);
        g.fill(shape);


        g.setColor(toColor(getColorSetting(ChartSetting.PLAN_ITEM_LABEL_COLOR, plan)));
        drawCharItemLabel(g, shape, planItem, TextStyle.TextPos.CENTER, plan);
    }

    private void drawCharItemLabel(Graphics2D g,
                                   Rectangle shape,
                                   PlanItem planItem,
                                   TextStyle.TextPos defaultTextPos,
                                   ProjectPlan plan) throws Exception {
        String label = planItem.getLabel();
        if ((label == null) || (label.isEmpty())){
            LOGGER.debug("drawCharItemLabel: label is null");
            return;
        }
        LOGGER.debug("drawCharItemLabel: shape: " + shape);


        setFont(g, planItem.getTextStyle(), plan);
//        g.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        int textWidth = getTextWidth(g, label);
        int textOffsetY = getTextOffsetY(g);
        int textHeight = getStandardTextHeight(g);

        int posX = 0;
        TextStyle.TextPos textPos = defaultTextPos;
        if (planItem.getTextStyle().getTextPos() != null){
            textPos = planItem.getTextStyle().getTextPos();
        }

        int padding = 5;

        switch (textPos) {
            case BEFORE:
                posX = shape.x - textWidth + padding;
                break;
            case BEGINNING:
                posX = shape.x + padding;
                break;
            case CENTER:
                posX = shape.x + (shape.width / 2) - (textWidth / 2);
                break;
            case END:
                posX = shape.x + shape.width - textWidth - padding;
                break;
            case AFTER:
                posX = shape.x + shape.width + padding;
                break;
            default:
                throw new Exception("Unexpected text pos: " + textPos);
        }
//        g.setColor(Color.RED);
//        g.draw(shape);
        g.drawString(label, posX, shape.y + (shape.height / 2) - (textHeight / 2) + textOffsetY);
//        g.drawString("Hello!!!!", shape.x, shape.y + textOffsetY);
    }

    private void drawMilestone(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan, ProjectPlan plan) throws Exception {
        long symbolTime = planItem.getDateSpan().getStart();
        if ((symbolTime < projectDateSpan.getStart()) || (symbolTime > projectDateSpan.getEnd())){
            return;
        }

        int dayIndex = projectPlanLogic.subtractDays(planItem.getDateSpan().getStart(), projectDateSpan.getStart());
        dayIndex = putInProjectRange(dayIndex, projectDateSpan);


        int posX = getIntSetting(ChartSetting.LABELS_WIDTH_IN_PX, plan) + (dayIndex * getIntSetting(ChartSetting.DAY_WIDTH_IN_PX, plan));
//        g.fillRect(posX, posY, DAY_WIDTH_IN_PX, BAR_HEIGHT_IN_PX);

        int paddingY = (getIntSetting(ChartSetting.ROW_HEIGHT_IN_PX, plan) - getIntSetting(ChartSetting.BAR_HEIGHT_IN_PX, plan)) / 2;
        Path2D.Double shape = getMilestoneShape(posX, posY + paddingY, getIntSetting(ChartSetting.BAR_HEIGHT_IN_PX, plan));
        g.fill(shape);

        g.setColor(toColor(getColorSetting(ChartSetting.PLAN_ITEM_LABEL_COLOR, plan)));
        TextStyle.TextPos defaultTextPos = TextStyle.TextPos.AFTER;
        if (planItem.getDateSpan().getStart() >= projectDateSpan.getEnd()){
            defaultTextPos = TextStyle.TextPos.BEFORE;
        }
        drawCharItemLabel(g, toRectangle(shape.getBounds()), planItem, defaultTextPos, plan);
    }

    private Rectangle toRectangle(Rectangle bounds) {
        return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private Path2D.Double getMilestoneShape(int left, int top, double size) {
        double width = size;
        double height = size;
        Path2D.Double result = new Path2D.Double();
        result.moveTo(left, top + (height / 2));
        result.lineTo(left + (width / 2), top);
        result.lineTo(left + width, top + (height / 2));
        result.lineTo(left + (width / 2), top + height);
        result.closePath();
        return result;
    }

    private int putInProjectRange(int dayIndex, DateSpan projectDateSpan) {
        int numberOfDays = projectPlanLogic.subtractDays(projectDateSpan.getEnd(), projectDateSpan.getStart());

        int result = dayIndex;
        if (result < 0){
            result = 0;
        }
        if (result > numberOfDays){
            result = numberOfDays;
        }

        return result;
    }

    private Color toColor(ProjectColor color){
        Color result = Color.BLACK;
        if (color != null){
            result = new Color(color.getRed(), color.getGreen(), color.getBlue());
        }
        return result;
    }

    private void setColor(Graphics2D g, ProjectColor color) {
        g.setColor(toColor(color));
    }


}
