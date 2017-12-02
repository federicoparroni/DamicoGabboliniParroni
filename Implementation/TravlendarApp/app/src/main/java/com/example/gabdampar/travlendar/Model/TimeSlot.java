/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;


public class TimeSlot {

    public LocalTime startingTime;
    public LocalTime endingTime;

    public TimeSlot(LocalTime starting, LocalTime ending) {
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