package it.polimi.gabdampar.travlendarapp.controller.TransportationServices;

import java.util.List;

import it.polimi.gabdampar.travlendarapp.model.Appointment;
import it.polimi.gabdampar.travlendarapp.model.Constraint.Constraint;
import it.polimi.gabdampar.travlendarapp.model.ScheduledAppointment;
import it.polimi.gabdampar.travlendarapp.model.TravelOptionData;
import it.polimi.gabdampar.travlendarapp.model.Area;

/**
 * Created by gabbo on 19/11/2017.
 */

interface ITransportationService {

    public Area getArea();

    public TravelOptionData getTravelOptionData(List<Constraint> constr, Appointment source, Appointment end);

    public void buyRideTicket(ScheduledAppointment appointmentSource, ScheduledAppointment appointmenDest);

    public float getRideTicketPrice(ScheduledAppointment appointmentSource, ScheduledAppointment appointmenDest);

}
