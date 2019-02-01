package com.hauff.patrick.betterme.habit_statistics;

import java.io.Serializable;

/**
 * Our Statistics object to save our Habits with their planned and successfully activities
 */
public class HabitStatistics  implements Serializable {

    private String habit;                                                                           //Our habit
    private int numberOfPlannedDays;                                                                //Stored our planned Days with habits
    private int numberOfSuccessfullyPlannedDays;                                                    //Stored our successfully planned days with habits

    /**
     * Our Constructor to create objects
     *
     * @param habit habit
     * @param numberOfPlannedDays numberOfPlannedDays
     * @param numberOfSuccessfullyPlannedDays numberOfSuccessfullyPlannedDays
     */
    public HabitStatistics(String habit, int numberOfPlannedDays,
                           int numberOfSuccessfullyPlannedDays){
        this.habit = habit;
        this.numberOfPlannedDays = numberOfPlannedDays;
        this.numberOfSuccessfullyPlannedDays = numberOfSuccessfullyPlannedDays;
    }

    /**
     * To get our habit
     *
     * @return habit habit
     */
    public String getHabit() {
        return habit;
    }

    /**
     * To get the number of our planned days
     *
     * @return numberOfPlannedDays
     */
    public int getNumberOfPlannedDays() {
        return numberOfPlannedDays;
    }

    /**
     * To set the number of our planned days
     *
     * @param numberOfPlannedDays numberOfPlannedDays
     */
    public void setNumberOfPlannedDays(int numberOfPlannedDays) {
        this.numberOfPlannedDays = numberOfPlannedDays;
    }

    /**
     * To get the number of our successully planned days
     *
     * @return numberOfPlannedDays numberOfPlannedDays
     */
    public int getNumberOfSuccessfullyPlannedDays() {
        return numberOfSuccessfullyPlannedDays;
    }

    /**
     * To set the number of our successully planned days
     *
     * @param numberOfSuccessfullyPlannedDays numberOfSuccessfullyPlannedDays
     */
    public void setNumberOfSuccessfullyPlannedDays(int numberOfSuccessfullyPlannedDays) {
        this.numberOfSuccessfullyPlannedDays = numberOfSuccessfullyPlannedDays;
    }
}
