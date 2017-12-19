/**
 * Created by gabdampar on 01/12/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;

import org.joda.time.LocalTime;

public class ScheduledAppointment {

    public Appointment originalAppointment;
    public LocalTime startingTime;
    public LocalTime ETA;
    public TravelMean travelMeanToUse;
    public TravelOptionData dataFromPreviousToThis;

    public ScheduledAppointment(Appointment appt, LocalTime startingTime, LocalTime ETA, TravelMean mean) {
        this.originalAppointment = appt;
        this.startingTime = startingTime;
        this.ETA = ETA;
        this.travelMeanToUse = mean;
    }

    public LocalTime endingTime() {
        return ETA.plusSeconds(originalAppointment.duration);
    }

    public String toString() {
        return originalAppointment.toString();
    }

}