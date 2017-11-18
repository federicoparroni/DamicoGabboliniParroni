package it.polimi.gabdampar.travlendarapp.model;

import android.location.Location;

import java.sql.Time;

/**
 * Created by gabbo on 12/11/2017.
 */

public class ScheduledAppointment {

    private Time startingTime;
    private Time duration;
    private Time eta;
    private Location location;
    private Appointment appointment;

    public ScheduledAppointment(Time startingTime, Time duration, Time eta, Appointment appointment){
        this.startingTime = startingTime;
        this.duration = duration;
        this.eta = eta;
        this.location = appointment.GetLocation();
    }
}
