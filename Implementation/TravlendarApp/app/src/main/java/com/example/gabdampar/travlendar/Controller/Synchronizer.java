package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;

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

    public void Synchronize(){
        AppointmentManager.GetInstance().AddAppointment(new Appointment("A", new LocalDate(2017,11,18),
                new LocalTime(11,30),20*60, null));
        AppointmentManager.GetInstance().AddAppointment(new Appointment("B", new LocalDate(2017,11,18),
                new LocalTime(15,0),15*60, null));
        AppointmentManager.GetInstance().AddAppointment(new Appointment("C", new LocalDate(2017,11,18),
                new LocalTime(16,0),10*60, null));
        AppointmentManager.GetInstance().AddAppointment(new Appointment("D", new LocalDate(2017,11,18),
                new TimeSlot(new LocalTime(13,30),new LocalTime(15,40)),
                5*60, null));
        AppointmentManager.GetInstance().AddAppointment(new Appointment("E", new LocalDate(2017,11,18),
                new TimeSlot(new LocalTime(10,0),new LocalTime(15,10)),
                10*60, null));
        AppointmentManager.GetInstance().AddAppointment(new Appointment("F", new LocalDate(2017,11,18),
                new  TimeSlot(new LocalTime(14,30),new LocalTime(16,0)),
                25*60, null));
    }

}
