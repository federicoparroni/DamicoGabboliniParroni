package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

/**
 * Created by federico on 11/12/17.
 */

public class Tram extends TravelMean {

    protected static final float AVG_SPEED = 0.015f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 0;     // g/km
    protected static final float TICKET_COST = 1.6f;                 // euro

    protected static final float AVG_TIME_TO_STOP = 6*60;            // sec

    public static Tram GetInstance() {
        if(instance == null) {
            instance = new Tram();
            TravelMean.MeansCollection.add(instance);
        }
        return (Tram)instance;
    }

    public Tram() {
        super();
        super.descr= TravelMeanEnum.TRAM;
    }

    @Override
    public float EstimateTime(Appointment from, Appointment to) {
        return (float)( MapUtils.distance(from.coords, to.coords)/AVG_SPEED * 1.2f + AVG_TIME_TO_STOP);
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to) {
        return MapUtils.distance(from.coords, to.coords) * AVG_CARBON_EMISSION_PER_KM ;
    }


}