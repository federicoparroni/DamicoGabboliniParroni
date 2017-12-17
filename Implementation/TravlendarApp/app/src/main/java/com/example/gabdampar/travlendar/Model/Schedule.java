/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import java.util.ArrayList;


public class Schedule {

    private ArrayList<TemporaryAppointment> appts;
    private ArrayList<ScheduledAppointment> scheduledAppts;

    public Schedule(ArrayList<TemporaryAppointment> apps) {
        this.appts = apps;

        // TODO: convert TemporaryAppointments into ScheduledAppointments
        // TODO ...
    }

    public void PrintToConsole() {
        System.out.println("----- Schedule -----");
        for(ScheduledAppointment appt : scheduledAppts) {
            System.out.println(String.format("%s: %s --- %s --- %s", appt.toString(), appt.startingTime.toString(), appt.ETA.toString(), appt.endingTime().toString()));
        }
        System.out.println("------------");
    }

}