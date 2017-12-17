/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.PublicTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Walk;
import com.google.android.gms.maps.model.LatLng;

public class Bus extends PublicTravelMean {


    protected static final float AVG_SPEED = 0.025f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 150;   // g/km
    protected static final float TICKET_COST = 1.50f;                    // euro

    //protected static final float AVG_TIME_TO_STOP = 6*60;            // sec

    public static Bus GetInstance() {
        if(instance == null) {
            instance = new Bus();
            TravelMean.MeansCollection.add(instance);
        }
        return (Bus)instance;
    }

    public Bus(){
        super();
        super.meanEnum = TravelMeanEnum.BUS;
    }

    @Override
    public float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        LatLng stationTo = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        float totalTime = 0;

        if(stationFrom == null || stationTo == null){
            return -1;
        }else{
            //distance travelled with bus
            totalTime += EstimateTime(stationFrom,stationTo);
            //distance from first appointment to the stop
            TravelMean meanFrom = from.means.get(0).getMean();
            totalTime += Walk.EstimateTime(from.originalAppt.coords,stationFrom);
            totalTime += Walk.EstimateTime(stationTo,to.originalAppt.coords);
        }
        return totalTime;
    }

    @Override
    public float EstimateTime(LatLng from, LatLng to) {
        return MapUtils.distance(from,to)*1.3f*AVG_SPEED;
    }

    @Override
    public float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(TemporaryAppointment from, TemporaryAppointment to, float distance) {
        LatLng stationFrom = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        LatLng stationTo = from.originalAppt.distanceOfEachTransitStop.get(TravelMeanEnum.BUS);
        return MapUtils.distance(stationFrom,stationTo) * AVG_CARBON_EMISSION_PER_KM ;
    }


}