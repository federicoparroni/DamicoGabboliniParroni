package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;

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

    public String getDirectionForRunningSchedule(){
        String directions=new String();
        if(runningSchedule==null){
            return directions;
        }
        else{
            directions += "Schedule directions: \n \n";
            for(int i=0; i<ScheduleManager.GetInstance().runningSchedule.getScheduledAppts().size(); i++) {
                ScheduledAppointment scheduledAppointment = ScheduleManager.GetInstance().runningSchedule.getScheduledAppts().get(i);
                if (scheduledAppointment.dataFromPreviousToThis != null) {
                    directions += "Start at " + scheduledAppointment.dataFromPreviousToThis.getTime().startingTime.toString("HH:mm")+"\n";
                    directions += scheduledAppointment.dataFromPreviousToThis.getDirections();
                    directions += "Estimate arrival time: " + scheduledAppointment.dataFromPreviousToThis.getTime().endingTime.toString("HH:mm") + "\n \n";
                }
                directions += scheduledAppointment.toString() + " [" + scheduledAppointment.ETA.toString("HH:mm") + " - " + scheduledAppointment.endingTime().toString("HH:mm") + "] \n \n";
            }
        }
        return directions;
    }
}
