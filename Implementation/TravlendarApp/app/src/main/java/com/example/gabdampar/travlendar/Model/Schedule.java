/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;

import java.util.ArrayList;


public class Schedule {

    private ArrayList<ScheduledAppointment> scheduledAppts = new ArrayList<>();

    private float cost;

    public Schedule(ArrayList<TemporaryAppointment> apps, float cost) {
        this.cost = cost;

        // TODO: convert TemporaryAppointments into ScheduledAppointments.. works??
        for(TemporaryAppointment a : apps) {
            TravelMean m = a.means != null ? a.means.get(0).getTravelMean() : null;
            scheduledAppts.add(new ScheduledAppointment(a.originalAppt, a.startingTime, a.ETA, m ));
        }
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("----- Schedule -----\n");
        for(ScheduledAppointment appt : scheduledAppts) {
            if(appt.travelMeanToUse != null) {
                res.append(String.format("| %s: %s --%s-- %s --- %s | ", appt.toString(), appt.startingTime.toString("HH:mm"), appt.travelMeanToUse.toString(), appt.ETA.toString("HH:mm"), appt.endingTime().toString("HH:mm")));
            } else {
                res.append(String.format("| %s: %s |", appt.toString(), appt.endingTime().toString("HH:mm") ));
            }
        }
        res.append("\n------------");
        return res.toString();
    }

}