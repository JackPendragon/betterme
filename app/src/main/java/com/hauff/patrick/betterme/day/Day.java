package com.hauff.patrick.betterme.day;

import com.hauff.patrick.betterme.entry.Entry;

import java.util.ArrayList;

/**
 * Day constructor to create single days with their activities
 */
public class Day {

    private String date;                                                                            //to save our choosen date
    private ArrayList<Entry> entryList;                                                             //to save our days

    /**
     * Constructor for our Day object
     *
     * @param date date
     * @param entryList entryList
     */
    public Day(String date, ArrayList<Entry> entryList){
        this.date = date;
        this.entryList = entryList;
    }
}
