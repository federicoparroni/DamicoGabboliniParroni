package com.example.gabdampar.travlendar.Model;

/**
 * Created by gabdampar on 30/11/2017.
 */

import java.util.Date;


public class TimeSlot {

    public Date startingTime;
    public Date endingTime;

    public TimeSlot(Date starting, Date ending) {
        if(starting.before(ending)) {
            this.startingTime = starting;
            this.endingTime = ending;
        } else {
            this.startingTime = ending;
            this.endingTime = starting;
        }
    }

    public static boolean AreOverlapped(TimeSlot t1, TimeSlot t2) {
        return (t1.endingTime.after(t2.startingTime)) || (t2.endingTime.after(t1.startingTime));
    }

}
