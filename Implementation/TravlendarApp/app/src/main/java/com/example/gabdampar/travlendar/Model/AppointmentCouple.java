package com.example.gabdampar.travlendar.Model;

/**
 * Created by federico on 14/12/17.
 */

public class AppointmentCouple {

    private Appointment a1;
    private Appointment a2;

    public AppointmentCouple(Appointment a1, Appointment a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    public boolean equals(Object obj) {
        //if(obj instanceof AppointmentCouple){
            AppointmentCouple a = (AppointmentCouple) obj;
            return (this.a1.equals(a.a1) && this.a2.equals(a.a2));
        /*}else{
            return false;
        }*/
    }

    @Override
    public int hashCode() {
        return a1.hashCode()+a2.hashCode();
    }
}
