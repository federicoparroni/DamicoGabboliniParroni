package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

/**
 * Created by federico on 11/12/17.
 */

public class Train extends TravelMean {

    protected static final float AVG_SPEED = 0.022f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 0;     // g/km
    protected static final float TICKET_COST = 1.6f;                 // euro

    protected static final float AVG_TIME_TO_STATION = 20 * 60;         // sec

    public static Train GetInstance() {
        if (instance == null) {
            instance = new Train();
            TravelMean.MeansCollection.add(instance);
        }
        return (Train) instance;
    }

    public Train() {
        super();
        super.descr = TravelMeanEnum.TRAIN;
    }

    @Override
    public float EstimateTime(Appointment from, Appointment to, float distance) {
        return distance / AVG_SPEED * 1.2f + AVG_TIME_TO_STATION;
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