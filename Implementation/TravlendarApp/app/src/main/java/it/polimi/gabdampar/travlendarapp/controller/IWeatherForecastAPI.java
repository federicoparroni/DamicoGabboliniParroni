package it.polimi.gabdampar.travlendarapp.controller;

import java.util.Date;

import it.polimi.gabdampar.travlendarapp.model.Weather;

/**
 * Created by gabbo on 12/11/2017.
 */

public interface IWeatherForecastAPI {

    /**
     given a date, it will return an item of the enumeration Weather which better represents how will the wheater like at that time
     */
    public Weather getWeatherForecast(Date d);

}
