/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean.privateMeans;

import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

public class Car extends TravelMean {

    protected static final float AVG_SPEED = 0.02f;                  // km/h
    protected static final float AVG_CARBON_EMISSION_PER_KM = 120;   // g/km
    protected static final float GAS_CONSUMPTION = 5.5f / 100f;      // l/km
    protected static final float GAS_COST = 1.4f;                    // euro/l

    public Car() {
        super();
        super.descr= TravelMeanEnum.CAR;
    }


    public static Car GetInstance() {
        if(instance == null) {
            instance = new Car();
            TravelMean.MeansCollection.add(instance);
        }
        return (Car)instance;
    }

    @Override
    public float EstimateTime(Appointment from, Appointment to, float distance) {
        return distance/AVG_SPEED * 1.2f;
    }

    @Override
    public float EstimateCost(Appointment from, Appointment to, float distance) {
        return distance * GAS_CONSUMPTION * GAS_COST;
    }

    @Override
    public float EstimateCarbon(Appointment from, Appointment to, float distance) {
        return distance * AVG_CARBON_EMISSION_PER_KM;
    }

}