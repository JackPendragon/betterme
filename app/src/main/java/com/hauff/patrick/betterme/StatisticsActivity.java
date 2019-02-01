package com.hauff.patrick.betterme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hauff.patrick.betterme.habit_statistics.HabitStatistics;
import com.hauff.patrick.betterme.entry.Entry;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The Statistics activity which show us some statistics about our activities and habits
 */
public class StatisticsActivity extends AppCompatActivity {

    PieChart averageDailyActivity;                                                                  //Some variables we need
    HorizontalBarChart habitProgress;
    RadarChart enforcementQuotes;
    private ArrayList<HabitStatistics> habitList;
    private ArrayList<Float> hours;
    private String date = "";

    /**
     * When we call this activity so this is our first init
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);                                              //Hide the title
        Objects.requireNonNull(getSupportActionBar()).hide();                                                               //Hide the title bar

        setContentView(R.layout.activity_statistics);                                               //Set our layout

        Intent intentFromStatisticsWithDayList = getIntent();                                       //get daylist data from mainactivity
        ArrayList<Entry> dayList = (ArrayList<Entry>) intentFromStatisticsWithDayList.getSerializableExtra("dayList");

        Intent intentFromStatisticsWithHabitList = getIntent();                                     //get habitlist data from mainactivity
        habitList = (ArrayList<HabitStatistics>) intentFromStatisticsWithHabitList.getSerializableExtra("habitList");

        Intent intentFromStatisticsWithDate = getIntent();                                          //get date from mainactivity
        date = (String) intentFromStatisticsWithDate.getSerializableExtra("date");

        getHours(dayList);                                                                          //Calculate Hours we used for an activity
        showDayPieChart("", hours, dayList);                                                  //Show us a PieChart with all activities for a selected day
        showHabitProgress("", habitList, dayList);                                            //Show us how many days we do new habits and which habits
        showEnforcementQuotes("", habitList, dayList);                                        //Show us the enforcement quotes

        Slidr.attach(this);
    }

    /**
     * Show us a PieChart with all activities for a selected day
     *
     * @param label label
     * @param h h
     * @param list list
     */
    public void showDayPieChart(String label, ArrayList<Float> h, ArrayList<Entry> list){
        TextView textviewDate = findViewById(R.id.textview_date_statistics);
        textviewDate.setText(date);

        averageDailyActivity = findViewById(R.id.piechart_average_daily_activity);

        averageDailyActivity.setUsePercentValues(true);
        averageDailyActivity.getDescription().setEnabled(false);
        averageDailyActivity.setExtraOffsets(5, 10, 5, 5);

        averageDailyActivity.setDragDecelerationFrictionCoef(0.95f);

        averageDailyActivity.setDrawHoleEnabled(true);
        averageDailyActivity.setHoleColor(Color.WHITE);
        averageDailyActivity.setTransparentCircleRadius(57f);
        averageDailyActivity.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        averageDailyActivity.setDrawEntryLabels(true);
        averageDailyActivity.setEntryLabelColor(Color.parseColor("#FF00280A"));
        averageDailyActivity.setDrawCenterText(true);
        averageDailyActivity.setEntryLabelTextSize(8);
        averageDailyActivity.setUsePercentValues(false);
        averageDailyActivity.getLegend().setEnabled(false);
        averageDailyActivity.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
                TextView label = findViewById(R.id.textview_activity_label);
                label.setText(((PieEntry) e).getLabel() + ":");

                TextView hours = findViewById(R.id.textview_activity_hours);
                String hou = Float.toString(((PieEntry) e).getValue());
                hours.setText(hou);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        ArrayList<PieEntry> yValuesPieChart = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            yValuesPieChart.add(new PieEntry(h.get(i), list.get(i).getActivity()));
        }

        PieDataSet dataSetPieChart = new PieDataSet(yValuesPieChart, label);

        dataSetPieChart.setSliceSpace(2f);
        dataSetPieChart.setSelectionShift(10f);
        dataSetPieChart.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData dataPieChart = new PieData(dataSetPieChart);
        dataPieChart.setValueTextSize(0);
        dataPieChart.setValueTextColor(Color.parseColor("#FF00280A"));

        averageDailyActivity.setData(dataPieChart);
    }

    /**
     * Show us how many days we do new habits and which habits
     *
     * @param label label
     * @param habits habits
     * @param list list
     */
    public void showHabitProgress(String label, ArrayList<HabitStatistics> habits, ArrayList<Entry> list){
        habitProgress = findViewById(R.id.horizontalBarChart_habit_progress);
        habitProgress.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        habitProgress.getLegend().setEnabled(false);
        habitProgress.getDescription().setEnabled(false);
        habitProgress.getAxisLeft().setDrawLabels(false);
        habitProgress.getXAxis().setDrawLabels(false);

        ArrayList<BarEntry> yValuesHorizontalBarChart = new ArrayList<>();
        for (int i = 0; i < habits.size(); i++){
            yValuesHorizontalBarChart.add(new BarEntry(i, habits.get(i).getNumberOfSuccessfullyPlannedDays()));
        }

        habitProgress.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
                TextView habitLabel = (TextView) findViewById(R.id.textview_habit_label);
                habitLabel.setText(habitList.get((int) h.getX()).getHabit());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet set1 = (BarDataSet) new BarDataSet(yValuesHorizontalBarChart, label);
        set1.setColors(ColorTemplate.JOYFUL_COLORS);

        BarData data = new BarData(set1);

        habitProgress.setData(data);
    }

    /**
     * Show us the enforcement quotes
     *
     * @param label label
     * @param habits habits
     * @param list list
     */
    public void showEnforcementQuotes(String label, final ArrayList<HabitStatistics> habits, ArrayList<Entry> list){
        enforcementQuotes = findViewById(R.id.radarchart_enforcement_quotes);
        enforcementQuotes.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        enforcementQuotes.getLegend().setEnabled(false);
        enforcementQuotes.getYAxis().setAxisMinimum(0f);
        enforcementQuotes.getYAxis().setAxisMaximum(100f);
        enforcementQuotes.getYAxis().setDrawLabels(false);
        enforcementQuotes.getDescription().setEnabled(false);
        enforcementQuotes.setRotationEnabled(false);
        enforcementQuotes.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        final ArrayList<String> xLabels = new ArrayList<>();
        for(int i = 0; i < habits.size(); i++){
            xLabels.add(habits.get(i).getHabit());
        }

        XAxis xAxis = enforcementQuotes.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabels.get((int)value % xLabels.size());
            }
        });

        ArrayList<RadarEntry> yValuesRadarChart = new ArrayList<>();
        for (int i = 0; i < habits.size(); i++){
            float plannedDays = habits.get(i).getNumberOfPlannedDays();
            float successfullyPlannedDays = habits.get(i).getNumberOfSuccessfullyPlannedDays();
            float sum = ((100 / plannedDays) * successfullyPlannedDays);
            yValuesRadarChart.add(new RadarEntry(sum));
        }

        RadarDataSet set1 = new RadarDataSet(yValuesRadarChart, label);
        set1.setColors(ColorTemplate.JOYFUL_COLORS);
        set1.setDrawFilled(true);

        RadarData data = new RadarData(set1);

        enforcementQuotes.setData(data);
        enforcementQuotes.invalidate();
    }

    /**
     * Calculate Hours we used for an activity
     *
     * @param list list
     */
    public void getHours(ArrayList<Entry> list){
        hours = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            float a = 0.0f;
            float b = 0.0f;
            String startTime = list.get(i).getStart();
            float startTimeHour = Float.parseFloat(startTime.substring(0, 2));
            float startTimeMinutes = Float.parseFloat(startTime.substring(3, 5));
            if(startTimeMinutes>0){
                a = (float) (60.0f / startTimeMinutes);
                b = 1 / a;
            }
            float startTimeDecimal = (float) (startTimeHour + b);

            String endTime = list.get(i).getEnd();
            if(i == (list.size()-1) && endTime.equals("00:00")) endTime = "24:00";
            float endTimeHour = Float.parseFloat(endTime.substring(0, 2));
            float endTimeMinutes = Float.parseFloat(endTime.substring(3, 5));
            if(endTimeMinutes>0){
                a = (float) (60.0f / (endTimeMinutes));
                b = 1 / a;
            }
            float endtimeDecimal = (float) (endTimeHour);

            if(endTime.equals("24:00")) hours.add(endtimeDecimal-startTimeDecimal);
            else hours.add(endtimeDecimal-startTimeDecimal);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
