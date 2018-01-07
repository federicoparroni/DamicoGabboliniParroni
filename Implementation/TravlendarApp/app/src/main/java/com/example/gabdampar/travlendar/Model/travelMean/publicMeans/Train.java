package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import android.graphics.Color;

import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.PublicTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Bike;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Walk;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by federico on 11/12/17.
 */

public class Train extends PublicTravelMean {

    private static Train instance;


    protected static final float AVG_SPEED = 13.6f;                 // m/s
    protected static final float AVG_CARBON_EMISSION_PER_KM = 30;   // g/km
    protected static final float TICKET_COST = 1.5f;                // euro
    protected static final float AVG_WAITING_SEC = 15*60f;          // sec

    public static Train GetInstance() {
        if (instance == null) {
            instance = new Train();
            TravelMean.MeansCollection.add(instance);
        }
        color=Color.RED;
        return instance;
    }

    public Train() {
        super();
        super.meanEnum = TravelMeanEnum.TRAIN;
    }


    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.TRAIN);
        LatLng stationTo = to.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.TRAIN);
        float totalTime = 0;

        if(stationFrom == null || stationTo == null || stationFrom.equals(stationTo)){
            return -1;
        }else{
            //distance travelled with bus
            totalTime += EstimateTime(stationFrom,stationTo);
            //distance from first appointment to the stop
            totalTime += Walk.GetInstance().EstimateTime(from.originalAppt.coords,stationFrom);
            totalTime += Walk.GetInstance().EstimateTime(stationTo,to.originalAppt.coords);
        }
        return totalTime;
    }
    @Override
    public float EstimateTime(LatLng from, LatLng to) {
        return (MapUtils.distance(from,to)*1.1f)/AVG_SPEED + AVG_WAITING_SEC;
    }

    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        if(IdentityManager.GetInstance().user.hasPass) return 0f;
        else return TICKET_COST;
    }
    @Override
    public float EstimateCost(LatLng from, LatLng to) {
        if(IdentityManager.GetInstance().user.hasPass) return 0f;
        else return TICKET_COST;
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