package com.example.gabdampar.travlendar.Model;

/**
 * Created by gabdampar on 30/11/2017.
 */

import java.util.ArrayList;


public class ConstraintOnSchedule extends Constraint {

    ArrayList<Weather> weather;
    TimeSlot timeSlot;

    public ConstraintOnSchedule(TravelMean mean, float maxDistance, ArrayList<Weather> weather, TimeSlot timeSlot) {
        this.mean = mean;
        this.maxDistance = maxDistance;
        this.weather = weather;
        this.timeSlot = timeSlot;
    }

}
