/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model.travelMean;

import com.example.gabdampar.travlendar.Controller.IMeanEvaluable;
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

    public TravelMean getTravelMean(TravelMeanEnum meanType) {
        return null;
    }

}