package it.polimi.gabdampar.travlendarapp.controller;

import java.util.List;

import it.polimi.gabdampar.travlendarapp.model.Appointment;
import it.polimi.gabdampar.travlendarapp.model.Constraint.Constraint;
import it.polimi.gabdampar.travlendarapp.model.TravelOptionData;

/**
 * Created by gabbo on 12/11/2017.
 */

public interface IMappingServiceAPI {

    /*
    given a set of constraints (that can be just on appointment, so can be specified wheter a travelmean is deactivated or not in a timeslot
    or on a schedule, so can be specified wheter a travelMean is deactived in a time slot, under certain weather condition and the maximun amount
    of distance that can be travelled with a travel mean) then it will return a list of possibile travelOptionData that fit with the constraints
    imposed.
     */
    public List<TravelOptionData> getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start);

}
