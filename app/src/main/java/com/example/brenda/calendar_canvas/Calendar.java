package com.example.brenda.calendar_canvas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by brenda on 26/03/2018.
 */

public class Calendar extends Activity implements View.OnClickListener
{
    private java.util.Calendar _calendar;
    private int month, year, daysInMonth, currentDayOfMonth,leading_monthDays;
    private static final String dateTemplate = "MMMM dd yyyy";
    List<String> dateList = new ArrayList<String>();
    List<String> dateStats = new ArrayList<String>();
    final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
    final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    CalendarView calendarView;
    Context context;
    ImageView btnPrev, btnNext;
    TextView txtMonth, txtYear;
    String monthString, yearString, current_Month;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        init();
        displayCalendar(month, year);
    }

    public void init(){
        context = getApplicationContext();
        _calendar = java.util.Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(java.util.Calendar.MONTH);
        year = _calendar.get(java.util.Calendar.YEAR);

        current_Month = String.valueOf(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        currentDayOfMonth = _calendar.get(java.util.Calendar.DAY_OF_MONTH);

        linearLayout = findViewById(R.id.line1);
        calendarView = new CalendarView(context);

        txtMonth = findViewById(R.id.txtMonth);
        txtYear = findViewById(R.id.txtYear);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        txtYear.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    public void prev(){

        if (month <= 0)
        {
            month = 11;
            year = Integer.parseInt(calendarView.getYearString()) - 1;
        } else {
            month--;
            year = Integer.parseInt(calendarView.getYearString());
        }

        linearLayout.removeView(calendarView);
        displayCalendar(month, year);
    }

    public void next(){

        if (month >= 11)
        {
            month = 0;
            year = Integer.parseInt(calendarView.getYearString()) + 1;
        } else
        {
            month++;
            year = Integer.parseInt(calendarView.getYearString());
        }

        linearLayout.removeView(calendarView);
        displayCalendar(month, year);

    }

    private void displayCalendar(int month, int year){
        int _month, _year;
        _month = month;
        _year = year;

        GregorianCalendar cal = new GregorianCalendar(_year, _month, 0);
        dateList.clear();
        dateStats.clear();

        // The number of days to leave blank at
        // the start of this month.
        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;

        // Days in Current Month
        daysInMonth = daysOfMonth[_month];
        int currentMonth = _month;
        if (currentMonth == 11)
        {
            prevMonth = 10;
            daysInPrevMonth = daysOfMonth[prevMonth];
        } else if (currentMonth == 0)
        {
            prevMonth = 11;
            daysInPrevMonth = daysOfMonth[prevMonth];
        } else
        {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = daysOfMonth[prevMonth];
        }

        // Compute how much to leave before before the first day of the month
        trailingSpaces = cal.get(java.util.Calendar.DAY_OF_WEEK);

        if (cal.isLeapYear(cal.get(java.util.Calendar.YEAR)) && _month == 1)
        {
            ++daysInMonth;
        }
       // Trailing Month days
        if(trailingSpaces!=0 && trailingSpaces!=7){
            for (int i = 0; i < trailingSpaces; i++)
            {
                dateList.add(String.valueOf((daysInPrevMonth - trailingSpaces + 1) + i));
                dateStats.add("trailingDays");
            }
        }
        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++)
        {
            if (currentDayOfMonth == i && _month == _calendar.get(java.util.Calendar.MONTH) && _year == _calendar.get(java.util.Calendar.YEAR)){
                dateList.add(String.valueOf(i));
                dateStats.add("currentDay");
            }else {
                dateList.add(String.valueOf(i));
                dateStats.add("daysOfTheMonth");
            }
        }

         // Leading Month days
        leading_monthDays = dateList.size()%7;
        if(leading_monthDays==0){
            leading_monthDays = 7;
        }else{
            leading_monthDays = (dateList.size()<=35 ? (7-leading_monthDays) + 7 : 7 - (leading_monthDays));
        }
        for (int i = 0; i < leading_monthDays; i++)
        {
            dateList.add(String.valueOf(i + 1));
            dateStats.add("leadingDays");
        }


        calendarView = new CalendarView(this, _month, _year);
        calendarView.setDateList(getDateList());
        calendarView.setDateStats(getDateStats());
        calendarView.invalidate();
        monthString = calendarView.getMonthString();
        yearString = calendarView.getYearString();

        txtMonth.setText(monthString);
        txtYear.setText(yearString);

        calendarView.eventList.add("4-11-2018-task");
        calendarView.eventList.add("4-11-2018-task");
        calendarView.eventList.add("4-12-2018-task");
        calendarView.eventList.add("4-11-2018-meeting");
        calendarView.eventList.add("5-5-2018-meeting");
        calendarView.eventList.add("4-13-2018-meeting");
        calendarView.eventList.add("4-11-2018-meeting");

        linearLayout.addView(calendarView);

    }

    public void selectYear(){
        RelativeLayout linearLayout = new RelativeLayout(this);
        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMaxValue(2030);
        yearPicker.setValue(2018);
        yearPicker.setMinValue(2000);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(yearPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Calendar.this);
        alertDialogBuilder.setTitle("Select year");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder.setCancelable(false);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        updateCal(yearPicker.getValue());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        alertDialogBuilder.setPositiveButton("OK", dialogClickListener);
        alertDialogBuilder.setNegativeButton("CANCEL",dialogClickListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateCal(int selectedYear){
        int selectedMonth = 0;
        int counter = 0;

        for(String months: months){
            if(txtMonth.getText().equals(months)){
                selectedMonth = counter;
            }
            counter++;
        }

        linearLayout.removeView(calendarView);
        displayCalendar(selectedMonth,selectedYear);
    }

    public List<String> getDateList() {
        return dateList;
    }

    public List<String> getDateStats() {
        return dateStats;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtYear:
                selectYear();
                break;
            case R.id.btnPrev:
                prev();
                break;
            case R.id.btnNext:
                next();
                break;
            default:
                break;
        }
    }
}