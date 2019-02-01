package com.hauff.patrick.betterme.shared_prefs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hauff.patrick.betterme.habit_statistics.HabitStatistics;
import com.hauff.patrick.betterme.day.Day;
import com.hauff.patrick.betterme.entry.Entry;
import com.hauff.patrick.betterme.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Our SharedPrefs class to manage our SharedPrefs
 */
@SuppressLint("Registered")
public class MySharedPreferences extends AppCompatActivity {

    private final Spinner[] spinnerTemplate = new Spinner[1];                                       //Our variables we need
    SharedPreferences myPrefs;
    SharedPreferences.Editor prefEditor;
    Context context;
    ArrayList<Entry> cache;
    Gson gson;
    String json;
    Type type;

    /**
     * Our constructor
     *
     * @param context context
     * @param prefsName prefsName
     */
    public MySharedPreferences(Context context, String prefsName){
        this.context = context;
        myPrefs = context.getSharedPreferences(prefsName, MODE_PRIVATE);
    }

    /**
     * Save Entry Object into SharedPrefs
     *
     * @param list list
     * @param key key
     */
    public void saveEntrysIntoSharedPreferences(ArrayList<Entry> list, String key){
        cache = new ArrayList<Entry>();                                                             //helpfull variables we need
        prefEditor = myPrefs.edit();
        gson = new Gson();
        json = myPrefs.getString(key, null);
        type = new TypeToken<ArrayList<Entry>>(){}.getType();
        cache = gson.fromJson(json, type);

        String isEmpty = myPrefs.getString(key, "isEmpty");

        if(isEmpty.equals("isEmpty") || isEmpty.equals("[]")){                                      //save complete list into SharedPreds when SharedPrefs is empty
            cache = list;
        }else if(!isEmpty.equals("isEmpty")){                                                       //Fill it with the isDone variables
            for(int i = 0; i < list.size(); i++) {
                if(cache.size() > 0){
                    if(list.get(i).getActivity().equals(cache.get(i).getActivity()))
                       cache.get(i).setDone(list.get(i).isDone());
                    else cache.add(list.get(i));
                }
            }
        }else if(isEmpty.equals("isEmpty")){                                                        //save complete list into SharedPreds
            cache = list;
        }

        json = gson.toJson(cache);
        prefEditor.putString(key, json);
        prefEditor.apply();
    }

    /**
     * Delete Entry from SharedPrefs
     *
     * @param list list
     * @param key key
     */
    public void deleteEntryFromSharedPreferences(ArrayList<Entry> list, String key){
        prefEditor = myPrefs.edit();
        gson = new Gson();

        json = myPrefs.getString(key, null);
        type = new TypeToken<ArrayList<Entry>>(){}.getType();

        json = gson.toJson(list);
        prefEditor.putString(key, json);
        prefEditor.apply();
    }

    /**
     * get the SharedPrefs
     *
     * @param key key
     * @param dayList dayList
     * @return dayList dayList
     */
    public ArrayList<Entry> getEntrySharedPrefs(String key, ArrayList<Entry> dayList){
        gson = new Gson();
        json = myPrefs.getString(key, null);
        type = new TypeToken<ArrayList<Entry>>(){}.getType();
        dayList = gson.fromJson(json, type);

        return dayList;
    }

    /**
     * Return all day SharedPrefs
     *
     * @return dayList dayList
     */
    public ArrayList<Day> getAllDaySharedPrefs(){
        Map<String, ?> allEntries = myPrefs.getAll();
        ArrayList<Day> dayList = new ArrayList<Day>();
        gson = new Gson();
        Type type;
        String key;

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            key = entry.getKey();
            json = myPrefs.getString(key, null);
            type = new TypeToken<ArrayList<Entry>>(){}.getType();
            cache = gson.fromJson(json, type);
            if(!key.equals("")) {
                Day day = new Day(key, cache);
                dayList.add(day);
            }
        }

        return dayList;
    }

    /**
     * Return all habits from SharedPrefs
     *
     * @return habitList habitList
     */
    public ArrayList<HabitStatistics> getMyHabitsFromSharedPrefs(){
        String[] stringArray = this.context.getResources().getStringArray(R.array.save_as);         //Get String array from strings.xml
        Map<String, ?> allEntries = myPrefs.getAll();                                               //helpfull variables we need
        ArrayList<HabitStatistics> habitList = new ArrayList<HabitStatistics>();
        gson = new Gson();
        Type type;
        String key;

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {                                  //Iteration over all Entries
            key = entry.getKey();
            json = myPrefs.getString(key, null);
            type = new TypeToken<ArrayList<Entry>>(){}.getType();
            cache = gson.fromJson(json, type);

            if(key.equals("")) {                                                                    //When empty next step in loop
                continue;
            }

            for(int h = 0; h < cache.size(); h++) {
                outerloop:
                if (cache.get(h).getSaveAs().equals(stringArray[2])) {                              //Is it a habit? Fast forwart
                    if(habitList.size()>0){                                                         //HabitList bigger than 0
                        for (int j = 0; j < habitList.size(); j++) {                                //HabitList iteration
                            if (cache.get(h).getActivity().equals(habitList.get(j).getHabit())) {   //Is there already this habit
                                if(cache.get(h).isDone() == null) break outerloop;                  //IsDone  = null -> next step
                                if(cache.get(h).isDone()) {                                 //When habit is checked
                                    habitList.get(j).setNumberOfPlannedDays(                        //Plus for this habit for the statistics
                                            habitList.get(j).getNumberOfPlannedDays()+1);
                                    habitList.get(j).setNumberOfSuccessfullyPlannedDays(
                                            habitList.get(j).getNumberOfSuccessfullyPlannedDays()+1);
                                    break outerloop;
                                }else if(!cache.get(h).isDone()){                           //Unchecked -> planned but not successfully
                                    habitList.get(j).setNumberOfPlannedDays(
                                            habitList.get(j).getNumberOfPlannedDays()+1);
                                    break outerloop;
                                }
                            }else if(habitList.size()== j+1){                                       //create a new habit entry for the statistics
                                if(cache.get(h).isDone())habitList.add(
                                        new HabitStatistics(cache.get(h).getActivity(),
                                                1, 1));
                                else habitList.add(new HabitStatistics(cache.get(h).getActivity(),
                                        1, 0));
                                break outerloop;
                            }
                        }
                    }else{                                                                          //create a new habit entry for the statistics
                        if(cache.get(h).isDone())habitList.add(new HabitStatistics(
                                cache.get(h).getActivity(),
                                1, 1));
                        else habitList.add(new HabitStatistics(
                                cache.get(h).getActivity(),
                                1, 0));
                    }
                } else {                                                                            //Next step in loop
                    continue;
                }
            }
        }

        return habitList;                                                                           //Return the List
    }

    /**
     * Method to delete a template from the templates SharedPrefs
     *
     * @param myDialog myDialog
     */
    public void deleteTemplateFromPrefs(Dialog myDialog){
        prefEditor = myPrefs.edit();
        String activity = "";
        final Spinner spinnerActivity;
        spinnerActivity = (Spinner) myDialog.findViewById(R.id.spinner_habits);
        activity = spinnerActivity.getSelectedItem().toString();
        prefEditor.remove(activity);
        prefEditor.apply();
    }

    /**
     * Reload all templates from templates SharedPrefs
     *
     * @param templateList templateList
     * @param activityFromTemplatesList activityFromTemplatesList
     * @param key key
     * @param cache cache
     */
    public void reloadTemplates(ArrayList<Entry> templateList, ArrayList<String> activityFromTemplatesList, String key, ArrayList<Entry> cache){
        String select = this.context.getResources().getString(R.string.please_select);              //Get String from strings.xml
        Map<String, ?> allEntries = myPrefs.getAll();
        gson = new Gson();
        Type type;

        if(templateList == null) templateList = new ArrayList<>();

        activityFromTemplatesList.clear();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            key = entry.getKey();
            json = myPrefs.getString(key, null);
            type = new TypeToken<ArrayList<Entry>>(){}.getType();
            cache = gson.fromJson(json, type);
            templateList.add(cache.get(0));
            activityFromTemplatesList.add(key);
        }
        activityFromTemplatesList.add(select);
    }

    /**
     * Fill the "plan new day" formular when you select a template or a new habit from spinner
     *
     * @param activityFromSpinner activityFromSpinner
     * @param textinputedittextActivity textinputedittextActivity
     * @param textinputedittextDescription textinputedittextDescription
     * @param getStartTime getStartTime
     * @param getEndTime getEndTime
     * @param cache cache
     * @param myDialog myDialog
     */
    public void fillFormular(String activityFromSpinner, TextInputEditText[] textinputedittextActivity, TextInputEditText[] textinputedittextDescription,
                             TextView[] getStartTime, TextView[] getEndTime, ArrayList<Entry> cache, Dialog myDialog){
        String[] stringArray = this.context.getResources().getStringArray(R.array.save_as);         //Get String array from strings.xml
        int index = 0;                                                                              //helpfull variables we need
        Type type;
        json = myPrefs.getString(activityFromSpinner, null);
        type = new TypeToken<ArrayList<Entry>>(){}.getType();
        cache = gson.fromJson(json, type);

        for(int i = 0; i < cache.size(); i++){
            String act = cache.get(i).getActivity().trim();
            if(act.equals(activityFromSpinner)){
                index = i;
            }
        }

        textinputedittextActivity[0] = myDialog.findViewById(R.id.textinputedittext_activity);      //Fill the formular here
        textinputedittextActivity[0].setText(cache.get(index).getActivity());

        textinputedittextDescription[0] = myDialog.findViewById(R.id.textinputedittext_description);
        textinputedittextDescription[0].setText(cache.get(index).getDescription());

        getStartTime[0] = myDialog.findViewById(R.id.textview_start_time);
        getStartTime[0].setText(cache.get(index).getStart());

        getEndTime[0] = myDialog.findViewById(R.id.textview_end_time);
        getEndTime[0].setText(cache.get(index).getEnd());

        spinnerTemplate[0] = myDialog.findViewById(R.id.spinner_save_as);

        if(cache.get(index).getSaveAs().equals(stringArray[2])) {                                   //Set the spinner selection here
            spinnerTemplate[0].setSelection(2);
        }else{
            spinnerTemplate[0].setSelection(1);
        }
    }
}
