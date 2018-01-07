/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.PublicTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Walk;
import com.google.android.gms.maps.model.LatLng;

public class Bus extends PublicTravelMean {

    private static Bus instance;

    protected static final float AVG_SPEED = 2.5f;                   // m/s
    protected static final float AVG_CARBON_EMISSION_PER_KM = 4;     // g/km
    protected static final float TICKET_COST = 1.50f;                // euro
    protected static final float AVG_WAITING_SEC = 4*60f;            // sec

    public static Bus GetInstance() {
        if(instance == null) {
            instance = new Bus();
            TravelMean.MeansCollection.add(instance);
        }
        color = Color.YELLOW;
        return instance;
    }

    public Bus(){
        super();
        super.meanEnum = TravelMeanEnum.BUS;
    }


    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        LatLng stationTo = to.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
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
        return (MapUtils.distance(from,to)*1.3f)/AVG_SPEED + AVG_WAITING_SEC;
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
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        LatLng stationTo = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        return MapUtils.distance(stationFrom, stationTo) * AVG_CARBON_EMISSION_PER_KM ;
    }


}