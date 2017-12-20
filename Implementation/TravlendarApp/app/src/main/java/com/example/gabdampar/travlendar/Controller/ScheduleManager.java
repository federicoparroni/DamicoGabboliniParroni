package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Schedule;

import java.util.ArrayList;

/**
 * Created by federico on 13/12/17.
 */

public class ScheduleManager {

    public ArrayList<Schedule> schedulesList = new ArrayList<>();

    /**
     * TODO: set it equals to the actual running schedule
     */
    public Schedule runningSchedule=null;

    // singleton
    private static ScheduleManager instance;
    public static ScheduleManager GetInstance() {
        if(instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public Schedule getSchedule(int position){
        return schedulesList.get(position);
    }



}
