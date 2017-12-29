package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanCostTimeInfo;

import org.joda.time.LocalTime;

import java.util.ArrayList;

/**
 * Created by federico on 16/12/17.
 */

public class TemporaryAppointment {

    public Appointment originalAppt;
    public LocalTime startingTime;
    public LocalTime ETA;

    /** list of travel means ordered by cost */
    public ArrayList<TravelMeanCostTimeInfo> means = new ArrayList<>();

    /** dummy contraint assigned to avoid unfeasibility */
    public ArrayList<ConstraintOnAppointment> incrementalConstraints = new ArrayList<>();

    /** true if this appointment is overlapping with the next */
    public boolean isTimeConflicting;

    /** true if this appointment is using a mean that violates the Km constraint */
    public boolean isMeanConflicting;

    /** true if this appointment is using a mean that violates the Km constraint */

    /**
     * Create list of TemporaryAppointment from an arrangement
     * @param arrangement: list of (ordered) appointment
     * @return: list of TemporaryAppointment
     */
    public static ArrayList<TemporaryAppointment> Create (ArrayList<Appointment> arrangement) {
        ArrayList<TemporaryAppointment> result = new ArrayList<>();
        for(Appointment a : arrangement) {
            result.add(new TemporaryAppointment(a));
        }
        return result;
    }

    public TemporaryAppointment(Appointment originalAppt) {
        this.originalAppt = originalAppt;
    }

    public TemporaryAppointment(Appointment originalAppt, LocalTime startingTime, LocalTime ETA, ArrayList<TravelMeanCostTimeInfo> means) {
        this(originalAppt);
        this.startingTime = startingTime;
        this.ETA = ETA;
        this.means = means;
    }

    public void Set(Appointment originalAppointment, LocalTime startingTime, LocalTime ETA, ArrayList<TravelMeanCostTimeInfo> means) {
        this.originalAppt = originalAppointment;
        this.startingTime = startingTime;
        this.ETA = ETA;
        this.means = means;
    }

    public LocalTime endingTime() {
        return ETA.plusSeconds(originalAppt.duration);
    }

}