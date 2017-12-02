/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.privateMeans;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;

public class Bike extends TravelMean {

    protected static float averageBikeSpeed	= 0.007f;

    public static Bike GetInstance() {
        if(instance == null) {
            instance = new Bike();
            TravelMean.MeansCollection.add(instance);
        }
        return (Bike)instance;
    }


    @Override
    public float EstimateTime(Appointment from, Appointment to) {
        return (float)Math.ceil(from.coords.distanceTo(to.coords)/averageBikeSpeed * 1.2f);
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to) {
        return 0;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to) {
        return 0;
    }

}