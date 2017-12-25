package com.example.gabdampar.travlendar.Controller;

import android.content.Context;

import com.example.gabdampar.travlendar.Model.Appointment;

import java.util.ArrayList;

/**
 * Created by Edoardo D'Amico on 04/12/2017.
 */

public class Synchronizer {

    // singleton
    private static Synchronizer instance;
    public static Synchronizer GetInstance() {
        if(instance == null) {
            instance = new Synchronizer();
        }
        return instance;
    }



    public void Synchronize(Context context) {
        ArrayList<Appointment> list = AppointmentManager.GetInstance().loadAppointments(context);
        if(list!=null)
            AppointmentManager.GetInstance().apptList = AppointmentManager.GetInstance().loadAppointments(context);
    }




}