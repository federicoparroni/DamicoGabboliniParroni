package com.example.gabdampar.travlendar.Model;

import org.joda.time.LocalTime;

/**
 * Created by gabbo on 02/12/2017.
 */

public class TimeWeather {
    public Weather weather;
    public LocalTime time;

    public TimeWeather(long l, Weather w){
        this.weather=w;
        this.time=new LocalTime(l*1000);
    }


}