package com.example.gabdampar.travlendar.Model.travelMean;

import android.support.annotation.NonNull;

/**
 * Created by federico on 14/12/17.
 */

public class
TravelMeanCostCouple implements Comparable<TravelMeanCostCouple> {


    public TravelMean mean;
    public float cost;

    public TravelMeanCostCouple(TravelMean mean, float cost) {
        this.mean = mean;
        this.cost = cost;
    }

    @Override
    public int compareTo(@NonNull TravelMeanCostCouple travelMeanCostCouple) {
        return Float.compare(cost, travelMeanCostCouple.cost);
    }

    public TravelMean getTravelMean() {
        return mean;
    }

    public float getCost() {
        return cost;
    }

}