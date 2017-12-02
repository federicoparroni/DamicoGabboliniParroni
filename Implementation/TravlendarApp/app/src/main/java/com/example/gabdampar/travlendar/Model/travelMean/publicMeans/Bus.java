/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.publicMeans;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;

public class Bus extends TravelMean {

    protected static final float AVG_SPEED = 0.014f;                 // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 150;   // g/km
    protected static final float TICKET_COST = 2;                    // euro

    public static Bus GetInstance() {
        if(instance == null) {
            instance = new Bus();
            TravelMean.MeansCollection.add(instance);
        }
        return (Bus)instance;
    }


    @Override
    public float EstimateTime(Appointment from, Appointment to) {
        return (float)( from.coords.distanceTo(to.coords)/AVG_SPEED * 1.2f );
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to) {
        return TICKET_COST;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to) {
        return (float)( from.coords.distanceTo(to.coords) * AVG_CARBON_EMISSION_PER_KM );
    }


}