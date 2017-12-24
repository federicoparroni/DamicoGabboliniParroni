package com.example.gabdampar.travlendar.Controller;

import android.content.Context;

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

        AppointmentManager.GetInstance().apptList = AppointmentManager.GetInstance().loadAppointments(context);

    }




}