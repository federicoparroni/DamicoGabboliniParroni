package com.example.gabdampar.travlendar.Model;

import org.joda.time.LocalTime;

/**
 * Created by gabbo on 02/12/2017.
 */

public class TimeWeather {
    Weather w;
    LocalTime l;

    public TimeWeather(long l, Weather w){
        this.w=w;
        this.l=new LocalTime(l);
    }

    /**
     in case the actual time is referred to the same hour-field incapsulated in this TimeWeather returns the Weather incapsulated
     otherwise the nulln object is returned
     */
    public Weather isActualTimeReferringToThisTimeWeather(LocalTime actualTime){
        Weather r=null;
        if(actualTime.getHourOfDay()==l.getHourOfDay())
            r=w;
        return r;
    }
}