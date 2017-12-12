/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

public class ConstraintOnAppointment extends Constraint {

    public ConstraintOnAppointment(TravelMeanEnum mean, float maxDistance){
        this.mean = mean;
        this.maxDistance = maxDistance;
    }
}