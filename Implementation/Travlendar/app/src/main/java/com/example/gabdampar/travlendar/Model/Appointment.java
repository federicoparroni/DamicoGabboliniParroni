package com.example.gabdampar.travlendar.Model;

/**
 * Created by gabdampar on 30/11/2017.
 */

import java.util.Date;

public class Appointment {

    public String name;
    public Date startingTime = null;
    public TimeSlot timeSlot = null;
    public int duration;
    public GeoCoordinate coords;


    public Appointment(String n, Date startingTime, int duration, GeoCoordinate coord) {
        this.name = n;
        this.startingTime = startingTime;
        this.duration = duration;
        this.coords = coord;
    }

    public Appointment(String n, TimeSlot timeSlot, int duration, GeoCoordinate coord) {
        this.name = n;
        this.timeSlot = timeSlot;
        this.duration = duration;
        this.coords = coord;
    }

    public boolean isDeterministic() {
        return timeSlot == null;
    }

    public Date endingTime() {
        return Date2.Add(startingTime, duration);
    }

    public String toString() {
        return this.name;
    }

}
