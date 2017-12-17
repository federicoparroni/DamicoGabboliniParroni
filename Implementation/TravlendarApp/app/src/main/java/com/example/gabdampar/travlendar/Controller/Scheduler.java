/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.AppointmentCouple;
import com.example.gabdampar.travlendar.Model.ConstraintOnAppointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.TimeWeather;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.Model.Weather;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanCostCouple;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanWeatherCouple;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeansState;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.example.gabdampar.travlendar.Model.travelMean.TravelMean.getTravelMean;

public class Scheduler implements WeatherForecastAPIWrapper.WeatherForecastAPIWrapperCallBack {

    public LocalTime wakeupTime;
    public LatLng startingLocation;
    public ArrayList<Appointment> appts = new ArrayList<>();
    public ArrayList<ConstraintOnSchedule> constraints = new ArrayList<>();
    public OptCriteria criteria;

    private TimeWeatherList weatherConditions;      // set by weather API callback

    private byte[][] pred;
    private HashMap<AppointmentCouple, Float> distances = new HashMap<>();

    /** contains the schedules computed until now */
    private ArrayList<Schedule> schedules = new ArrayList<>();

    //ArrayList<Schedule> possibleSchedules = new ArrayList<Schedule>();

    public Scheduler() {}

    public Scheduler(LocalTime wakeupTime, LatLng location, ArrayList<Appointment> appts, ArrayList<ConstraintOnSchedule> constraints, OptCriteria c) {
        this.wakeupTime = wakeupTime;
        this.startingLocation = location;
        this.appts = appts;
        this.constraints = constraints;
        this.criteria = c;
    }

    /** check if all parameters have been set */
    public boolean isConsistent() {
        return wakeupTime != null && startingLocation != null && criteria != null && appts.size() > 0;
    }


    public Schedule ComputeSchedule() {
        if(this.appts.size() > 0) {

            /** call API to optimize time during arragements computation */
            WeatherForecastAPIWrapper.getInstance().getWeather(this, appts.get(0).date, appts.get(0).coords);

            // 1-2
            CalculatePredecessorsAndDistanceMatrix(appts);

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

    @Override
    public void onWeatherResults(TimeWeatherList weatherConditionList) {
        this.weatherConditions = weatherConditionList;
    }

    void CalculatePredecessorsAndDistanceMatrix(ArrayList<Appointment> apps) {
        pred = new byte[apps.size()][apps.size()];

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
                distances.put(new AppointmentCouple(a1,a2), MapUtils.distance(a1.coords, a2.coords));
            }
        }
    }


    /**
     * Recursively compute dispositions of appointment
     */
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

        // all appointments have been ordered, calculate schedule
        ArrayList<Appointment> arrangement = ConvertPredMatrixToList(appts, pr);
        if(arrangement.size() == pred.length) {
            // schedule appointments in the arrangement
            Schedule s = GetScheduleFromArrangement(arrangement);
            if(s != null) {
                schedules.add(s);
                /** order schedules to update current minimum cost */

            } else {

                for(Appointment appt : arrangement) {
                    System.out.print(appt.toString() + " ");
                }
                System.out.println("- NON FATTIBILE");
            }
        }
    }



    /**
     * 	Assign starting times to ARRANGEMENT (ordered appointments) and check the schedule feasibility
     */
    Schedule GetScheduleFromArrangement(ArrayList<Appointment> arrangement) {
        ArrayList<TemporaryAppointment> temporaryAppts = new ArrayList<>();

        /** keep track of means usage (to not violating constraints) */
        TravelMeansState state = new TravelMeansState(constraints);

        // create wakeup dummy appointment
        Appointment wakeUpAppt = new Appointment("WakeUp", appts.get(0).date, wakeupTime, 0, startingLocation);
        temporaryAppts.add(new TemporaryAppointment(wakeUpAppt, wakeupTime, wakeupTime));

        Appointment firstAppt = arrangement.get(0);
        if(firstAppt.isDeterministic()) {
            ArrayList<TravelMeanCostCouple> means = GetUsableTravelMeansOrderedByCost(wakeUpAppt, firstAppt, state);
            /** use FIRST BEST BEST */
            TravelMean mean = means.get(0).getTravelMean();
            int travelTime1 = (int) mean.EstimateTime(wakeUpAppt, firstAppt, distances.get(new AppointmentCouple(wakeUpAppt, firstAppt)));
            LocalTime startingTime1 = firstAppt.startingTime.minusSeconds(travelTime1);

            temporaryAppts.add(new TemporaryAppointment(firstAppt, startingTime1, firstAppt.startingTime, means));

            // check if feasible
            if(startingTime1.isBefore(wakeupTime)) return null;

        } else {		// first appt is not deterministic
            ArrayList<TravelMeanCostCouple> means = GetUsableTravelMeansOrderedByCost(wakeUpAppt, firstAppt, state);
            int travelTime1 = (int)means.EstimateTime(wakeUpAppt, firstAppt, distances.get(new AppointmentCouple(wakeUpAppt, firstAppt)));

            if(arrangement.size() == 1) {
                LocalTime ETA1 = firstAppt.timeSlot.endingTime.minusSeconds(firstAppt.duration);
                LocalTime startingTime1 = ETA1.minusSeconds(travelTime1);
                temporaryAppts.add(new TemporaryAppointment(firstAppt, startingTime1, ETA1, means));

                //check if feasible
                if(startingTime1.isBefore(wakeupTime)) return null;
            } else {
                Appointment secondAppt = arrangement.get(1);
                int travelTime2 = (int)means.EstimateTime(firstAppt, secondAppt, distances.get(new AppointmentCouple(firstAppt, secondAppt)));

                LocalTime maxEndingTime1 = secondAppt.isDeterministic() ? secondAppt.startingTime.minusSeconds(travelTime2) :
                        secondAppt.timeSlot.startingTime.minusSeconds(travelTime2);
                LocalTime endingTime1 = DateManager.MinTime(firstAppt.timeSlot.endingTime, maxEndingTime1);
                LocalTime ETA1 = endingTime1.minusSeconds(firstAppt.duration);
                LocalTime startingTime1 = ETA1.minusSeconds(travelTime1);
                TemporaryAppointment firstSchedAppt = new TemporaryAppointment(firstAppt, startingTime1, ETA1, means);
                temporaryAppts.add(firstSchedAppt);
                // check if feasible
                if(startingTime1.isBefore(wakeupTime) || startingTime1.isBefore(firstAppt.timeSlot.startingTime)) return null;
            }
        }

        // only appt2 must be scheduled for each cycle
        for(int i=1; i < arrangement.size(); i++) {
            TemporaryAppointment appt1 = temporaryAppts.get(i);
            Appointment appt2 = arrangement.get(i);

            ArrayList<TravelMeanCostCouple> means = GetUsableTravelMeansOrderedByCost(appt1.originalAppointment, appt2, state);
            int travelTime = (int)means.EstimateTime(appt1.originalAppointment, appt2, distances.get(new AppointmentCouple(appt1.originalAppointment, appt2)));

            if(appt2.isDeterministic()) {
                // both deterministic, only creates scheduledAppointment with recalculated startingTime and ETA
                LocalTime fixedStartingTime = appt2.startingTime.minusSeconds(travelTime);
                temporaryAppts.add(new TemporaryAppointment(appt2, fixedStartingTime, appt2.startingTime, means));

                // check if overlapping
                if(appt1.endingTime().isAfter(fixedStartingTime)) return null;

            } else {
                // 2nd non deterministic, assing startingTime at max possible
                LocalTime fixedStartingTime = DateManager.MaxTime(appt1.endingTime(), appt2.timeSlot.startingTime.minusSeconds(travelTime));
                LocalTime ETA2 = fixedStartingTime.plusSeconds(travelTime);

                temporaryAppts.add(new TemporaryAppointment(appt2, fixedStartingTime, ETA2, means));

                //Check overlapping
                if(appt2.timeSlot.endingTime.isBefore(ETA2.plusSeconds(appt2.duration))) return null;
            }

        }

        return new Schedule(temporaryAppts);
    }


    ArrayList<TravelMeanCostCouple> GetUsableTravelMeansOrderedByCost(Appointment a1, Appointment a2, TravelMeansState state) {
        ArrayList<TravelMeanEnum> availableMeans = new ArrayList<>( Arrays.asList(TravelMeanEnum.values()) );

        /** discard travel means that are not allowed by constraints on SCHEDULES */
        for(ConstraintOnSchedule constraint : constraints) {
            if(constraint.maxDistance == 0 &&
                constraint.weather.contains(weatherConditions.getWeatherForTime(a1.endingTime()))
                || (constraint.timeSlot.endingTime.isAfter(a1.endingTime()) && constraint.timeSlot.startingTime.isBefore(a2.startingTime))
                )
                availableMeans.remove(constraint.mean);
        }
        /** discard travel means that are not allowed by constraints on APPOINTMENTS */
        for(ConstraintOnAppointment c : a2.getConstraints()) {
            if(c.maxDistance == 0) availableMeans.remove(c.mean);
        }
        /** compute cost for each remaining mean */
        ArrayList<TravelMeanCostCouple> meansQueue = new ArrayList<>();
        switch (criteria) {
            case OPTIMIZE_TIME:
                for ( TravelMeanEnum mean: availableMeans ) {
                    TravelMean tm = getTravelMean(mean);
                    meansQueue.add(new TravelMeanCostCouple(tm, tm.EstimateTime(a1, a2, distances.get(new AppointmentCouple(a1,a2)))));
                }
            case OPTIMIZE_CARBON:
                for ( TravelMeanEnum mean: availableMeans ) {
                    TravelMean tm = getTravelMean(mean);
                    meansQueue.add(new TravelMeanCostCouple(tm, tm.EstimateCarbon(a1, a2, distances.get(new AppointmentCouple(a1,a2)))));
                }
            case OPTIMIZE_COST:
                for ( TravelMeanEnum mean: availableMeans ) {
                    TravelMean tm = getTravelMean(mean);
                    meansQueue.add(new TravelMeanCostCouple(tm, tm.EstimateCost(a1, a2, distances.get(new AppointmentCouple(a1,a2)))));
                }
        }
        /** order the remaining means by cost */
        Collections.sort( meansQueue );
        return meansQueue;
    }



    /**
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





    public boolean addConstraintToUnfeasibleSchedule (ArrayList<TemporaryAppointment> arrangment, TravelMeansState state){

        ArrayList<TemporaryAppointment> subArrangmentTimeFlaged = null;
        ArrayList<TemporaryAppointment> subArrangmentMeanFlaged = null;


        boolean arrangmentIsTimeConflicting = false;
        boolean arrangmentIsMeanConflicting = false;
        boolean mustReiterate = false;

        /**
         * Check if an appointment has a TimeConflict Flag UP
         */
        for (TemporaryAppointment appt: arrangment) {
            if (appt.isTimeConflicting == true){
                subArrangmentTimeFlaged.add(appt);
                arrangmentIsTimeConflicting = true;
            }
        }

        /**
         * If at least one appointment has a TimeConflict Flag UP
         */
        if(arrangmentIsTimeConflicting){
            int value = 0;
            int index;
            for (TemporaryAppointment appt: subArrangmentTimeFlaged) {
                if(appt.means.size() > 1){
                    TravelMeanCostCouple tmcc = appt.means.get(1);

                }
            }

        /**
         * If there aren't appointment with TimeConflict we must check for Mean conflicts
         */
        }else{
            TravelMeanEnum conflictingMean = null;
            Weather appointmentWeather = null;
            for (TravelMeanWeatherCouple a: state.meansState.keySet()) {
                if (state.meansState.get(a) <= 0) {
                    arrangmentIsMeanConflicting = true;
                    conflictingMean = a.mean;
                    appointmentWeather = a.weather;
                    break;
                }
            }
            /**
             * If at least one appointment has a MeanConflict Flag UP
             */
            if(conflictingMean != null){
                for (TemporaryAppointment appt: arrangment) {
                    if (appt.means.get(0).mean == getTravelMean(conflictingMean) &&
                            weatherConditions.getWeatherForTime(appt.startingTime) == appointmentWeather){
                        subArrangmentMeanFlaged.add(appt);
                        arrangmentIsMeanConflicting = true;
                    }
                }
            //TODO====================
            }
        }
        return mustReiterate;
    }



    /**
    **  +++++ AUXILIARY FUNCTIONS +++++
    **
    */

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

    /**
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