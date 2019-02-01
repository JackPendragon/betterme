package com.hauff.patrick.betterme.list_adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hauff.patrick.betterme.R;
import com.hauff.patrick.betterme.entry.Entry;
import com.hauff.patrick.betterme.shared_prefs.MySharedPreferences;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Our entry list adapter to show our day activities in our listview
 */
public class EntryListAdapter extends ArrayAdapter<Entry> {

    private final String SHARED_PREFERENCES_TEMPLATES = "sharedPreferencesTemplates";                       //SharedPrefs name
    private Context mContext;                                                                       //Our Context
    private int mResource;                                                                          //Our resource

    /**
     * Our EntryListAdapter object constructor to crete an object
     *
     * @param context context
     * @param resource resource
     * @param entrys entrys
     */
    public EntryListAdapter(Context context, int resource, ArrayList<Entry> entrys) {
        super(context, resource, entrys);
        mContext = context;
        mResource = resource;
    }

    /**
     * Method to fill our listview with our planned activities
     *
     * @param position position
     * @param convertView convertView
     * @param parent parent
     * @return convertView convertView
     */
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String startTime = Objects.requireNonNull(getItem(position)).getStart();                                            //get our variables
        String endTime = Objects.requireNonNull(getItem(position)).getEnd();
        String activity = Objects.requireNonNull(getItem(position)).getActivity();
        String description = Objects.requireNonNull(getItem(position)).getDescription();
        String saveAs = Objects.requireNonNull(getItem(position)).getSaveAs();
        Boolean done;

        if(Objects.requireNonNull(getItem(position)).isDone() == null) done = false;                                        //Save the case when our isDone variable is null
        else done = Objects.requireNonNull(getItem(position)).isDone();

        LayoutInflater inflater = LayoutInflater.from(mContext);                                    //Our inflater for our listview
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewStartTime = convertView.findViewById(R.id.textview_start_time); //get our textviews
        TextView textViewEndTime = convertView.findViewById(R.id.textview_end_time);
        TextView textViewActivity =convertView.findViewById(R.id.textview_activity);
        TextView textViewDescription = convertView.findViewById(R.id.textview_description);
        ImageView isDone = convertView.findViewById(R.id.imgeview_isdone);

        //Our template list
        ArrayList<Entry> activityFromTemplatesList = new ArrayList<>();
        //SharedPrefs object
        MySharedPreferences mySharedPreferences = new MySharedPreferences(mContext, SHARED_PREFERENCES_TEMPLATES);
        activityFromTemplatesList = mySharedPreferences
                .getEntrySharedPrefs(activity, activityFromTemplatesList);

        String[] stringArray = convertView.getResources().getStringArray(R.array.save_as);          //Get String array from strings.xml

        if(activityFromTemplatesList !=null){                                                        //Set our saveAs to the save as from SharedPrefs
            for(int i = 0; i < activityFromTemplatesList.size(); i++){
                String saveAsFromPrefs = activityFromTemplatesList.get(i).getSaveAs();
                if(saveAsFromPrefs.equals(stringArray[2])){
                    saveAs = saveAsFromPrefs;
                }else if(saveAsFromPrefs.equals(stringArray[0])){
                    saveAs = saveAsFromPrefs;
                }
            }
        }

        if(saveAs.equals(stringArray[2])){                                                          //Set our activity Icon in listview
            View view = convertView.findViewById(R.id.view_activity_coloration);
            view.setBackgroundResource(R.mipmap.new_activity);
        }else if(saveAs.equals(stringArray[0])){
            View view = convertView.findViewById(R.id.view_activity_coloration);
            view.setBackgroundResource(R.mipmap.template);
        }

        textViewStartTime.setText(startTime);                                                       //Set our parameters in our list
        textViewEndTime.setText(endTime);
        textViewActivity.setText(activity);
        textViewDescription.setText(description);
        if(done) isDone.setImageResource(R.mipmap.checked);
        else isDone.setImageResource(R.mipmap.checked_grey);

        return convertView;                                                                         //return our convertview
    }
}
