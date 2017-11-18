package it.polimi.gabdampar.travlendarapp.model;

import android.location.Location; // https://developer.android.com/reference/android/location/Location.html

import java.sql.Date; // https://developer.android.com/reference/java/sql/Date.html
import java.sql.Time; // https://docs.oracle.com/javase/7/docs/api/java/sql/Time.html

/**
 * Created by federico on 09/11/17.
 */

public class Appointment {
    private Time startingTime;
    private Time duration;
    private Time timeSlot; // penso vada creata la classe time slot senno non so che tipo deve essere quell'oggeto
    private Location location;
    private int numberOfInvolvedPeople;
    private Boolean isRecurrent;
    private Date date;

    //costruttore-------------------------------------------

    public Appointment(Time startingTime, Time duration, Time timeSlot, Location location,
                       int numberOfInvolvedPeople, Boolean isRecurrent, Date date){
        this.startingTime = startingTime;
        this.duration = duration;
        this.timeSlot = timeSlot;
        this.location = location;
        this.numberOfInvolvedPeople = numberOfInvolvedPeople;
        this.isRecurrent = isRecurrent;
        this.date = date;
    }

    // metodi SetAttribute()--------------------------------

    //metodi GetAttribute()---------------------------------

    public Location GetLocation(){
        return this.location;
    }
}
