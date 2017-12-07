package com.example.gabdampar.travlendar.Controller;

import android.util.Log;

import com.example.gabdampar.travlendar.Model.TimeWeather;
import com.example.gabdampar.travlendar.Model.Weather;
import com.google.maps.model.LatLng;
import com.here.android.mpa.common.GeoCoordinate;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.DataPoint;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gabbo on 02/12/2017.
 */

public class WeatherForecastAPIWrapper {
    private static WeatherForecastAPIWrapper ourInstance=null;
    private ArrayList<TimeWeather> l=new ArrayList<TimeWeather>();

    public static WeatherForecastAPIWrapper getInstance() {
        if (ourInstance==null)
            ourInstance = new WeatherForecastAPIWrapper();
        return ourInstance;
    }

    private WeatherForecastAPIWrapper() {
        ForecastApi.create("1f7ef1984705d6eb16e76096d374e171");
    }

    /**
     requires: time to be formatted like this: YYYY-MM-DDTHH:MM:SS (e.g. 2013-05-06T12:00:00)
     returns: list of Weather for each hour from actual time till the next 48 hours
     */
    public ArrayList<TimeWeather> getWeather(String time, LatLng coord){
        l.clear();

        double lat=coord.lat;
        double lon=coord.lng;
        Request request = new Request();
        request.setLat(String.valueOf(lat));
        request.setLng(String.valueOf(lon));
        request.setUnits(Request.Units.SI);
        request.setTime(time);
        request.setLanguage(Request.Language.ENGLISH);
        request.addExcludeBlock(Request.Block.ALERTS);
        request.addExcludeBlock(Request.Block.FLAGS);
        request.addExcludeBlock(Request.Block.DAILY);
        request.addExcludeBlock(Request.Block.CURRENTLY);
        request.addExcludeBlock(Request.Block.MINUTELY);

        RequestBuilder weather = new RequestBuilder();

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                for(DataPoint d : weatherResponse.getHourly().getData()) {
                    Weather r=null;
                    String s=d.getIcon();
                    if(d.getIcon().equals("clear-day"))
                        r = Weather.CLEAN;
                    if(d.getIcon().equals("clear-night"))
                        r = Weather.CLEAN;
                    if(d.getIcon().equals("rain"))
                        r = Weather.RAINY;
                    if(d.getIcon().equals("snow"))
                        r = Weather.SNOWY;
                    if(d.getIcon().equals("sleet"))
                        r = Weather.SNOWY;
                    if(d.getIcon().equals("wind"))
                        r = Weather.WINDY;
                    if(d.getIcon().equals("fog"))
                        r = Weather.FOGGY;
                    if(d.getIcon().equals("cloudy"))
                        r = Weather.CLOUDY;
                    if(d.getIcon().equals("partly-cloudy-day"))
                        r = Weather.CLOUDY;
                    if(d.getIcon().equals("partly-cloudy-night"))
                        r = Weather.CLOUDY;
                    l.add(new TimeWeather(d.getTime(), r));
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

        return l;
    }

}

