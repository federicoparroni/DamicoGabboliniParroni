/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.privateMeans;

import android.graphics.Color;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.PrivateTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

public class Car extends PrivateTravelMean {

    private static Car instance;

    protected static final float AVG_SPEED = 3.5f;                    // m/s
    protected static final float AVG_CARBON_EMISSION_PER_KM = 120;   // g/km
    protected static final float GAS_CONSUMPTION = 5.5f / 100f;      // l/km
    protected static final float GAS_COST = 1.4f/1000f;              // euro/l

    public Car() {
        super();
        super.meanEnum = TravelMeanEnum.CAR;
    }

    public static Car GetInstance() {
        if(instance == null) {
            instance = new Car();
            TravelMean.MeansCollection.add(instance);
        }
        color=Color.DKGRAY;
        return instance;
    }


    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance/AVG_SPEED * 1.2f;
    }
    @Override
    public float EstimateTime(LatLng from, LatLng to) {
        return MapUtils.distance(from, to)/AVG_SPEED * 1.2f;
    }

    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance * GAS_CONSUMPTION * GAS_COST;
    }
    @Override
    public float EstimateCost(LatLng from, LatLng to) {
        float distance = MapUtils.distance(from, to);
        return distance * GAS_CONSUMPTION * GAS_COST;
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