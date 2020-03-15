package de.bright_side.brightprojectchart.bl;

import de.bright_side.brightprojectchart.dao.ProjectPlanExcelDAO;
import de.bright_side.brightprojectchart.logic.ProjectPlanLogic;
import de.bright_side.brightprojectchart.model.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;

public class ChartCreator {
    private static final Logger LOGGER = LogManager.getLogger(ChartCreator.class);

    private static final ProjectColor CHART_BACKGROUND_COLOR = new ProjectColor(255, 255, 255);
    private static final int BAR_HEIGHT_IN_PX = 14;
    private static final int ROW_HEIGHT_IN_PX = 24;
    private static final int DAY_WIDTH_IN_PX = 3;
    private static final int LABELS_WIDTH_IN_PX = 150;
    private static final int TEXT_PADDING_TOP = 3;
    private static final int TEXT_PADDING_LEFT = 5;
    private static final ProjectColor SECTION_BORDER_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor AREAS_BORDER_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor SECTION_LABEL_COLOR = new ProjectColor(0, 0, 0);
    private static final String SAMPLE_TEXT_TO_DETERMINE_HEIGHT = "THE QUICK FOX JUMPS OVER THE LAZY DOG, the quick fox jumps over the lazy dog. ÄÖÜß";
    private static final int HEADER_BAR_HEIGHT = 20;
    private static final String FONT_NAME = "Arial";
    private static final ProjectColor HEADER_BACKGROUND_COLOR = new ProjectColor(255, 255, 255);
    private static final int HEADER_BAR_REGULAR_TEXT_SIZE = 12;
    private static final int HEADER_BAR_SMALL_TEXT_SIZE = 10;
    private static final ProjectColor HEADER_TEXT_COLOR = new ProjectColor(0, 0, 0);
    private static final ProjectColor PLAN_ITEM_LABEL_COLOR = new ProjectColor(0, 0, 0);

    private ProjectPlanLogic projectPlanLogic = new ProjectPlanLogic();

    public void createChart(File path, ProjectPlan projectPlan) throws Exception {
        LOGGER.debug("createChart: project time span: " + dateToString(projectPlan.getDateSpan().getStart()) + " - " + dateToString(projectPlan.getDateSpan().getEnd()));

        int height = projectPlan.getSections().size() * ROW_HEIGHT_IN_PX;
        int width = LABELS_WIDTH_IN_PX + projectPlanLogic.countDays(projectPlan) * DAY_WIDTH_IN_PX;

        height = addHeadingHeight(projectPlan, height);

        LOGGER.debug("createChart: height = " + height);
        LOGGER.debug("createChart: width = " + width);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);

        int posY = 0;
        posY = drawHeadings(projectPlan, width, g, posY);

        int headingsHeight = posY;
        for (Section i: projectPlan.getSections()){
            drawSection(g, posY, width, i, projectPlan.getDateSpan());
            posY += ROW_HEIGHT_IN_PX;
        }

        drawLineAroundChartAreas(height, width, g, headingsHeight);

        ImageIO.write(image, "png", path);
    }

    private int addHeadingHeight(ProjectPlan projectPlan, int height) {
        if (projectPlan.isShowYears()){
            height += HEADER_BAR_HEIGHT;
        }
        if (projectPlan.isShowMonths()){
            height += HEADER_BAR_HEIGHT;
        }
        if (projectPlan.isShowWeeks()){
            height += HEADER_BAR_HEIGHT;
        }
        return height;
    }

    private void drawLineAroundChartAreas(int height, int width, Graphics2D g, int headingsHeight) {
        g.setColor(toColor(AREAS_BORDER_COLOR));
        g.drawRect(0, headingsHeight, LABELS_WIDTH_IN_PX, height - headingsHeight - 1); //: labels
        g.drawRect(LABELS_WIDTH_IN_PX, 0, width - LABELS_WIDTH_IN_PX - 1, headingsHeight); //: heading
        g.drawRect(LABELS_WIDTH_IN_PX, headingsHeight, width - LABELS_WIDTH_IN_PX - 1, height - headingsHeight - 1); //: plan items
    }

    private int drawHeadings(ProjectPlan projectPlan, int width, Graphics2D g, int posY) {
        if (projectPlan.isShowMonths()){
            drawYears(g, posY, width, projectPlan.getDateSpan());
            posY += HEADER_BAR_HEIGHT;
        }
        if (projectPlan.isShowMonths()){
            drawMonths(g, posY, width, projectPlan.getDateSpan());
            posY += HEADER_BAR_HEIGHT;
        }
        if (projectPlan.isShowWeeks()){
            drawWeeks(g, posY, width, projectPlan.getDateSpan());
            posY += HEADER_BAR_HEIGHT;
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

    private void drawYears(Graphics2D g, int top, int width, DateSpan projectDateSpan) {
        int posX = LABELS_WIDTH_IN_PX;
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
            long endOfYear = projectPlanLogic.getEndOfYear(currentDay);
            long endOfYearOrProject = Math.min(endOfYear, projectDateSpan.getEnd());
            int cellWidth = projectPlanLogic.subtractDays(endOfYearOrProject, currentDay) * DAY_WIDTH_IN_PX;
            Font font = new Font(FONT_NAME, Font.BOLD, HEADER_BAR_REGULAR_TEXT_SIZE);
            String text = getYearLabel(currentDay);

            drawLabelCell(g, text, top, posX, cellWidth, HEADER_BAR_HEIGHT, font, HEADER_TEXT_COLOR, HEADER_BACKGROUND_COLOR, SECTION_BORDER_COLOR);

            LOGGER.debug("drawYears: currentDay =         " + dateToString(currentDay));
            currentDay = projectPlanLogic.getStartOfNextYear(currentDay);
            LOGGER.debug("drawYears: start of next year = " + dateToString(currentDay));
            posX += cellWidth;
        }
    }

    private void drawMonths(Graphics2D g, int top, int width, DateSpan projectDateSpan) {
        int posX = LABELS_WIDTH_IN_PX;
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
            long endOfMonth = projectPlanLogic.getEndOfMonth(currentDay);
            long endOfMonthOrProject = Math.min(endOfMonth, projectDateSpan.getEnd());
            int cellWidth = projectPlanLogic.subtractDays(endOfMonthOrProject, currentDay) * DAY_WIDTH_IN_PX;
            Font font = new Font(FONT_NAME, Font.BOLD, HEADER_BAR_REGULAR_TEXT_SIZE);
            String text = getMonthLabel(currentDay);

            drawLabelCell(g, text, top, posX, cellWidth, HEADER_BAR_HEIGHT, font, HEADER_TEXT_COLOR, HEADER_BACKGROUND_COLOR, SECTION_BORDER_COLOR);

            currentDay = projectPlanLogic.getStartOfNextMonth(currentDay);
            posX += cellWidth;
        }
    }

    private void drawWeeks(Graphics2D g, int top, int width, DateSpan projectDateSpan) {
        int posX = LABELS_WIDTH_IN_PX;
        long currentDay = projectDateSpan.getStart();

        while (currentDay < projectDateSpan.getEnd()){
//            log("drawWeeks: current day =        " + new SimpleDateFormat("ddd, yyyy-MM-dd HH:mm").format(currentDay));
//            log("drawWeeks: start of next week = " + new SimpleDateFormat("ddd, yyyy-MM-dd HH:mm").format(projectPlanLogic.getStartOfNextWeek(currentDay)));
//            log("drawWeeks: endOfWeek   =        " + new SimpleDateFormat("ddd, yyyy-MM-dd HH:mm").format(projectPlanLogic.getEndOfWeek(currentDay)));
            long endOfWeek = projectPlanLogic.getEndOfWeek(currentDay);
            int cellWidth = projectPlanLogic.subtractDays(endOfWeek, currentDay) * DAY_WIDTH_IN_PX;
//            boolean cellFullyVisible = (currentDay <= projectDateSpan.getStart()) || (endOfWeek <= projectDateSpan.getEnd());
//            if (cellFullyVisible){
            Font font = new Font(FONT_NAME, Font.BOLD, HEADER_BAR_SMALL_TEXT_SIZE);
            String text = getWeekLabel(currentDay);
            drawLabelCell(g, text, top, posX, cellWidth, HEADER_BAR_HEIGHT, font, HEADER_TEXT_COLOR, HEADER_BACKGROUND_COLOR, SECTION_BORDER_COLOR);

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
    private void drawSection(Graphics2D g, int posY, int chartWidth, Section section, DateSpan projectDateSpan) throws Exception {
        setFont(g, section.getTextStyle());
        g.setColor(Color.BLACK);

        int textOffsetY = getTextOffsetY(g);

        //: draw section background
        setColor(g, section.getColor());
        g.fillRect(0, posY, chartWidth, ROW_HEIGHT_IN_PX);


        //: draw section label
        setColor(g, SECTION_LABEL_COLOR);
        g.drawString(section.getLabel(), TEXT_PADDING_LEFT + section.getTextStyle().getIndent(), posY + textOffsetY + TEXT_PADDING_TOP);

        //: draw section bars
        if (!section.getPlanItems().isEmpty()) {
            for (PlanItem i : section.getPlanItems()) {
                drawPlanItem(g, i, posY, projectDateSpan);
            }
        }

        //: draw border
//        setColor(g, SECTION_BORDER_COLOR);
//        g.drawRect(0, posY, chartWidth, ROW_HEIGHT_IN_PX);
//        g.drawRect(0, posY, LABELS_WIDTH_IN_PX, ROW_HEIGHT_IN_PX);

        return;
    }

    private void setFont(Graphics2D g, TextStyle textStyle) {
        int fontStyle = Font.PLAIN;
        if (textStyle.isBold()) {
            fontStyle = Font.BOLD;
        } else if (textStyle.isItalic()){
            fontStyle = Font.ITALIC;
        }
        Font font = new Font(FONT_NAME, fontStyle, textStyle.getTextSize());
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

    private void drawPlanItem(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan) throws Exception {
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
                drawBar(g, planItem, posY, projectDateSpan);
                break;
            case MILESTONE:
                drawMilestone(g, planItem, posY, projectDateSpan);
                break;
            default:
                throw new Exception("Unexpected plan item type: " + planItem.getType());
        }
    }

    private void drawBar(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan) throws Exception {
        int barStartDay = projectPlanLogic.subtractDays(planItem.getDateSpan().getStart(), projectDateSpan.getStart());
        int barEndDay = projectPlanLogic.subtractDays(planItem.getDateSpan().getEnd(), projectDateSpan.getStart());
        barStartDay = putInProjectRange(barStartDay, projectDateSpan);
        barEndDay = putInProjectRange(barEndDay, projectDateSpan);

        int barLength = barEndDay - barStartDay;
        if (barLength <= 0){
            return;
        }
        int barPadding = (ROW_HEIGHT_IN_PX - BAR_HEIGHT_IN_PX) / 2;
        int shapeLeft = LABELS_WIDTH_IN_PX + (barStartDay * DAY_WIDTH_IN_PX);
        int shapeWidth = barLength * DAY_WIDTH_IN_PX;
        Rectangle shape = new Rectangle(shapeLeft, posY + barPadding, shapeWidth, BAR_HEIGHT_IN_PX);
//        g.fillRect(shapeLeft, posY + barPadding, shapeWidth, BAR_HEIGHT_IN_PX);
        g.fill(shape);


        g.setColor(toColor(PLAN_ITEM_LABEL_COLOR));
        drawCharItemLabel(g, shape, planItem, TextStyle.TextPos.CENTER);
    }

    private void drawCharItemLabel(Graphics2D g,
                                   Rectangle shape,
                                   PlanItem planItem,
                                   TextStyle.TextPos defaultTextPos) throws Exception {
        String label = planItem.getLabel();
        if ((label == null) || (label.isEmpty())){
            LOGGER.debug("drawCharItemLabel: label is null");
            return;
        }
        LOGGER.debug("drawCharItemLabel: shape: " + shape);


        setFont(g, planItem.getTextStyle());
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

    private void drawMilestone(Graphics2D g, PlanItem planItem, int posY, DateSpan projectDateSpan) throws Exception {
        long symbolTime = planItem.getDateSpan().getStart();
        if ((symbolTime < projectDateSpan.getStart()) || (symbolTime > projectDateSpan.getEnd())){
            return;
        }

        int dayIndex = projectPlanLogic.subtractDays(planItem.getDateSpan().getStart(), projectDateSpan.getStart());
        dayIndex = putInProjectRange(dayIndex, projectDateSpan);


        int posX = LABELS_WIDTH_IN_PX + (dayIndex * DAY_WIDTH_IN_PX);
//        g.fillRect(posX, posY, DAY_WIDTH_IN_PX, BAR_HEIGHT_IN_PX);

        int paddingY = (ROW_HEIGHT_IN_PX - BAR_HEIGHT_IN_PX) / 2;
        Path2D.Double shape = getMilestoneShape(posX, posY + paddingY, BAR_HEIGHT_IN_PX);
        g.fill(shape);

        g.setColor(toColor(PLAN_ITEM_LABEL_COLOR));
        TextStyle.TextPos defaultTextPos = TextStyle.TextPos.AFTER;
        if (planItem.getDateSpan().getStart() >= projectDateSpan.getEnd()){
            defaultTextPos = TextStyle.TextPos.BEFORE;
        }
        drawCharItemLabel(g, toRectangle(shape.getBounds()), planItem, defaultTextPos);
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
