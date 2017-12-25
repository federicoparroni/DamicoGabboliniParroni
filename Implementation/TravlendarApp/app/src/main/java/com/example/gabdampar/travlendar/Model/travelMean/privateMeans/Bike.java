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

public class Bike extends PrivateTravelMean {

    private static Bike instance;

    protected static float averageBikeSpeed	= 2.84f;

    public Bike() {
        super();
        super.meanEnum = TravelMeanEnum.BIKE;
    }

    public static Bike GetInstance() {
        if(instance == null) {
            instance = new Bike();
            TravelMean.MeansCollection.add(instance);
        }
        color=Color.MAGENTA;
        return instance;
    }


    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance / averageBikeSpeed * 1.2f;
    }
    @Override
    public float EstimateTime(LatLng from, LatLng to) {
        return MapUtils.distance(from, to)/ averageBikeSpeed * 1.2f;
    }


    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return 0;
    }
    @Override
    public float EstimateCost(LatLng from, LatLng to) {
        return 0;
    }


    @Override
    public float EstimateCarbon(LatLng from, LatLng to) {
        return 0;
    }
    @Override
    public float EstimateCarbon(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return 0;
    }

}