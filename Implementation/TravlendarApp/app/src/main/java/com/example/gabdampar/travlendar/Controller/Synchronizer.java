package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

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

    // TODO: (DUMMY METHOD) insert coord

    public void Synchronize() {
        /**
         * dummy appointments creational part
         */
        if(AppointmentManager.GetInstance().apptList.size() == 0) {
            AppointmentManager.GetInstance().apptList.add(new Appointment("A", new LocalDate(2017, 11, 18),
                    new LocalTime(11, 30), 20 * 60, new LatLng(45.4372464, 9.165939)));
            AppointmentManager.GetInstance().apptList.add(new Appointment("B", new LocalDate(2017, 11, 18),
                    new LocalTime(15, 0), 15 * 60, new LatLng(45.4781108, 9.2250824)));
            AppointmentManager.GetInstance().apptList.add(new Appointment("C", new LocalDate(2017, 11, 18),
                    new LocalTime(16, 0), 10 * 60, new LatLng(45.4641013, 9.1897325)));
            AppointmentManager.GetInstance().apptList.add(new Appointment("Aaaaaaaaaa", new LocalDate(2017, 11, 18),
                    new TimeSlot(new LocalTime(13, 30), new LocalTime(23, 40)),
                    5 * 60, new LatLng(45.4955892, 9.1919801)));
            AppointmentManager.GetInstance().apptList.add(new Appointment("E", new LocalDate(2017,11,18),
                new TimeSlot(new LocalTime(10,0),new LocalTime(15,10)),
                10*60, new LatLng(45.4704799,9.1771438)));
            AppointmentManager.GetInstance().apptList.add(new Appointment("F", new LocalDate(2017,11,18),
                new TimeSlot(new LocalTime(14,30),new LocalTime(17,0)),
                25*60, new LatLng(45.5061279,9.1500127)));
        }
    }

}