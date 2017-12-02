package com.example.gabdampar.travlendar.Controller;


/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    // singleton
    private static AppointmentManager instance;
    public static AppointmentManager GetInstance() {
        if(instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }
}
