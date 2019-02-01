package com.hauff.patrick.betterme.entry;

import java.io.Serializable;

/**
 * Entry class to create entry objects for our day object
 */
public class Entry implements Serializable {

    private String act;                                                                             //String for our activity
    private String sta;                                                                             //String for our start time
    private String en;                                                                              //String for our end time
    private String des;                                                                             //String for our description
    private String sav;                                                                             //String to save our "save as"
    private Boolean don;                                                                            //String to check our activity is done

    /**
     * Entry constructor with all parameters
     *
     * @param activity activity
     * @param start start
     * @param end end
     * @param description description
     * @param saveAs saveAs
     * @param done done
     */
    public Entry(String activity, String start, String end,
                 String description, String saveAs, Boolean done){
        this.act = activity;
        this.sta = start;
        this.en = end;
        this.des = description;
        this.sav = saveAs;
        this.don = done;
    }

    /**
     * get our activity
     *
     * @return act act
     */
    public String getActivity(){
        return act;
    }

    /**
     * get our start time
     *
     * @return sta sta
     */
    public String getStart(){
        return sta;
    }

    /**
     * get our end time
     *
     * @return en en
     */
    public String getEnd(){
        return en;
    }

    /**
     * get our description
     *
     * @return des des
     */
    public String getDescription(){
        return des;
    }

    /**
     * get our save as state
     * @return sav sav
     */
    public String getSaveAs(){
        return sav;
    }

    /**
     * check is the activity done
     *
     * @return don don
     */
    public Boolean isDone() {
        return this.don;
    }

    /**
     * To set our activity
     *
     * @param activity activity
     */
    public void setActivity(String activity){
        act = activity;
    }

    /**
     * To set our activity done
     *
     * @param done done
     */
    public void setDone(Boolean done) {
        this.don = done;
    }
}
