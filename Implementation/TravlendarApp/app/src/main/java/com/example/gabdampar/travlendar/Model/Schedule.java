/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;


public class Schedule {

    public ArrayList<ScheduledAppointment> getScheduledAppts() {
        return scheduledAppts;
    }

    private ArrayList<ScheduledAppointment> scheduledAppts = new ArrayList<>();
    private float cost;

    public Schedule(ArrayList<TemporaryAppointment> apps, float cost) {
        this.cost = cost;
        // TODO: convert TemporaryAppointments into ScheduledAppointments.. works??
        for(TemporaryAppointment a : apps) {
            scheduledAppts.add(new ScheduledAppointment(a.originalAppt, a.startingTime, a.ETA, a.means.get(0).getTravelMean()));
        }
    }

    public LocalDate getDate(){
        return scheduledAppts.get(0).originalAppointment.getDate();
    }

    public void PrintToConsole() {
        System.out.println("----- Schedule -----");
        for(ScheduledAppointment appt : scheduledAppts) {
            System.out.println(String.format("%s: %s --- %s --- %s", appt.toString(), appt.startingTime.toString(), appt.ETA.toString(), appt.endingTime().toString()));
        }
        System.out.println("------------");
    }

}