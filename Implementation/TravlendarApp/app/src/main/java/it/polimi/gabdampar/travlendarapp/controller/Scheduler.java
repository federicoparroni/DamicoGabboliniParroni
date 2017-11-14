package it.polimi.gabdampar.travlendarapp.controller;

import android.location.Location;

import java.sql.Time;
import java.util.ArrayList;

import it.polimi.gabdampar.travlendarapp.model.Appointment;
import it.polimi.gabdampar.travlendarapp.model.Constraint.ConstraintOnSchedule;
import it.polimi.gabdampar.travlendarapp.model.OptimizingCriteria;
import it.polimi.gabdampar.travlendarapp.model.Schedule;
import it.polimi.gabdampar.travlendarapp.model.ScheduledAppointment;

/**
 * Created by Edoardo D'Amico on 13/11/2017.
 */

public class Scheduler {
    private Time wakeUpTime;
    private Location startingLocation;

    public Scheduler(Time wakeUpTime, Location startingLocation){
        this.wakeUpTime = wakeUpTime;
        this.startingLocation = startingLocation;
    }

    public Schedule ComputeSchedule(ArrayList<Appointment> appointmentsToSchedule,
                             ArrayList<ConstraintOnSchedule> constraintOnSchedule,
                             OptimizingCriteria optimizingCriteria){
        //sorting dell' Array list di appointments secondo l'ora di inizio.
            //CODICE
        //gli appointment che non hanno ora di inizio saranno inseriti all'inizio della lista
        //creare ArrayList<Appointment> considerando tutte le possibili combinazioni di posizioni degli appointment senza ora di inizio
            /*
            while(appointmentToSchedule.GetFirst().GetStartingTime() == 0)

             */
            return null;
    }
}
