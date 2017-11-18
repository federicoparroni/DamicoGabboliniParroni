package it.polimi.gabdampar.travlendarapp.model;

import android.location.Location;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList; // https://developer.android.com/reference/java/util/ArrayList.html

/**
 * Created by federico on 09/11/17.
 */

public class Schedule {

    private Date date;
    private Location startingPosition;
    private int startingNumberOfPeople;
    private Time wakeUpTime;
    private ArrayList scheduledAppointmentList;

    // costruttore--------------------

    public Schedule(Date date, Location startingPosition, int startingNumberOfPeople,
                    Time wakeUpTime, ArrayList scheduledAppointmentList){
        this.date = date;
        this.startingPosition = startingPosition;
        this.startingNumberOfPeople = startingNumberOfPeople;
        this.wakeUpTime = wakeUpTime;
        this.scheduledAppointmentList = scheduledAppointmentList;
    }
}
