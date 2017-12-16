package com.example.gabdampar.travlendar.Model.travelMean;

import com.example.gabdampar.travlendar.Model.Weather;

import java.util.Comparator;

/**
 * Created by federico on 14/12/17.
 */

public class TravelMeanCostCouple implements Comparator {


    private TravelMeanEnum mean;
    private float cost;

    public TravelMeanCostCouple(TravelMeanEnum mean, float cost) {
        this.mean = mean;
        this.cost = cost;
    }


    @Override
    public int compare(Object o, Object t1) {
        TravelMeanCostCouple o1 = (TravelMeanCostCouple) o;
        TravelMeanCostCouple o2 = (TravelMeanCostCouple) t1;
        return Float.compare(o1.cost, o2.cost);
    }


}