package com.example.gabdampar.travlendar.Controller;


import com.example.gabdampar.travlendar.Model.Appointment;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    public ArrayList<Appointment> apptList = new ArrayList<>();

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

}