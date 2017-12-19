package com.example.gabdampar.travlendar.Model;

import android.content.Context;

import org.joda.time.LocalTime;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by federico on 13/12/17.
 */

public class TimeWeatherList implements Serializable {

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


    /**
     * Serialization
     */
    // Constant with a file name
    //public static String fileName = "createResumeForm.ser";

    // Serializes an object and saves it to a file
    public void saveToFile(Context context, String fileName) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(this);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Creates an object by reading it from a file
    public static TimeWeatherList readFromFile(Context context, String fileName) {
        TimeWeatherList result = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            result = (TimeWeatherList) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


}