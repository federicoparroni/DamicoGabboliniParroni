package com.example.gabdampar.travlendar.Model;

import org.joda.time.LocalTime;

import java.util.ArrayList;

/**
 * Created by federico on 13/12/17.
 */

public class TimeWeatherList {

    private ArrayList<TimeWeather> weatherConditionList;

    public TimeWeatherList(ArrayList<TimeWeather> weatherConditionList) {
        this.weatherConditionList = weatherConditionList;
    }

    /**
     in case the actual time is referred to the same hour-field incapsulated in this TimeWeather returns the Weather incapsulated
     otherwise the nulln object is returned
     */
    public Weather getWeatherForTime(LocalTime actualTime){
        for(TimeWeather t:weatherConditionList) {
            if(t.time.getHourOfDay() == actualTime.getHourOfDay())
                return t.weather;
        }

        return null;
    }
}
