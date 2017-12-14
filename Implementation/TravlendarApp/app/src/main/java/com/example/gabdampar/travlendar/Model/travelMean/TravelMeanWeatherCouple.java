package com.example.gabdampar.travlendar.Model.travelMean;

import com.example.gabdampar.travlendar.Model.Weather;

/**
 * Created by federico on 14/12/17.
 */


public class TravelMeanWeatherCouple {

    private TravelMeanEnum mean;
    private Weather weather;

    public TravelMeanWeatherCouple(TravelMeanEnum mean, Weather weather) {
        this.mean = mean;
        this.weather = weather;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != TravelMeanWeatherCouple.class) return  false;
        else {
            TravelMeanWeatherCouple obj2 = (TravelMeanWeatherCouple) obj;
            return this.mean == obj2.mean && this.weather == obj2.weather;
        }
    }
}