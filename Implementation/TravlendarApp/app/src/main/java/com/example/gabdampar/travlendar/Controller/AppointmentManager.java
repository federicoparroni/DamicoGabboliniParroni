package com.example.gabdampar.travlendar.Controller;


import com.example.gabdampar.travlendar.Model.Appointment;

import java.util.ArrayList;

/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    private ArrayList<Appointment> apptList = new ArrayList<>();

    // singleton
    private static AppointmentManager instance;
    public static AppointmentManager GetInstance() {
        if(instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public void AddAppointment(Appointment a) {
        apptList.add(a);
    }

    public void RemoveFromList(int position){
        apptList.remove(position);
    }

    public ArrayList<Appointment> GetAppointmentList() {
        return apptList;
    }


}
