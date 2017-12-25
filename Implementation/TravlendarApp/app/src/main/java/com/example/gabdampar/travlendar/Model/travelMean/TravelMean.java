/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean;

import android.graphics.Color;

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

    public TravelMeanEnum meanEnum;
    public static int color;

    public int availableSeats;

    // provides a list of all instantiated travel means
    public static ArrayList<TravelMean> MeansCollection = new ArrayList<>();

    public static TravelMean GetInstance() {
        throw new IllegalAccessError("Cannot call this method");
    }

    public static TravelMean getTravelMean(TravelMeanEnum meanType) {
        TravelMean res = null;
        switch (meanType) {
            case WALK:
                res = Walk.GetInstance();
                break;
            case BIKE:
                res = Bike.GetInstance();
                break;
            case CAR:
                res = Car.GetInstance();
                break;
            case TRAIN:
                res = Train.GetInstance();
                break;
            case TRAM:
                res = Tram.GetInstance();
                break;
            case BUS:
                res = Bus.GetInstance();
                break;
            case METRO:
                res = Metro.GetInstance();
                break;
        }
        return res;
    }

    public static boolean isMeanUsable(TravelMeanEnum actualMeanE, TravelMeanEnum futureMeanE){
        if(actualMeanE == null) return true;

        if(getTravelMean(actualMeanE) instanceof PublicTravelMean){
            if (getTravelMean(futureMeanE) instanceof PublicTravelMean){
                return true;
            }else{
                return false;
            }
        }else{
            if (getTravelMean(futureMeanE) instanceof PublicTravelMean){
                return true;
            }else{
                return actualMeanE == futureMeanE;
            }
        }
    }

    public String toString() {
        return meanEnum.toString();
    }

}