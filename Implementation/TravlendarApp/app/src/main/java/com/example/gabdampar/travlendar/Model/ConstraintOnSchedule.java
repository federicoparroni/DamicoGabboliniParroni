/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

import java.util.ArrayList;

public class ConstraintOnSchedule extends Constraint {

    public ArrayList<Weather> weather;
    public TimeSlot timeSlot;

    public ConstraintOnSchedule() {}

    public ConstraintOnSchedule(TravelMeanEnum mean, float maxDistance, ArrayList<Weather> weather, TimeSlot timeSlot) {
        this.mean = mean;
        this.maxDistance = maxDistance;
        this.weather = weather;
        this.timeSlot = timeSlot;
    }

}