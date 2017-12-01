/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import org.joda.time.DateTime;


public class TimeSlot {

    public DateTime startingTime;
    public DateTime endingTime;

    public TimeSlot(DateTime starting, DateTime ending) {
        if(starting.isBefore(ending)) {
            this.startingTime = starting;
            this.endingTime = ending;
        } else {
            this.startingTime = ending;
            this.endingTime = starting;
        }
    }

    public static boolean AreOverlapped(TimeSlot t1, TimeSlot t2) {
        return (t1.endingTime.isAfter(t2.startingTime)) || (t2.endingTime.isAfter(t1.startingTime));
    }

}