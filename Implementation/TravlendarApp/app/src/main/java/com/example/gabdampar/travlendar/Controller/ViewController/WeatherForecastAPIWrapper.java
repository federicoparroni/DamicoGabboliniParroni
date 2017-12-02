package com.example.gabdampar.travlendar.Controller.ViewController;

import android.util.Log;

import com.example.gabdampar.travlendar.Model.TimeWeather;
import com.example.gabdampar.travlendar.Model.Weather;
import com.here.android.mpa.common.GeoCoordinate;
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

class WeatherForecastAPIWrapper {
    private static WeatherForecastAPIWrapper ourInstance=null;
    private ArrayList<TimeWeather> l=null;

    static WeatherForecastAPIWrapper getInstance() {
        if (ourInstance==null)
            ourInstance = new WeatherForecastAPIWrapper();
        return ourInstance;
    }

    private WeatherForecastAPIWrapper() {
    }

    /**
     requires: time to be formatted like this: YYYY-MM-DDTHH:MM:SS (e.g. 2013-05-06T12:00:00)
     returns: list of Weather for each hour from actual time till the next 48 hours
     */
    public ArrayList<TimeWeather> getWeather(String time, GeoCoordinate coord){
        l.clear();

        double lat=coord.getLatitude();
        double lon=coord.getLongitude();
        RequestBuilder weather = new RequestBuilder();
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

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                for(DataPoint d : weatherResponse.getDaily().getData()) {
                    Weather r;
                    switch (weatherResponse.getCurrently().getIcon()) {
                        case "clear-day":
                            r = Weather.CLEAN;
                        case "clear-night":
                            r = Weather.CLEAN;
                        case "rain":
                            r = Weather.RAINY;
                        case "snow":
                            r = Weather.SNOWY;
                        case "sleet":
                            r = Weather.SNOWY;
                        case "wind":
                            r = Weather.WINDY;
                        case "fog":
                            r = Weather.FOGGY;
                        case "cloudy":
                            r = Weather.CLOUDY;
                        case "partly-cloudy-day":
                            r = Weather.CLOUDY;
                        case "partly-cloudy-night":
                            r = Weather.CLOUDY;
                        default:
                            r = null;
                    }
                    l.add(new TimeWeather(d.getTime(), r));
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("Error", "Error while calling: " + retrofitError.getUrl());
                Log.d("Error", retrofitError.toString());
            }
        });

        return l;
    }

}

