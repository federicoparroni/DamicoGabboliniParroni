package it.polimi.gabdampar.travlendarapp.controller.TravelMean;
import it.polimi.gabdampar.travlendarapp.model.*;
import it.polimi.gabdampar.travlendarapp.model.Constraint.*;

import java.util.List;

/**
 * Created by federico on 09/11/17.
 */

public class PrivateTravelMean extends TravelMean {
    public TravelOptionData getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start){
        return null;
    }

    public int getRidePrice(ScheduledAppointment sapp){
        return -1;
    }

}