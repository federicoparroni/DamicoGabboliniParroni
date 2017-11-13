package it.polimi.gabdampar.travlendarapp.controller.TravelMean;
import it.polimi.gabdampar.travlendarapp.model.*;
import it.polimi.gabdampar.travlendarapp.model.Constraint.*;

import java.util.List;

/**
 * Created by federico on 09/11/17.
 */

public class PublicTravelMean extends TravelMean {

    public TravelOptionData getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start){
        return null;
    }

    public void buyRideTicket(ScheduledAppointment sapp){

    }

    public int getRideTicketPrice(ScheduledAppointment sapp){
        return -1;
    }

}
