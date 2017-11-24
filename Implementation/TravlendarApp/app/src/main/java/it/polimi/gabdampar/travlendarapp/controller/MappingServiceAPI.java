package it.polimi.gabdampar.travlendarapp.controller;

import java.util.ArrayList;
import java.util.List;

import it.polimi.gabdampar.travlendarapp.model.Appointment;
import it.polimi.gabdampar.travlendarapp.model.Constraint.Constraint;
import it.polimi.gabdampar.travlendarapp.model.TravelOptionData;

/**
 * Created by gabbo on 12/11/2017.
 */

public class MappingServiceAPI implements IMappingServiceAPIWrapper {

    public List<TravelOptionData> getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start){
        return new ArrayList<TravelOptionData>();

        //to-do: call getTravelOptionData of all the hierarchy of travelmeans defined (which belong to the right region)and append every
        //returned value to the arraylist. this way all the possible paths to an appointment to another are considered, with
        //all travel services considered, but just in our region of interest
    }

}
