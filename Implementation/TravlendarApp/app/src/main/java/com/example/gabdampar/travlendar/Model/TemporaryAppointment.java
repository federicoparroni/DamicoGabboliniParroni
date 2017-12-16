package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanCostCouple;

import org.joda.time.LocalTime;

import java.util.ArrayList;

/**
 * Created by federico on 16/12/17.
 */

public class TemporaryAppointment {

    public Appointment originalAppointment;
    public LocalTime startingTime;
    public LocalTime ETA;

    /** list of travel means ordered by cost */
    public ArrayList<TravelMeanCostCouple> means = new ArrayList<>();

    /** dummy contraint assigned to avoid unfeasibility */
    public ArrayList<ConstraintOnAppointment> incrementalConstraints = new ArrayList<>();

    /** true if this appointment is overlapping with the next */
    public boolean isConflicting;


    public TemporaryAppointment(Appointment originalAppointment, LocalTime startingTime, LocalTime ETA) {
        this.originalAppointment = originalAppointment;
        this.startingTime = startingTime;
        this.ETA = ETA;
    }

    public TemporaryAppointment(Appointment originalAppointment, LocalTime startingTime, LocalTime ETA, ArrayList<TravelMeanCostCouple> means) {
        this.originalAppointment = originalAppointment;
        this.startingTime = startingTime;
        this.ETA = ETA;
        this.means = means;
    }

    public LocalTime endingTime() {
        return ETA.plusSeconds(originalAppointment.duration);
    }

}