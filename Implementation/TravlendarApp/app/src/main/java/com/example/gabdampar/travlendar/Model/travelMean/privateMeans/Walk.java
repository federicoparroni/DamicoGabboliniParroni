package com.example.gabdampar.travlendar.Model.travelMean.privateMeans;

import android.graphics.Color;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.PrivateTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by federico on 11/12/17.
 */

public class Walk extends PrivateTravelMean {

    private static Walk instance;

    protected static final float AVG_SPEED = 1.27f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 0;     // g/km
    protected static final float TICKET_COST = 0;                   // euro

    public static Walk GetInstance() {
        if (instance == null) {
            instance = new Walk();
            TravelMean.MeansCollection.add(instance);
        }
        color=Color.BLUE;
        return instance;
    }

    public Walk() {
        super();
        super.meanEnum = TravelMeanEnum.WALK;
    }


    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance / AVG_SPEED * 1.2f ;
    }
    @Override
    public float EstimateTime(LatLng from, LatLng to) {
        return MapUtils.distance(from,to) / AVG_SPEED * 1.2f ;
    }

    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return TICKET_COST;
    }
    @Override
    public float EstimateCost(LatLng from, LatLng to) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(LatLng from, LatLng to) {
        float distance = MapUtils.distance(from, to);
        return distance * AVG_CARBON_EMISSION_PER_KM;
    }
    @Override
    public float EstimateCarbon(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance * AVG_CARBON_EMISSION_PER_KM;
    }


}