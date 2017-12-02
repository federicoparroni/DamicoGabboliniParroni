/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.here.android.mpa.common.GeoCoordinate;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class Appointment {

    public String name;
    public LocalDate date;
    public LocalTime startingTime = null;
    public TimeSlot timeSlot = null;
    public int duration;    // seconds
    public int involvedPeople = 0;
    public GeoCoordinate coords;


    public Appointment(String n, LocalDate date, LocalTime startingTime, int duration, GeoCoordinate coord) {
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

    public LocalTime endingTime() {
        return startingTime.plusSeconds(duration);
    }

    public String toString() {
        return this.name;
    }

}