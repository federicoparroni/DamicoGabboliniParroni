package com.example.gabdampar.travlendar.Controller;

/**
 * Created by gabdampar on 30/11/2017.
 */


/*
// Provides methods to evaluate travelling costs from appt 1 to appt 2
*/
public interface IMeanEvaluable {

    float EstimateTime(Appointment from, Appointment to);

    float EstimateCost(Appointment from, Appointment to);

    float EstimateCarbon(Appointment from, Appointment to);

}
