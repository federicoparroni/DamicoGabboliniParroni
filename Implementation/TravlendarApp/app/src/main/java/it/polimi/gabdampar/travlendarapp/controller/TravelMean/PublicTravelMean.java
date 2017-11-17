package it.polimi.gabdampar.travlendarapp.controller.TravelMean;
import it.polimi.gabdampar.travlendarapp.model.*;
import it.polimi.gabdampar.travlendarapp.model.Constraint.*;

import java.util.List;

/**
 * Created by federico on 09/11/17.
 */

public abstract class PublicTravelMean extends TravelMean {

    public abstract TravelOptionData getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start) throws UnsupportedOperationException;

    public abstract void buyRideTicket(ScheduledAppointment sapp) throws UnsupportedOperationException;

    public abstract void getRideTicketPrice(ScheduledAppointment sapp) throws UnsupportedOperationException;

}
