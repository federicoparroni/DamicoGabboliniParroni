package it.polimi.gabdampar.travlendarapp.controller.TravelMean;

import java.util.List;

import it.polimi.gabdampar.travlendarapp.model.Appointment;
import it.polimi.gabdampar.travlendarapp.model.Area;
import it.polimi.gabdampar.travlendarapp.model.Constraint.Constraint;
import it.polimi.gabdampar.travlendarapp.model.ScheduledAppointment;
import it.polimi.gabdampar.travlendarapp.model.TravelOptionData;

/**
 * Created by gabbo on 12/11/2017.
 */

class MilanPublicTransportation extends PublicTravelMean {
    private static final MilanPublicTransportation ourInstance = new MilanPublicTransportation();

    static MilanPublicTransportation getInstance() {
        return ourInstance;
    }

    private MilanPublicTransportation() {
        area= Area.MILAN;
    }

    @Override
    public TravelOptionData getTravelOptionData(List<Constraint> constr, Appointment end, Appointment start) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public void buyRideTicket(ScheduledAppointment sapp) throws UnsupportedOperationException {
        //to-do
    }

    @Override
    public void getRideTicketPrice(ScheduledAppointment sapp) throws UnsupportedOperationException {

    }
}
