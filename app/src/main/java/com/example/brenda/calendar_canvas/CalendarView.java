package com.example.brenda.calendar_canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brenda on 04/04/2018.
 */

public class CalendarView extends View {

    Paint currentDayPaint, highlightPaint, taskPaint, meetingPaint, paint;
    TextPaint textDatePaint;
    String daysLabel[] = {"S", "M", "T", "W", "T", "F", "S"};
    List<String> dateList = new ArrayList<String>();
    List<String> dateStats = new ArrayList<String>();
    List<String> eventList = new ArrayList<String>();
    final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
    int height, width, dataCol, dataRow,i, selectedMonth, selectedYear;
    Float columnWidth,columnLeft,columnRight,bottom,top,spacing,
            eventSpace, totalGridSpacing;
    Double columnHeight;
    String dateBounds[];
    Boolean highlight = false;
    int highlightPos[];
    String monthString,yearString, text;
    Calendar calendar_obj;

    public CalendarView(Context context){
        super(context);
    }
    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public String getYearString() {
        return yearString;
    }

    public String getMonthString() {
        return monthString;
    }
    public void setDateList(List<String> dateList) {
        this.dateList = dateList;

    }

    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    public void setDateStats(List<String> dateStats) {
        this.dateStats = dateStats;
    }

    public CalendarView(Context context, int selectedMonth, int selectedYear){
        super(context);

        calendar_obj = new Calendar();
        currentDayPaint = new Paint();
        currentDayPaint.setStyle(Paint.Style.FILL);
        currentDayPaint.setColor(Color.CYAN);

        textDatePaint = new TextPaint();
        textDatePaint.setColor(Color.DKGRAY);
        textDatePaint.setTextSize(60);
        textDatePaint.setTextAlign(Paint.Align.CENTER);

        highlightPaint = new Paint();
        highlightPaint.setColor(Color.LTGRAY);
        highlightPaint.setStyle(Paint.Style.FILL);

        taskPaint = new Paint();
        taskPaint.setStyle(Paint.Style.FILL);
        taskPaint.setColor(Color.MAGENTA);

        meetingPaint = new Paint();
        meetingPaint.setStyle(Paint.Style.FILL);
        meetingPaint.setColor(Color.BLUE);

        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
        monthString = months[selectedMonth].toString();
        yearString = String.valueOf(selectedYear);
        setYearString(yearString);
        setMonthString(monthString);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        height = this.getContext().getResources().getDisplayMetrics().heightPixels;
        width = this.getContext().getResources().getDisplayMetrics().widthPixels;

    }

    public void onDraw(Canvas canvas){

        clearCanvas(canvas);
        if(highlight){
            RectF recHighlight = new RectF(highlightPos[0], highlightPos[1], highlightPos[2], highlightPos[3]);
            canvas.drawRoundRect(recHighlight,pixelsFromDp(10.0).floatValue(),pixelsFromDp(10.0).floatValue(),highlightPaint);
            highlight = false;
        }
        // draw dates
        drawDates(canvas);

        //draw event indicator
        drawEventIndicator(canvas);

    }

    public void drawDates(Canvas canvas){

        dateBounds = new String[dateList.size()];
        final float margin = pixelsFromDp(10).floatValue();
        final Double gridTop = pixelsFromDp(0);
        final float gridLeft = margin;
        final float gridRight = width-margin;
        final Double gridBottom = gridTop+height*.09;
        i = 0;
        spacing = pixelsFromDp(1).floatValue();
        dataCol = 7;
        eventSpace = pixelsFromDp(1).floatValue();
        dataRow = dateList.size()/dataCol+1;
        totalGridSpacing = spacing * (dataCol + 1);
        columnWidth = (gridRight - gridLeft - totalGridSpacing) / dataCol;
        columnHeight = (gridBottom.floatValue() - gridTop - margin) ;
        columnLeft = gridLeft + spacing;
        columnRight = columnLeft + columnWidth;
        top = gridTop.floatValue() + spacing;
        bottom = top + columnHeight.floatValue();

        for(int x = 0; x<dataRow;x++){
            columnLeft = gridLeft + spacing;
            columnRight = columnLeft + columnWidth;
            for(int y = 0; y<dataCol; y++){
                Rect areaRect = new Rect(columnLeft.intValue(), top.intValue(), columnRight.intValue(), bottom.intValue());
                dateBounds[i] = String.valueOf(columnLeft.intValue()) + "-" + String.valueOf(columnRight.intValue()) + "-"
                        + String.valueOf(top.intValue()) + "-" + String.valueOf(bottom.intValue());

                //highlight current day
                if(dateStats.get(i).contains("currentDay")){
                    RectF areaRectF = new RectF(areaRect.left,areaRect.top,areaRect.right,areaRect.bottom);
                    canvas.drawRoundRect(areaRectF,pixelsFromDp(10).floatValue(),pixelsFromDp(10).floatValue(),currentDayPaint);
                }

                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setTextSize(Float.valueOf(String.valueOf(areaRect.height()*.40)));

                if(x==0){
                    text = daysLabel[y];
                }else{
                    String dateStat = dateStats.get(i);
                    text = String.valueOf(dateList.get(i));
                    if (dateStat.equals("currentDay"))
                    {
                        mPaint.setColor(Color.BLUE);
                    }
                    if (dateStat.equals("trailingDays") || dateStat.equals("leadingDays"))
                    {
                        mPaint.setColor(Color.LTGRAY);
                    }
                    if (dateStat.equals("daysofTheMonth"))
                    {
                        mPaint.setColor(Color.BLACK);
                    }
                    i++;
                }

                RectF bounds = new RectF(areaRect);
                bounds.right = mPaint.measureText(text, 0, text.length());
                bounds.bottom = mPaint.descent() - mPaint.ascent();
                bounds.left += (areaRect.width() - bounds.right) / 2.0f;
                bounds.top += (areaRect.height() - bounds.bottom) / 2.5f;

                canvas.drawText(text, bounds.left, bounds.top - mPaint.ascent()-eventSpace, mPaint);
                columnLeft = columnRight + spacing;
                columnRight = columnLeft + columnWidth;

            }

            top = bottom + spacing;
            bottom = top + columnHeight.floatValue();
        }
    }

    public void drawHighlight(int b[]){
        this.highlightPos = b;
        highlight = true;
    }

    public void clearCanvas(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if(event.getAction()== MotionEvent.ACTION_DOWN){
                int boundsInt[] = new int[4];
                for (int c = 0; c<dateBounds.length; c++){
                    String bounds[] = dateBounds[c].toString().split("-");

                    if(dateStats.get(c).equals("daysOfTheMonth") || dateStats.get(c).equals("currentDay")){
                        if((x >= Integer.parseInt(bounds[0]) && x <= Integer.parseInt(bounds[1]))
                                && (y >= Integer.parseInt(bounds[2]) && y <= Integer.parseInt(bounds[3]))){
                            Toast.makeText(getContext(), dateList.get(c),Toast.LENGTH_SHORT).show();
                            boundsInt[0] = Integer.parseInt(bounds[0]);
                            boundsInt[1] = Integer.parseInt(bounds[2]);
                            boundsInt[2] = Integer.parseInt(bounds[1]);
                            boundsInt[3] = Integer.parseInt(bounds[3]);
                            drawHighlight(boundsInt);
                            invalidate();
                        }
                    }
                }
            }
        return true;
    }

    private Double pixelsFromDp(double dp)
    {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }

    public void drawEventIndicator(Canvas canvas){

        int counter = 0;
        List<String> dateWithEvents = new ArrayList<String>();

        for(String x: eventList){
            String events[] = x.split("-");
            if(selectedMonth==Integer.parseInt(events[0]) && selectedYear==Integer.parseInt(events[2])){

                if(dateWithEvents.toString().contains(events[1]+"-"+events[3])){
                    //
                }else if(dateWithEvents.toString().contains(events[1])){
                    if(!dateWithEvents.toString().contains(events[1]+"-bothEvents")){
                        if(events[3].equals("meeting")){
                            dateWithEvents.remove(events[1]+"-task");
                            dateWithEvents.add(events[1]+"-"+"bothEvents");
                        }else{
                            dateWithEvents.remove(events[1]+"-meeting");
                            dateWithEvents.add(events[1]+"-"+"bothEvents");
                        }
                    }
                }else {
                    dateWithEvents.add(events[1]+"-"+events[3]);
                }
            }
        }

        Log.e("Events", dateWithEvents.toString());
        for(String e: dateWithEvents){
            String events[] = e.toString().split("-");
            for(String d: dateList){
                if(events[0].equals(d)){
                    String bounds[] = dateBounds[dateList.indexOf(events[0])].toString().split("-");
                    float boundsLeft = Float.parseFloat(bounds[0]);
                    float boundsTop = Float.parseFloat(bounds[2]);
                    float boundsRight = Float.parseFloat(bounds[1]);
                    float boundsBottom = Float.parseFloat(bounds[3]);

                    float eventSpace = pixelsFromDp(3).floatValue();
                    float eventSpaceWidth = boundsRight - boundsLeft;
                    Double eventSpaceHeight = ((boundsBottom - boundsTop) - eventSpace*2) * .4;
                    float top_oneEvent = boundsBottom-eventSpaceHeight.floatValue()+eventSpace;
                    Double _left = boundsLeft + (pixelsFromDp(eventSpaceWidth)/pixelsFromDp(2.3));
                    float left = _left.floatValue();
                    Double _right = boundsRight - (pixelsFromDp(eventSpaceWidth)/pixelsFromDp(2.3));
                    float right = _right.floatValue();
                    float bottom_oneEvent = boundsBottom - (eventSpaceHeight.floatValue()/2);
                    float bottom_bothEvent = boundsBottom;
                    float top_bothEvent = bottom_oneEvent+eventSpace;

                    if(events[1].equals("bothEvents")){
                        canvas.drawRect(left,top_oneEvent,right,bottom_oneEvent,taskPaint);
                        canvas.drawRect(left,top_bothEvent,right,bottom_bothEvent,meetingPaint);
                    }else if(events[1].equals("meeting")){
                        canvas.drawRect(left,top_oneEvent,right,bottom_oneEvent,meetingPaint);
                    }else if(events[1].equals("task")){
                        canvas.drawRect(left,top_oneEvent,right,bottom_oneEvent,taskPaint);
                    }
                }
                counter++;
            }
        }
    }
}