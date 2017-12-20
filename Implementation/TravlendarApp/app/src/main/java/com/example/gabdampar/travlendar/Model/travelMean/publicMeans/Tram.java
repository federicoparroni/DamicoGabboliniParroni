package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
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

public class Tram extends PublicTravelMean {

    private static Tram instance;


    protected static final float AVG_SPEED = 6.94f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 0;     // g/km
    protected static final float TICKET_COST = 1.5f;                 // euro

    public static Tram GetInstance() {
        if(instance == null) {
            instance = new Tram();
            TravelMean.MeansCollection.add(instance);
        }
        return instance;
    }

    public Tram() {
        super();
        super.meanEnum = TravelMeanEnum.TRAM;
    }

    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.TRAM);
        LatLng stationTo = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.TRAM);
        float totalTime = 0;

        if(stationFrom == null || stationTo == null){
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
        return (MapUtils.distance(from,to)*1.3f)/AVG_SPEED;
    }


    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return distance * AVG_CARBON_EMISSION_PER_KM ;
    }


}