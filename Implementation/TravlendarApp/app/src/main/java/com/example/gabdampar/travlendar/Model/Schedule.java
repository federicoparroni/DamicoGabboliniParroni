/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import java.util.ArrayList;


public class Schedule {

    public ArrayList<ScheduledAppointment> appointments;

    public Schedule(ArrayList<ScheduledAppointment> apps) {
        // TODO Auto-generated constructor stub
        this.appointments = apps;
    }

    public void PrintToConsole() {
        System.out.println("----- Schedule -----");
        for(ScheduledAppointment appt : appointments) {
            System.out.println(String.format("%s: %s --- %s --- %s", appt.toString(), appt.startingTime.toString(), appt.ETA.toString(), appt.endingTime().toString()));
        }
        System.out.println("------------");
    }

}