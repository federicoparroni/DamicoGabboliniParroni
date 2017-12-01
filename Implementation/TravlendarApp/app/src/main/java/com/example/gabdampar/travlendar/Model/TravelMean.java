package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Controller.IMeanEvaluable;

import java.util.ArrayList;

/**
 * Created by gabdampar on 30/11/2017.
 */

public abstract class TravelMean implements IMeanEvaluable {

    protected static TravelMean instance;

    public int availableSeats;
    public static ArrayList<TravelMean> Means;

    public static TravelMean GetInstance() {
        throw new IllegalAccessError("Cannot call this method");
    }

}
