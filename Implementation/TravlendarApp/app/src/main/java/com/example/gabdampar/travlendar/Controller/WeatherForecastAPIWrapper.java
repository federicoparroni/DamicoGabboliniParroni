package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.gabdampar.travlendar.Controller.JsonSerializer.LocalTimeSerializer;
import com.example.gabdampar.travlendar.Model.TimeWeather;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.Model.Weather;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.DataPoint;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gabbo on 02/12/2017.
 */

public class WeatherForecastAPIWrapper {

    private static WeatherForecastAPIWrapper instance = null;

    private static final String WEATHER_API_KEY = "1f7ef1984705d6eb16e76096d374e171";

    /** Weather API data are saved in a file and cached to reduce internet usage. This data refers to a specific date and place.
     * For future calls, we check if the cached data refers to the same date and nearby a certain location.
     * This is the maximum admissible distance from the cached location in order to reuse the cached data */
    private static final float CACHED_WEATHER_DATA_MAX_DISTANCE = 6000f;

    public static WeatherForecastAPIWrapper getInstance() {
        if (instance ==null)
            instance = new WeatherForecastAPIWrapper();
        return instance;
    }

    private WeatherForecastAPIWrapper() {
        ForecastApi.create(WEATHER_API_KEY);
    }

    /**
     requires: time to be formatted like this: YYYY-MM-DDTHH:MM:SS (e.g. 2013-05-06T12:00:00)
     returns: list of Weather for each hour from actual time till the next 48 hours
     */
    public void getWeather(final Context context, final WeatherForecastAPIWrapperCallBack listener, final LocalDate date, final LatLng coord) {
        final ArrayList<TimeWeather> result = new ArrayList<>();

        if(checkIfSavedInCache(context, date, coord)) {
            /** load cached json from file */
            listener.onWeatherResults(new TimeWeatherList( loadWeatherFromFile(context) ));
        } else {
            /** calls to weather API because no data found */
            double lat = coord.latitude;
            double lon = coord.longitude;
            Request request = new Request();
            request.setLat(String.valueOf(lat));
            request.setLng(String.valueOf(lon));
            request.setUnits(Request.Units.SI);
            request.setTime(date.toDateTimeAtStartOfDay().toString("YYYY-MM-dd'T'HH:mm:SS'Z'"));
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
                    for (DataPoint d : weatherResponse.getHourly().getData()) {
                        Weather r = null;
                        String s = d.getIcon();
                        if (d.getIcon().equals("clear-day"))
                            r = Weather.CLEAN;
                        if (d.getIcon().equals("clear-night"))
                            r = Weather.CLEAN;
                        if (d.getIcon().equals("rain"))
                            r = Weather.RAINY;
                        if (d.getIcon().equals("snow"))
                            r = Weather.SNOWY;
                        if (d.getIcon().equals("sleet"))
                            r = Weather.SNOWY;
                        if (d.getIcon().equals("wind"))
                            r = Weather.WINDY;
                        if (d.getIcon().equals("fog"))
                            r = Weather.FOGGY;
                        if (d.getIcon().equals("cloudy"))
                            r = Weather.CLOUDY;
                        if (d.getIcon().equals("partly-cloudy-day"))
                            r = Weather.CLOUDY;
                        if (d.getIcon().equals("partly-cloudy-night"))
                            r = Weather.CLOUDY;
                        result.add(new TimeWeather(d.getTime(), r));
                    }

                    // cache json weather
                    cacheWeather(context, result, date, coord);

                    listener.onWeatherResults(new TimeWeatherList(result));
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e("WeatherError", retrofitError.getMessage());
                }
            });

        }
    }

    public interface WeatherForecastAPIWrapperCallBack{
        void onWeatherResults(TimeWeatherList weatherConditionList);
    }


    /** WEATHER PERSISTENCE */

    /**
     * Check if weather API json have been already cached
     * @param context: used to retrieve user preferences and saved file
     * @param date: weather data for a date
     * @param coords: weather data for a location
     * @return true if available in cache, false otherwise
     */
    private boolean checkIfSavedInCache(Context context, LocalDate date, LatLng coords) {
        // check for weather data
        SharedPreferences settings = context.getSharedPreferences("ApiData", 0);
        String savedDateString = settings.getString("weatherAPICacheDate","");
        String savedCoordsString = settings.getString("weatherAPICacheCoords","");
        if(savedDateString.isEmpty() || savedCoordsString.isEmpty()) return false;
        else {
            LocalDate savedDate = LocalDate.parse(savedDateString);
            if(savedDate.getYear() != date.getYear() || savedDate.getDayOfYear() != date.getDayOfYear()) {
                /** calls to weather because data found are of a different day */
                return false;
            } else {
                /** check if saved coords are near the requested ones */
                String[] savedCoordsArray = savedCoordsString.split(",");
                if(savedCoordsArray.length == 2) {
                    LatLng savedCoords = new LatLng(Float.parseFloat(savedCoordsArray[0]), Float.parseFloat(savedCoordsArray[1]));
                    if(MapUtils.distance(coords, savedCoords) > CACHED_WEATHER_DATA_MAX_DISTANCE ) {
                        return false;
                    }
                } else {
                    throw new JsonParseException("Error in parsing json LatLng");
                }
            }
        }
        return true;
    }

    /**
     * Cache the provided weather API data into a file
     * @param context: used to retrieve user preferences and write the file
     * @param data: weather data to save
     * @param date: weather data for a date
     * @param coords: weather data for a location
     * @return
     */
    private boolean cacheWeather(Context context, ArrayList<TimeWeather> data, LocalDate date, LatLng coords) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer());
        final Gson gson = builder.create();

        try {
            /** save json into a file */
            String json = gson.toJson(data.toArray());

            FileOutputStream fos = context.openFileOutput("weather.json", Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();

        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            return false;
        }

        /** save details of the json in user preference */
        SharedPreferences settings = context.getSharedPreferences("ApiData", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("weatherAPICacheDate", date.toString());
        editor.putString("weatherAPICacheCoords", String.format(Locale.US,"%f,%f", coords.latitude, coords.longitude));
        editor.commit();

        return true;
    }

    /**
     * Get weather API data from file
     * @param context: used to access the file
     * @return weather API data
     */
    private ArrayList<TimeWeather> loadWeatherFromFile(Context context) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer());
        final Gson gson = builder.create();

        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(context.openFileInput("weather.json")));

            StringBuffer text = new StringBuffer();
            String line;
            while ((line = bReader.readLine()) != null) {
                text.append(line + "\n");
            }

            TimeWeather[] result = gson.fromJson(text.toString(), TimeWeather[].class);
            return new ArrayList<>(Arrays.asList(result));
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            return null;
        }
    }

}