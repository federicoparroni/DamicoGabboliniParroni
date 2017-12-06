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
    public boolean isRecurrent = false;


    public Appointment(String n, LocalDate date, LocalTime startingTime, int duration, GeoCoordinate coord) {
        this.name = n;
        this.startingTime = startingTime;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
    }

    public Appointment(String n, LocalDate date, TimeSlot timeSlot, int duration, GeoCoordinate coord) {
        this.name = n;
        this.timeSlot = timeSlot;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
    }

    public Appointment(String n, LocalDate date,LocalTime startingTime, TimeSlot timeSlot, int duration, GeoCoordinate coord,
                       int involvedPeople, boolean isRecurrent) {
        this.name = n;
        this.timeSlot = timeSlot;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
        this.startingTime = startingTime;
        this.involvedPeople = involvedPeople;
        this.isRecurrent = isRecurrent;
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

    public LocalDate getDate(){
        return this.date;
    }

    public TimeSlot getTimeSlot(){
        return this.timeSlot;
    }

    public LocalTime getStartingTime(){
        return this.startingTime;
    }

    public int getDuration(){
        return this.duration;
    }

    public void EditAppointment(String n, LocalDate date, LocalTime startingTime,TimeSlot timeSlot,
                                             int duration, GeoCoordinate coord,int involvedPeople,Boolean isRecurrent){
        this.name = n;
        this.startingTime = startingTime;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
        this.timeSlot = timeSlot;
        this.isRecurrent = isRecurrent;
        this.involvedPeople = involvedPeople;
    }

}