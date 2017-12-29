package com.example.gabdampar.travlendar.Model.travelMean;

import android.support.annotation.NonNull;

import java.util.ArrayList;

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

    public float getCost() {
        return cost;
    }

    public float getTime() {
        return time;
    }

    public TravelMean getMean(){
        return mean;
    }

    public static void CleanUncovenientMeans(ArrayList<TravelMeanCostTimeInfo> meansQueue) {
        for (int i = 1; i < meansQueue.size(); i++) { //aggiunto quel meno uno sul for
            TravelMeanCostTimeInfo mc2 = meansQueue.get(i);
            TravelMeanCostTimeInfo mc1 = meansQueue.get(i - 1);
            float delta;
            if((mc2.getCost() - mc1.getCost()) > 0){
                delta = (mc1.getTime() - mc2.getTime()) / (mc2.getCost() - mc1.getCost());
            }else{
                delta = mc1.getTime() - mc2.getTime();
            }

            if (delta <= 0)
                meansQueue.remove(i);
            else
                mc2.relativeCost = delta;
        }
    }

}