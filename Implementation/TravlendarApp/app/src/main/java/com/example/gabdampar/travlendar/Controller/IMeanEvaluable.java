
package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;

import org.joda.time.Duration;

/**
 * Created by gabdampar on 30/11/2017.
 */
/*
Provides methods to evaluate travelling costs from appt 1 to appt 2
*/

/**
 * TODO: move EstimateCost to the mapping travel mean api wrapper class
 */
public interface IMeanEvaluable {

    float EstimateTime(Appointment from, Appointment to, float distance);

    float EstimateCost(Appointment from, Appointment to, float distance);

    float EstimateCarbon(Appointment from, Appointment to, float distance);

}