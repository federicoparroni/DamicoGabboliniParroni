/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean;

import com.example.gabdampar.travlendar.Controller.IMeanEvaluable;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Bike;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Car;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Walk;
import com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Bus;
import com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Metro;
import com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Train;
import com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Tram;

import java.util.ArrayList;

public abstract class TravelMean implements IMeanEvaluable {

    protected static TravelMean instance;
    public TravelMeanEnum descr;

    public int availableSeats;

    // provides a list of all instantiated travel means
    public static ArrayList<TravelMean> MeansCollection;

    public static TravelMean GetInstance() {
        throw new IllegalAccessError("Cannot call this method");
    }

    public static TravelMean getTravelMean(TravelMeanEnum meanType) {
        switch (meanType) {
            case WALK:
                return Walk.GetInstance();
            case BIKE:
                return Bike.GetInstance();
            case CAR:
                return Car.GetInstance();
            case TRAIN:
                return Train.GetInstance();
            case TRAM:
                return Tram.GetInstance();
            case BUS:
                return Bus.GetInstance();
            case METRO:
                return Metro.GetInstance();
        }
        return null;
    }

}