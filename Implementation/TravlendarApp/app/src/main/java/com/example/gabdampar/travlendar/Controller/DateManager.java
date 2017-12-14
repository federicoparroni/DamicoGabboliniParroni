/**
 * Created by gabdampar on 02/12/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import org.joda.time.LocalTime;

public class DateManager {

    public static LocalTime MaxTime(LocalTime t1, LocalTime t2) {
        return t1.isAfter(t2) ? t1 : t2;
    }

    public static LocalTime MinTime(LocalTime t1, LocalTime t2) {
        return t1.isBefore(t2) ? t1 : t2;
    }

}