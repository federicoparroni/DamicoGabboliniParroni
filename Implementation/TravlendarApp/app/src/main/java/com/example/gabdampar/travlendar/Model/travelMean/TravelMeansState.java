package com.example.gabdampar.travlendar.Model.travelMean;

import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.Weather;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by federico on 14/12/17.
 */

public class TravelMeansState {

    public TravelMeanEnum currentMean;      /** assign an order in choosing means (e.g.: car cannot be used if not picked from the first appt */
    public HashMap<TravelMeanWeatherCouple, Float> meansState = new HashMap();

    public TravelMeansState(ArrayList<ConstraintOnSchedule> constraints) {
        for (ConstraintOnSchedule c: constraints) {
            for (Weather w : c.weather) {
                addState(c.mean, w, c.maxDistance);
            }
        }
    }

    public float getDistance(TravelMeanEnum tm, Weather weather) {
        return meansState.get(new TravelMeanWeatherCouple(tm, weather));
    }

    public void addState(TravelMeanEnum tm, Weather weather, float distance) {
        meansState.put(new TravelMeanWeatherCouple(tm, weather), distance);
    }

    public float subtractDistance(TravelMeanEnum tm, Weather weather, float distance) {
        TravelMeanWeatherCouple key = new TravelMeanWeatherCouple(tm ,weather);

        if(meansState.containsKey(key)) {
            float currentDistance = getDistance(tm, weather);
            meansState.remove(new TravelMeanWeatherCouple(tm, weather));
            addState(tm, weather, currentDistance - distance);

            return currentDistance - distance;
        } else {
            return Float.MAX_VALUE;
        }

    }

}