/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.DateManager;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.Model.TravelOptionData;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Bike;
import com.example.gabdampar.travlendar.Model.travelMean.publicMeans.Bus;
import com.example.gabdampar.travlendar.Model.travelMean.privateMeans.Car;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.google.android.gms.maps.model.LatLng;
import com.here.android.mpa.common.GeoCoordinate;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class Scheduler{

    private LocalTime wakeupTime;
    private LatLng startingLocation;
    private ArrayList<Appointment> appts;
    private ArrayList<ConstraintOnSchedule> constraints;
    private OptCriteria criteria;

    byte[][] pred;
    double[][] dist;
    private ArrayList<Schedule> schedules = new ArrayList<Schedule>();

    ArrayList<Schedule> possibleSchedules = new ArrayList<Schedule>();

    public Scheduler(LocalTime wakeupTime, LatLng location, ArrayList<Appointment> appts, ArrayList<ConstraintOnSchedule> constraints, OptCriteria c) {
        this.wakeupTime = wakeupTime;
        this.startingLocation = location;
        this.appts = appts;
        this.constraints = constraints;
        this.criteria = c;
    }


    public Schedule ComputeSchedule() {
        if(this.appts.size() > 0) {

            // 1-2
            CalculatePredecessorsAndDistanceMatrix(appts);
		/* DEBUG */
            StampaArray(pred);

            // 3
            CalculateCombinations(appts, pred.clone(), 0, 1);

            // 4
            // OrderSchedules();

            // 5
            return schedules.get(0);
        } else {
            throw new IllegalArgumentException("Empty appointment list");
        }
    }

    void CalculatePredecessorsAndDistanceMatrix(ArrayList<Appointment> apps) {
        pred = new byte[apps.size()][apps.size()];
        dist = new double[apps.size()][apps.size()];

        for(int i=0; i < apps.size()-1; i++) {
            for(int j=i+1; j < apps.size(); j++) {
                Appointment a1 = apps.get(i);
                Appointment a2 = apps.get(j);

                if(a1.isDeterministic() && a2.isDeterministic()) {
                    if(a1.startingTime.isBefore(a2.startingTime)) pred[i][j] = 1;
                    else pred[i][j] = 0;
                } else
                if(a1.isDeterministic() && !a2.isDeterministic()) {
                    if(a1.startingTime.isBefore(a2.timeSlot.startingTime)) pred[i][j] = 1;
                    else if(a1.endingTime().isAfter(a2.timeSlot.endingTime)) pred[i][j] = 0;
                    else pred[i][j] = 2;
                } else
                if(!a1.isDeterministic() && a2.isDeterministic()) {
                    if(a1.timeSlot.endingTime.isAfter(a2.endingTime())) pred[i][j] = 1;
                    else if(a1.timeSlot.startingTime.isAfter(a2.startingTime)) pred[i][j] = 0;
                    else pred[i][j] = 2;
                } else
                if(!a1.isDeterministic() && !a2.isDeterministic()) {
                    if(a1.timeSlot.endingTime.isAfter(a2.timeSlot.startingTime)) pred[i][j] = 1;
                    else if(a1.timeSlot.startingTime.isAfter(a2.timeSlot.endingTime)) pred[i][j] = 0;
                    else pred[i][j] = 2;
                }

                // distance
                //dist[i][j] = a1.coords.distanceTo(a2.coords);

            }
        }
    }



    void CalculateCombinations(ArrayList<Appointment> appts, byte[][] pr, int curri, int currj) {

        for(int i=curri; i < appts.size()-1; i++) {
            for(int j=currj-1; j < appts.size(); j++) {
                if(pr[i][j] == 2) {

                    byte[][] pred0 = CloneMatrix(pr);
                    pred0[i][j] = 0;
                    CalculateCombinations(appts, pred0, i, j);

                    byte[][] pred1 = CloneMatrix(pr);
                    pred1[i][j] = 1;
                    CalculateCombinations(appts, pred1, i, j);

                    return;
                }
            }
        }

        // all deterministic
        ArrayList<Appointment> arrangement = ConvertPredMatrixToList(appts, pr);
        if(arrangement.size() == pred.length) {
            // schedule appointments in the arrangement
            Schedule s = GetSchedule(arrangement);
            if(s != null) {
                // DEBUG
                s.PrintToConsole();
                schedules.add(s);

            } else {

                for(Appointment appt : arrangement) {
                    System.out.print(appt.toString() + " ");
                }
                System.out.println("- NON FATTIBILE");
            }
        }
    }


    /*
     * 	Elaborate predecessor matrix and return the appointment ordered by precedence
     */
    ArrayList<Appointment> ConvertPredMatrixToList(ArrayList<Appointment> appts, byte[][] pred) {
        ArrayList<Appointment> res = new ArrayList<Appointment>();

        for(int i = pred.length-1; i >= 0; i--) {
            // check/find the i-th appointment
            for(int k=0; k < pred.length; k++) {
                if( SumRow(pred,k) + SumInvertedCol(pred,k) == i ) {
                    res.add(appts.get(k));
                    break;
                }
            }
        }

        return res;
    }

    /*
     * 	Assign starting times to ordered appointments and check the schedule feasibility
     */
    Schedule GetSchedule(ArrayList<Appointment> arrangement) {
        ArrayList<ScheduledAppointment> scheduledAppts = new ArrayList<ScheduledAppointment>();

        // create wakeup dummy appointment
        Appointment wakeUpAppt = new Appointment("WakeUp", appts.get(0).date, wakeupTime, 0, startingLocation);
        scheduledAppts.add(new ScheduledAppointment(wakeUpAppt, wakeupTime, wakeupTime, null));

        Appointment firstAppt = arrangement.get(0);
        if(firstAppt.isDeterministic()) {
            TravelMean mean = GetBestTravelMean(wakeUpAppt, firstAppt);
            int travelTime1 = (int)mean.EstimateTime(wakeUpAppt, firstAppt);
            LocalTime startingTime1 = firstAppt.startingTime.minusSeconds(travelTime1);
            scheduledAppts.add(new ScheduledAppointment(firstAppt, startingTime1, firstAppt.startingTime, mean));

            // check if feasible
            if(startingTime1.isBefore(wakeupTime)) return null;

        } else {		// first appt is not deterministic
            TravelMean mean = GetBestTravelMean(wakeUpAppt, firstAppt);
            int travelTime1 = (int)mean.EstimateTime(wakeUpAppt, firstAppt);

            if(arrangement.size() == 1) {
                LocalTime ETA1 = firstAppt.timeSlot.endingTime.minusSeconds(firstAppt.duration);
                LocalTime startingTime1 = ETA1.minusSeconds(travelTime1);
                scheduledAppts.add(new ScheduledAppointment(firstAppt, startingTime1, ETA1, mean));

                //check if feasible
                if(startingTime1.isBefore(wakeupTime)) return null;
            } else {
                Appointment secondAppt = arrangement.get(1);
                int travelTime2 = (int)mean.EstimateTime(firstAppt, secondAppt);

                LocalTime maxEndingTime1 = secondAppt.isDeterministic() ? secondAppt.startingTime.minusSeconds(travelTime2) :
                        secondAppt.timeSlot.startingTime.minusSeconds(travelTime2);
                LocalTime endingTime1 = DateManager.MinTime(firstAppt.timeSlot.endingTime, maxEndingTime1);
                LocalTime ETA1 = endingTime1.minusSeconds(firstAppt.duration);
                LocalTime startingTime1 = ETA1.minusSeconds(travelTime1);
                ScheduledAppointment firstSchedAppt = new ScheduledAppointment(firstAppt, startingTime1, ETA1, mean);
                scheduledAppts.add(firstSchedAppt);
                // check if feasible
                if(startingTime1.isBefore(wakeupTime) || startingTime1.isBefore(firstAppt.timeSlot.startingTime)) return null;
            }

        }


        // only appt2 must be scheduled for each cycle
        for(int i=1; i < arrangement.size(); i++) {
            ScheduledAppointment appt1 = scheduledAppts.get(i);
            Appointment appt2 = arrangement.get(i);

            TravelMean mean = GetBestTravelMean(appt1.originalAppointment, appt2);
            int travelTime = (int)mean.EstimateTime(appt1.originalAppointment, appt2);

            if(appt2.isDeterministic()) {
                // both deterministic, only creates scheduledAppointment with recalculated startingTime and ETA
                LocalTime fixedStartingTime = appt2.startingTime.minusSeconds(travelTime);
                scheduledAppts.add(new ScheduledAppointment(appt2, fixedStartingTime, appt2.startingTime, mean));

                // check if overlapping
                if(appt1.endingTime().isAfter(fixedStartingTime)) return null;

            } else {
                // 2nd non deterministic, assing startingTime at max possible
                LocalTime fixedStartingTime = DateManager.MaxTime(appt1.endingTime(),
                        appt2.timeSlot.startingTime.minusSeconds(travelTime));
                LocalTime ETA2 = fixedStartingTime.plusSeconds(travelTime);

                scheduledAppts.add(new ScheduledAppointment(appt2, fixedStartingTime, ETA2, mean));

                if(appt2.timeSlot.endingTime.isBefore(ETA2.plusSeconds(appt2.duration))) return null;
            }

        }

        return new Schedule(scheduledAppts);
    }

    TravelMean GetBestTravelMean(Appointment a1, Appointment a2) {
        switch (this.criteria) {
            case OPTIMIZE_TIME:
                return Car.GetInstance();
            case OPTIMIZE_CARBON:
                return Bike.GetInstance();
            case OPTIMIZE_COST:
                return Bus.GetInstance();
            default:
                return null;

        }
    }



    //
    // +++++ AUXILIARY FUNCTIONS +++++
    //
    //

    int SumRow(byte[][] matrix, int riga) {
        int sum = 0;
        for(int i=riga+1; i < matrix.length; i++) {
            sum += matrix[riga][i];
        }
        return sum;
    }
    int SumInvertedCol(byte[][] matrix, int col) {
        int sum = 0;
        for(int i=0; i < col; i++) {
            sum += matrix[i][col];
        }
        return col - sum;
    }

    /*
     * Create a clone of a matrix
     */
    byte[][] CloneMatrix(byte[][] orig) {
        byte[][] clone = new byte[orig.length][orig.length];

        for(int i=0; i < orig.length; i++) {
            for(int j=0; j < orig.length; j++) {
                clone[i][j] = orig[i][j];
            }
        }
        return clone;
    }

    void StampaArray(byte[][] m) {
        for(int i=0; i < m.length; i++) {
            for(int j=0; j < m.length; j++) {
                System.out.print(String.format("%d ", m[i][j]));
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("---------------------------------");
        System.out.println("");
    }

}