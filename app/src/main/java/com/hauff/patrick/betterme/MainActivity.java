package com.hauff.patrick.betterme;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hauff.patrick.betterme.habit_statistics.HabitStatistics;
import com.hauff.patrick.betterme.day.Day;
import com.hauff.patrick.betterme.entry.Entry;
import com.hauff.patrick.betterme.list_adapter.EntryListAdapter;
import com.hauff.patrick.betterme.notofications.NotificationHelper;
import com.hauff.patrick.betterme.shared_prefs.MySharedPreferences;
import com.hauff.patrick.betterme.timepicker.TimePickerFragment;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.github.yavski.fabspeeddial.FabSpeedDial;

/**
 * MainActivity class which is the first one to show when you open the app
 */
public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    private Dialog myDialog;                                                                        //A lot of variables ive used
    final String SHARED_PREFERENCES_TEMPLATES = "sharedPreferencesTemplates";
    final String SHARED_PREFERENCES_DAYS = "sharedPreferencesDays";
    private Boolean isStartBtn = true;
    private String date = "";
    private String key = "";
    MySharedPreferences mySharedPreferencesTemplates;
    MySharedPreferences mySharedPreferencesDays;
    private Entry entry;
    private ArrayList<Entry> templateList;
    private ArrayList<Entry> activitysOfTheDayList;
    private ArrayList<Entry> cache;
    private ArrayList<String> activityFromTemplatesList;
    private ArrayList<Entry> dayList;
    private ArrayList<String> notificationsOfTheDay;
    private ArrayList<Day> sharedPrefsList;
    private ArrayList<HabitStatistics> habitList;
    private final TextView[] textviewStartTime = new TextView[1];
    private final TextView[] textviewEndTime = new TextView[1];
    private final TextInputEditText[] textinputedittextActivity = new TextInputEditText[1];
    private final TextInputEditText[] textinputedittextDescription = new TextInputEditText[1];
    private final TextInputLayout[] textinputlayoutDescription = new TextInputLayout[1];
    private final TextInputLayout[] textinputlayoutActivity = new TextInputLayout[1];
    private final Spinner[] spinnerSaveAs = new Spinner[1];
    private final Spinner[] spinnerTemplate = new Spinner[1];
    private final Button[] getStartTime = new Button[1];
    private final Button[] getEndTime = new Button[1];
    private ListView listView;
    private EntryListAdapter adapter;
    NotificationHelper notificationHelper;

    /**
     * this method will called when the app starts
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        notificationHelper = new NotificationHelper(this);                                    //Init our notificationHelper

        Calendar c = Calendar.getInstance();                                                        //Init our calendar
        date = DateFormat.getDateInstance().format(c.getTime());                                    //Get the current or choosen date
        StringBuilder sb = new StringBuilder(date);
        if(date.substring(0, 1).equals("0") && date.substring(3, 4).equals("0")) {
            date = sb.deleteCharAt(0).toString();
            date = sb.deleteCharAt(2).toString();
        }
        if(date.substring(0, 1).equals("0")) date = sb.deleteCharAt(0).toString();
        if(date.substring(3, 4).equals("0")) date = sb.deleteCharAt(3).toString();

        if(dayList == null) dayList = new ArrayList<>();                                            //Init the dayList

        mySharedPreferencesDays = new MySharedPreferences(                                          //Init the Day SharedPrefs
                myDialog.getContext(), SHARED_PREFERENCES_DAYS);
        dayList = mySharedPreferencesDays.getEntrySharedPrefs(date, dayList);
        sharedPrefsList = mySharedPreferencesDays.getAllDaySharedPrefs();

        mySharedPreferencesTemplates = new MySharedPreferences(                                     //Init the Templates SharedPrefs
                myDialog.getContext(), SHARED_PREFERENCES_TEMPLATES);


        showDayList();                                                                              //Init the DayList
        getNotificationTime();                                                                      //Init the Notifications
        notificationHelper.setNotificationTime(notificationsOfTheDay, dayList);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {             //Set the listview clickable
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
                showAreYouSurePopUp(position);                                                      //Set a delete mode on lingtime click
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {                     //Set a activity checked and unchecked and save this in the SharedPrefs
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = adapter.getView(position, view, parent).findViewById(R.id.imgeview_isdone);
                if(dayList.get(position).isDone()){
                    imageView.setImageResource(R.mipmap.checked_grey);
                    dayList.get(position).setDone(false);
                    dayList.clear();
                    dayList = mySharedPreferencesDays.getEntrySharedPrefs(date, dayList);
                    dayList.get(position).setDone(false);
                    mySharedPreferencesDays.saveEntrysIntoSharedPreferences(dayList, date);
                    adapter.notifyDataSetChanged();
                    showDayList();
                }else{
                    imageView.setImageResource(R.mipmap.checked);
                    dayList.get(position).setDone(true);
                    dayList.clear();
                    dayList = mySharedPreferencesDays.getEntrySharedPrefs(date, dayList);
                    dayList.get(position).setDone(true);
                    mySharedPreferencesDays.saveEntrysIntoSharedPreferences(dayList, date);
                    adapter.notifyDataSetChanged();
                    showDayList();
                }
            }
        });

        habitList = new ArrayList<>();                                                              //Init the habitList
        habitList = mySharedPreferencesDays.getMyHabitsFromSharedPrefs();

        CalendarView calendar = findViewById(R.id.calender_view);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "." + (month + 1) + "." + year;
                showDayList();
            }
        });

        FabSpeedDial mainMenu = findViewById(R.id.fabSpeedDial);                                    //Init the Menu navigation
        mainMenu.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if(menuItem.getTitle() == getString(R.string.action_plan_next_day)) {
                    showNewEntryPopUp();
                }
                else if(menuItem.getTitle() == getString(R.string.action_trophys)) {
                    openTrophyPopUp();
                }
                else if(menuItem.getTitle() == getString(R.string.action_statistics)){
                    goToStatistics(dayList, habitList);
                }
                return true;
            }

            @Override
            public void onMenuClosed() {}
        });
    }

    /**
     * Show a popup to create a new entry in the list and SharedPrefs
     */
    public void showNewEntryPopUp(){
        final String select = myDialog.getContext().getResources().getString(R.string.please_select);  //Get String array from strings.xml
        myDialog.setContentView(R.layout.popup_plan_next_day_layout);                               //Set popup layout
        mySharedPreferencesTemplates = new MySharedPreferences(                                     //Load SharedPrefs Days and Templates
                myDialog.getContext(), SHARED_PREFERENCES_TEMPLATES);
        mySharedPreferencesDays = new MySharedPreferences(
                myDialog.getContext(), SHARED_PREFERENCES_DAYS);

        activityFromTemplatesList = new ArrayList<>();
        mySharedPreferencesTemplates.reloadTemplates(                                               //Init our Templates
                templateList, activityFromTemplatesList, key, cache );

        spinnerTemplate[0] = myDialog.findViewById(R.id.spinner_habits);                            //Init our template spinner
        ArrayAdapter<String> karant_adapter = new ArrayAdapter<>(
                getApplicationContext(), android.R.layout.simple_spinner_item, activityFromTemplatesList);
        spinnerTemplate[0].setAdapter(karant_adapter);
        spinnerTemplate[0].setSelection(activityFromTemplatesList.size()-1);
        spinnerTemplate[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String activityFromSpinner = parent.getSelectedItem().toString().trim();
                if(!activityFromSpinner.equals(select)){
                    mySharedPreferencesTemplates.fillFormular(
                            activityFromSpinner, textinputedittextActivity,
                            textinputedittextDescription, getStartTime,
                            getEndTime, cache, myDialog);
                    spinnerSaveAs[0] = myDialog.findViewById(R.id.spinner_save_as);
                }else{
                    textinputedittextActivity[0] =
                            myDialog.findViewById(R.id.textinputedittext_activity);
                    textinputedittextActivity[0].setText("");

                    textinputedittextDescription[0] =
                            myDialog.findViewById(R.id.textinputedittext_description);
                    textinputedittextDescription[0].setText("");

                    getStartTime[0] = myDialog.findViewById(R.id.textview_start_time);
                    getStartTime[0].setText(R.string.textview_start_time);

                    getEndTime[0] = myDialog.findViewById(R.id.textview_end_time);
                    getEndTime[0].setText(R.string.textview_end_time);

                    spinnerSaveAs[0] = myDialog.findViewById(R.id.spinner_save_as);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView textviewDate = myDialog.findViewById(R.id.textview_date);
        textviewDate.setText(date);

        getStartTime[0] = myDialog.findViewById(R.id.textview_start_time);                          //This will show you the TimPicker for the start time
        getTimePicker(getStartTime[0]);

        getEndTime[0] = myDialog.findViewById(R.id.textview_end_time);                              //This will show you the TimPicker for the end time
        getTimePicker(getEndTime[0]);

        spinnerSaveAs[0] = myDialog.findViewById(R.id.spinner_save_as);                             //Init the saveAs spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.save_as, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSaveAs[0].setAdapter(adapter);
        spinnerSaveAs[0].setOnItemSelectedListener(this);

        Button buttonSave = myDialog.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int countSelectedSpinnerLetters =
                        spinnerSaveAs[0].getSelectedItem().toString().length();

                textinputlayoutActivity[0] =                                                        //Get the activity
                        myDialog.findViewById(R.id.textinputlayout_activity);
                String activity = Objects.requireNonNull(textinputlayoutActivity[0].getEditText()).getText().toString().trim();

                textinputlayoutDescription[0] = myDialog.findViewById(R.id.textinputlayout_description);//Get the description
                String description = Objects.requireNonNull(textinputlayoutDescription[0].getEditText()).getText().toString().trim();

                textviewStartTime[0] = myDialog.findViewById(R.id.textview_start_time);             //Get the start time
                String startTime = textviewStartTime[0].getText().toString();

                textviewEndTime[0] = myDialog.findViewById(R.id.textview_end_time);                 //Get the end time
                String endTime = textviewEndTime[0].getText().toString();

                String saveAs = spinnerSaveAs[0].getSelectedItem().toString();                      //Get the saveAs

                if(templateList == null) templateList = new ArrayList<>();                          //Init the template list
                if(activitysOfTheDayList == null) activitysOfTheDayList = new ArrayList<>();        //Init the activity of the day list

                if(countSelectedSpinnerLetters == 8 || countSelectedSpinnerLetters == 7 ||          //Save only habits and templates to SharedPrefs
                        countSelectedSpinnerLetters == 15 || countSelectedSpinnerLetters == 19){
                    entry = new Entry(activity, startTime, endTime, description, saveAs, false);
                    templateList.add(entry);
                    mySharedPreferencesTemplates.saveEntrysIntoSharedPreferences(templateList, activity);
                    mySharedPreferencesTemplates.reloadTemplates(templateList, activityFromTemplatesList, key, cache );
                }
                activitysOfTheDayList.clear();
                entry = new Entry(activity, startTime, endTime, description, saveAs, false);  //Prepare a Entry Object and save it
                activitysOfTheDayList.add(entry);
                mySharedPreferencesDays.saveEntrysIntoSharedPreferences(activitysOfTheDayList, date);

                showDayList();

                Toast.makeText(MainActivity.this, R.string.toast_save, Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonClose = myDialog.findViewById(R.id.button_cancel);
        buttonClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        Button buttonDelete = myDialog.findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textinputlayoutActivity[0] = myDialog.findViewById(R.id.textinputlayout_activity);

                mySharedPreferencesTemplates.deleteTemplateFromPrefs(myDialog);
                mySharedPreferencesTemplates.reloadTemplates(templateList, activityFromTemplatesList, key, cache );
                spinnerTemplate[0].setSelection(activityFromTemplatesList.size()-1);

                Toast.makeText(MainActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    /**
     * Method to navigate to the statistics activity with the daylist, habitlist and the selected date
     *
     * @param dList Our dayList
     * @param hList Our habitsList
     */
    public void goToStatistics(ArrayList<Entry> dList, ArrayList<HabitStatistics> hList){
        Intent intentToStatistics = new Intent(myDialog.getContext(), StatisticsActivity.class);
        intentToStatistics.putExtra("dayList", dList);
        intentToStatistics.putExtra("habitList", hList);
        intentToStatistics.putExtra("date", date);
        startActivity(intentToStatistics);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Open the Trophy popup
     */
    public void openTrophyPopUp(){
        myDialog.setContentView(R.layout.content_trophys);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

        ArrayList<Integer> stats = new ArrayList<>();
        for(int i = 0; i < habitList.size(); i++){
            float plannedDays = habitList.get(i).getNumberOfPlannedDays();
            float successfullyPlannedDays = habitList.get(i).getNumberOfSuccessfullyPlannedDays();
            float sum = ((100 / plannedDays) * successfullyPlannedDays);
            stats.add((int) sum);
        }

        if(sharedPrefsList.size()>7 ) {                                                              //The first trophy for the first planned week
            View view = myDialog.findViewById(R.id.view_walking);
            view.setBackgroundResource(R.mipmap.walking_done);
        }
        if(sharedPrefsList.size()>14) {                                                             //The first trophy for the second planned week
            View view = myDialog.findViewById(R.id.view_jogging);
            view.setBackgroundResource(R.mipmap.jogging_done);
        }
        if(sharedPrefsList.size()>21) {                                                             //The first trophy for the 3th planned week
            View view = myDialog.findViewById(R.id.view_running);
            view.setBackgroundResource(R.mipmap.running_done);
        }
        if(sharedPrefsList.size()>90) {                                                             //The first trophy 90 days
            View view = myDialog.findViewById(R.id.view_athletic);
            view.setBackgroundResource(R.mipmap.athletic_done);
        }
        if((habitList.size()>=1) && stats.get(0)>=80 && sharedPrefsList.size()>14) {                //When the first habit is more that 80% successfully
            View view = myDialog.findViewById(R.id.view_habit_walker);
            view.setBackgroundResource(R.mipmap.walking_done);
        }
        if((habitList.size()>=2) && stats.get(1)>=80 && sharedPrefsList.size()>14) {                //When the second habit is more that 80% successfully
            View view = myDialog.findViewById(R.id.view_habit_jogging);
            view.setBackgroundResource(R.mipmap.jogging_done);
        }
        if((habitList.size()>=3) && stats.get(2)>=80 && sharedPrefsList.size()>14) {                //When the 3th habit is more that 80% successfully
            View view = myDialog.findViewById(R.id.view_habit_runner);
            view.setBackgroundResource(R.mipmap.running_done);
        }
        if((habitList.size()>=4) && stats.get(3)>=80 && sharedPrefsList.size()>14) {                //When the 4th habit is more that 80% successfully
            View view = myDialog.findViewById(R.id.view_habit_athletic);
            view.setBackgroundResource(R.mipmap.athletic_done);
        }
    }

    /**
     * Show a verifying popup before you can delete a listview entry
     *
     * @param position position
     */
    public void showAreYouSurePopUp(final int position) {
        myDialog.setContentView(R.layout.popup_are_you_sure_layout);                                //Load the PopUp Layout

        Button buttonYes = myDialog.findViewById(R.id.button_yes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cache = mySharedPreferencesDays.getEntrySharedPrefs(date, dayList);
                cache.remove(position);
                mySharedPreferencesDays.deleteEntryFromSharedPreferences(cache, date);
                showDayList();
                Toast.makeText(MainActivity.this, R.string.toast_delete_successfully, Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        });

        Button buttonNo = myDialog.findViewById(R.id.button_no);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    /**
     * Method to load the daylist
     */
    public void showDayList(){
        dayList = mySharedPreferencesDays.getEntrySharedPrefs(date, dayList);
        listView = findViewById(R.id.listview_day_activities);
        if(dayList == null) {
            listView.setAdapter(null);
        } else {
            adapter = new EntryListAdapter(myDialog.getContext(), R.layout.adapter_view_layout, dayList);
            listView.setAdapter(adapter);
        }
    }

    /**
     * Method to open the time picker
     *
     * @param textView TextView we set
     */
    public void getTimePicker(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker start time");
            }
        });
    }

    /**
     * Method to get the notification time
     */
    public void getNotificationTime(){
        notificationsOfTheDay = new ArrayList<>();
        if(dayList != null){
            for(int i = 0; i < dayList.size(); i++){
                notificationsOfTheDay.add(dayList.get(i).getStart());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min = "";
        String hou = "";
        if(isStartBtn){
            TextView textView = myDialog.findViewById(R.id.textview_start_time);
            if(minute < 10) min = "0" + minute;
            else min = "" + minute;
            if(hourOfDay < 10) hou = "0" + hourOfDay;
            else hou = "" + hourOfDay;
            textView.setText(hou + ":" + min);
            isStartBtn = false;
        }else{
            TextView textView = myDialog.findViewById(R.id.textview_end_time);
            if(minute < 10) min = "0" + minute;
            else min = "" + minute;
            if(hourOfDay < 10) hou = "0" + hourOfDay;
            else hou = "" + hourOfDay;
            textView.setText(hou + ":" + min);
            isStartBtn = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}