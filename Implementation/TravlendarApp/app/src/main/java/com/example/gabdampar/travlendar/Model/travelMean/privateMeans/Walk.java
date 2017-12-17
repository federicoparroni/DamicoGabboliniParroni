package com.example.gabdampar.travlendar.Model.travelMean.privateMeans;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.PrivateTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

/**
 * Created by federico on 11/12/17.
 */

public class Walk extends PrivateTravelMean {


    protected static final float AVG_SPEED = 0.007f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 0;     // g/km
    protected static final float TICKET_COST = 0;                   // euro

    public static Walk GetInstance() {
        if (instance == null) {
            instance = new com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Metro();
            TravelMean.MeansCollection.add(instance);
        }
        return (Walk) instance;
    }

    public Walk() {
        super();
        super.meanEnum = TravelMeanEnum.WALK;
    }

    @Override
    public float EstimateTime(Appointment from, Appointment to, float distance) {
        return distance / AVG_SPEED * 1.2f ;
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to, float distance) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to, float distance) {
        return distance * AVG_CARBON_EMISSION_PER_KM;
    }


}