package com.example.gabdampar.travlendar.Controller;


import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    public ArrayList<Appointment> apptList = new ArrayList<>();
    int sync=0;

    // singleton
    private static AppointmentManager instance;
    public static AppointmentManager GetInstance() {
        if(instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public ArrayList<Appointment> getAppointmentList(){
        return apptList;
    }

    public Appointment GetAppointment(int position){
        return apptList.get(position);
    }

    public ArrayList<Appointment> GetAppointmentsByDate(LocalDate date) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a: apptList) {
            if( a.getDate().equals(date) ) result.add(a);
        }
        return result;
    }

    public void setAllStopsCloseToAppointment(final Appointment app){
        NetworkManager.transitStopsResponseReturned = false;
        sync=0;
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null)
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, latLng);
                if(sync==4)
                    NetworkManager.transitStopsResponseReturned = true;
            }
        }, TravelMeanEnum.TRAM, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null)
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, latLng);
                if(sync==4)
                    NetworkManager.transitStopsResponseReturned = true;
            }
        }, TravelMeanEnum.BUS, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null)
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, latLng);
                if(sync==4)
                    NetworkManager.transitStopsResponseReturned = true;
            }
        }, TravelMeanEnum.METRO, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null)
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, latLng);
                if(sync==4)
                    NetworkManager.transitStopsResponseReturned = true;
            }
        }, TravelMeanEnum.TRAIN, app.coords, 2000);
    }

}