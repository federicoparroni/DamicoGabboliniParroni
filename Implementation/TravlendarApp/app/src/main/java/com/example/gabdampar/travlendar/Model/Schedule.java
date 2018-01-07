/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import android.support.annotation.NonNull;

import com.example.gabdampar.travlendar.Model.travelMean.PublicTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;


public class Schedule implements Comparable {

    public ArrayList<ScheduledAppointment> getScheduledAppts() {
        return scheduledAppts;
    }
    public LatLng startingLocation;

    private ArrayList<ScheduledAppointment> scheduledAppts = new ArrayList<>();
    public OptCriteria criteria;

    public LocalDate getDate(){
        return scheduledAppts.get(0).originalAppointment.getDate();
    }

    // properties
    private int totTravelTime = 0;      // sec
    private float totCost = 0;          // eur
    private float totCarbon = 0;        // mg
    private boolean canBuyTickets = false;

    public Schedule(ArrayList<TemporaryAppointment> apps) {
        for(TemporaryAppointment a : apps) {
            TravelMean m = a.means != null ? a.means.get(0).getMean() : null;
            scheduledAppts.add(new ScheduledAppointment(a.originalAppt, a.startingTime, a.ETA, m ));
        }

        // calculate time, cost, carbon
        for(int i=0; i < scheduledAppts.size() - 1; i++) {
            ScheduledAppointment a1 = scheduledAppts.get(i);
            ScheduledAppointment a2 = scheduledAppts.get(i+1);

            totTravelTime += (a2.ETA.getMillisOfDay() - a2.startingTime.getMillisOfDay()) / 1000;
            totCost += a2.travelMeanToUse.EstimateCost(a1.originalAppointment.coords, a2.originalAppointment.coords);
            totCarbon += a2.travelMeanToUse.EstimateCarbon(a1.originalAppointment.coords, a2.originalAppointment.coords);

            //TravelMeanEnum mean = a1.travelMeanToUse.meanEnum;
            if(a1.travelMeanToUse instanceof PublicTravelMean) canBuyTickets = true;
        }
    }

    public Schedule(ArrayList<TemporaryAppointment> apps, OptCriteria criteria) {
        this(apps);
        this.criteria = criteria;
        this.startingLocation=apps.get(0).originalAppt.coords;
    }

    public LocalTime getTotalTravelTime(){
        return new LocalTime(totTravelTime /3600, totTravelTime % 60);
    }

    public float getTotalCost() {
        return totCost /*/ 1000*/;
    }

    public float getTotalCarbon() {
        return totCarbon / 1000; //must count the g
    }

    public boolean canBuyTickets() {
        return canBuyTickets;
    }



    /**
     * String methods
     */

    public String toString() {
        StringBuilder res = new StringBuilder();

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

    public String toStringGraphical(){
        StringBuilder res = new StringBuilder();

        for(ScheduledAppointment appt : scheduledAppts) {
            if(appt.travelMeanToUse != null) {
                res.append(String.format("%s a.time: %s", appt.toString(), appt.ETA.toString("HH:mm")));
            }
        }
        return res.toString();
    }

    /**
     * Compare two Schedules
     * @param o: another schedule
     * @return compare between two float (based on optimization criteria)
     */
    @Override
    public int compareTo(@NonNull Object o) {
        Schedule s2 = (Schedule) o;
        switch (criteria) {
            case OPTIMIZE_TIME:
                return this.getTotalTravelTime().compareTo(s2.getTotalTravelTime());
            case OPTIMIZE_COST:
                return Float.compare( this.getTotalCost(), s2.getTotalCost() );
            case OPTIMIZE_CARBON:
                return Float.compare( this.getTotalCarbon(), s2.getTotalCarbon() );
        }
        return 1;
    }
}