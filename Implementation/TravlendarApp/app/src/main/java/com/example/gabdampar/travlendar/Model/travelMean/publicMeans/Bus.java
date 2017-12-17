/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.PublicTravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

public class Bus extends PublicTravelMean {


    protected static final float AVG_SPEED = 0.025f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 150;   // g/km
    protected static final float TICKET_COST = 2;                    // euro

    protected static final float AVG_TIME_TO_STOP = 6*60;            // sec

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
    public float EstimateTime(Appointment from, Appointment to, float distance) {
        return distance * 1.2f + AVG_TIME_TO_STOP ;
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to, float distance) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to, float distance) {
        return distance * AVG_CARBON_EMISSION_PER_KM ;
    }


}