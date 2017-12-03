package com.example.gabdampar.travlendar.Controller;


import com.example.gabdampar.travlendar.Model.Appointment;

import java.util.ArrayList;

/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    private static ArrayList<Appointment> apptList = new ArrayList<>();

    // singleton
    private static AppointmentManager instance;
    public static AppointmentManager GetInstance() {
        if(instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public static void AddAppointment(Appointment a) {
        apptList.add(a);
    }

    public static ArrayList<Appointment> GetAppointmentList() {
        return apptList;
    }

}
