package com.example.gabdampar.travlendar.Model.travelMean;

import android.support.annotation.NonNull;

/**
 * Created by federico on 14/12/17.
 */

public class TravelMeanCostTimeInfo implements Comparable<TravelMeanCostTimeInfo> {


    private TravelMean mean;
    private float cost;

    private float time;
    public float relativeCost;

    public TravelMeanCostTimeInfo(TravelMean mean, float cost, float time) {
        this.mean = mean;
        this.cost = cost;
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull TravelMeanCostTimeInfo travelMeanCostTimeInfo) {
        return Float.compare(cost, travelMeanCostTimeInfo.cost);
    }

    public TravelMean getTravelMean() {
        return mean;
    }

    public float getCost() {
        return cost;
    }

    public float geTime() {
        return time;
    }

    public TravelMean getMean(){
        return mean;
    }

}