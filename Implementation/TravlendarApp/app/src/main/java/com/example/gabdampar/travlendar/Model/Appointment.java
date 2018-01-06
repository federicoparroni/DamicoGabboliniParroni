/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;

public class Appointment {

    public String name;
    public LocalDate date;
    public LocalTime startingTime = null;
    public TimeSlot timeSlot = null;
    public int duration = -1;    // seconds
    public int involvedPeople = 0;
    public LatLng coords;
    public boolean isRecurrent = false;
    public String location;
    public ArrayList<ConstraintOnAppointment> constraints = new ArrayList<>();
    public HashMap<TravelMeanEnum,LatLng> distanceOfEachTransitStop = new HashMap<>();

    public Appointment(String n, LocalDate date, LocalTime startingTime, int duration, LatLng coord) {
        this.name = n;
        this.startingTime = startingTime;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
    }

    public Appointment(String n, LocalDate date, TimeSlot timeSlot, int duration, LatLng coord) {
        this.name = n;
        this.timeSlot = timeSlot;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
    }

    public Appointment(String n, LocalDate date,LocalTime startingTime, TimeSlot timeSlot, int duration, LatLng coord, String location,
                       int involvedPeople, boolean isRecurrent) {
        this.name = n;
        this.timeSlot = timeSlot;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
        this.startingTime = startingTime;
        this.involvedPeople = involvedPeople;
        this.isRecurrent = isRecurrent;
        this.location = location;
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

    public String getStringDuration() {
        int hours = (int)Math.floor(duration / 3600);
        int minutes = (duration / 60) % 60;

        if(hours > 0) {
            return String.format("%dh %dmin", hours, minutes);
        } else {
            return String.format("%dmin", minutes);
        }
    }

    public void EditAppointment(String n, LocalDate date, LocalTime startingTime,TimeSlot timeSlot,
                                             int duration, LatLng coord, String location, int involvedPeople,Boolean isRecurrent){
        this.name = n;
        this.startingTime = startingTime;
        this.duration = duration;
        this.coords = coord;
        this.date = date;
        this.timeSlot = timeSlot;
        this.isRecurrent = isRecurrent;
        this.involvedPeople = involvedPeople;
        this.location = location;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getLocation(){
        return this.location;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }

    public ArrayList<ConstraintOnAppointment> getConstraints(){
        return constraints;
    }

    public void setConstraints(ArrayList<ConstraintOnAppointment> constraints){
        this.constraints = constraints;
    }


}