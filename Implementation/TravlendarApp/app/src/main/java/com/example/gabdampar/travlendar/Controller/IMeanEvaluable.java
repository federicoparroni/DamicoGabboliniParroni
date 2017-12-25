
package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.google.android.gms.maps.model.LatLng;

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

    float EstimateTime(TemporaryAppointment from, TemporaryAppointment to, float distance);
    float EstimateTime(LatLng from, LatLng to);

    float EstimateCost(TemporaryAppointment from, TemporaryAppointment to, float distance);
    float EstimateCost(LatLng from, LatLng to);

    float EstimateCarbon(TemporaryAppointment from, TemporaryAppointment to, float distance);
    float EstimateCarbon(LatLng from, LatLng to);

}