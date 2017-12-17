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
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.Model.Weather;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanCostTimeInfo;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanWeatherCouple;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeansState;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.example.gabdampar.travlendar.Model.travelMean.TravelMean.getTravelMean;

public class Scheduler implements WeatherForecastAPIWrapper.WeatherForecastAPIWrapperCallBack {

    public LocalTime scheduleStartingTime;
    public LatLng startingLocation;
    public ArrayList<Appointment> appts = new ArrayList<>();
    public ArrayList<ConstraintOnSchedule> constraints = new ArrayList<>();
    public OptCriteria criteria;

    private TimeWeatherList weatherConditions;      // set by weather API callback

    private byte[][] pred;
    private HashMap<AppointmentCouple, Float> distances = new HashMap<>();

    /** contains the schedules computed until now */
    private ArrayList<Schedule> schedules = new ArrayList<>();
    //float cost = Float.MAX_VALUE;

    //ArrayList<Schedule> possibleSchedules = new ArrayList<Schedule>();

    public Scheduler() {}

    public Scheduler(LocalTime scheduleStartingTime, LatLng location, ArrayList<Appointment> appts, ArrayList<ConstraintOnSchedule> constraints, OptCriteria c) {
        this.scheduleStartingTime = scheduleStartingTime;
        this.startingLocation = location;
        this.appts = appts;
        this.constraints = constraints;
        this.criteria = c;
    }

    /** check if all parameters have been set */
    public boolean isConsistent() {
        return scheduleStartingTime != null && startingLocation != null && criteria != null && appts.size() > 0;
    }


    public Schedule ComputeSchedule() {
        if(this.appts.size() > 0) {

            //TODO DECIDERE SE SPOSTARE LA CHIAMATA ALLE API DEL METEO
            /** call API to optimize time during arragements computation */
            WeatherForecastAPIWrapper.getInstance().getWeather(this, appts.get(0).date, appts.get(0).coords);

            // 1-2
            CalculatePredecessorsAndDistanceMatrix(appts);

            // 3
            CalculateCombinations(appts, pred.clone(), 0, 1);

            // 4 TODO==========================
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

    private void CalculatePredecessorsAndDistanceMatrix(ArrayList<Appointment> apps) {
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
    private void CalculateCombinations(ArrayList<Appointment> appts, byte[][] pr, int curri, int currj) {

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

                /*for(Appointment appt : arrangement) {
                    System.out.print(appt.toString() + " ");
                }
                System.out.println("- NON FATTIBILE");
                */
            }
        }
    }



    /**
     * 	Assign starting times and means to ARRANGEMENT (ordered appointments) and check the schedule feasibility
     * 	1- Return a schedule if feasible
     * 	2- Return NULL if unfeasible
     *
     * 	First appt is dummy wake-up appointment
     */
    private Schedule GetScheduleFromArrangement(ArrayList<Appointment> arrangement) {
        /** create and set wake-up dummy appointment */
        Appointment wakeUpAppt = new Appointment("WakeUp", appts.get(0).date, scheduleStartingTime, 0, startingLocation);
        //arrangement.add(wakeUpAppt);
        ArrayList<TemporaryAppointment> tempAppts = TemporaryAppointment.Create(arrangement);
        tempAppts.add(0, new TemporaryAppointment(wakeUpAppt, wakeUpAppt.startingTime, wakeUpAppt.startingTime, null));

        float currentCost = 0;
        boolean mustReiterate;       // current arrangement has conflicts

        /**
         *  | arrangement[0] | arrangement[1] | arrangement[2] | .....
         *  |     wake-up    |  tempAppts[0]  |  tempAppts[1]  |  tempAppts[2]  | .....
        **/
        do {
            currentCost = 0;
            mustReiterate = false;
            /** keep track of means usage (to not violate constraints) */
            TravelMeansState state = new TravelMeansState(constraints);

            // only appt2 must be scheduled for each iteration,
            for (int i = 1; i < tempAppts.size(); i++) {
                TemporaryAppointment appt1 = tempAppts.get(i);
                Appointment appt2 = arrangement.get(i);

                float distance = distances.get(new AppointmentCouple(appt1.originalAppt, appt2));
                ArrayList<TravelMeanCostTimeInfo> means = UpdateStateWithBestMean(tempAppts.get(0), tempAppts.get(1), state, distance);
                if(means.size() == 0) return null;      // no more means available
                TravelMean bestMean = means.get(0).getTravelMean();

                int travelTime = (int) bestMean.EstimateTime(appt1.originalAppt, appt2, distance);

                if (appt2.isDeterministic()) {
                    // both deterministic, only creates scheduledAppointment with recalculated startingTime and ETA
                    LocalTime fixedStartingTime = appt2.startingTime.minusSeconds(travelTime);
                    tempAppts.get(i).Set(appt2, fixedStartingTime, appt2.startingTime, means);
                    currentCost += means.get(0).getCost();

                    // previous appt is overlapping with current one (deterministic)
                    if (appt1.endingTime().isAfter(fixedStartingTime)) {
                        mustReiterate = true;
                        SetFlagForTimeConflicts(tempAppts, i);
                    }

                } else {
                    // 2nd non deterministic, assing startingTime at max possible
                    LocalTime fixedStartingTime = DateManager.MaxTime(appt1.endingTime(), appt2.timeSlot.startingTime.minusSeconds(travelTime));
                    LocalTime ETA2 = fixedStartingTime.plusSeconds(travelTime);
                    tempAppts.get(i).Set(appt2, fixedStartingTime, ETA2, means);
                    currentCost += means.get(0).getCost();

                    // appt is out of its time slot bounds
                    if (appt2.timeSlot.endingTime.isBefore(ETA2.plusSeconds(appt2.duration))) {
                        mustReiterate = true;
                        SetFlagForTimeConflicts(tempAppts, i);
                    }

                }
            }
            if(mustReiterate) {
               mustReiterate = addConstraintToUnfeasibleSchedule(tempAppts, state);
               if(!mustReiterate) return null;
            }
        }
        while (mustReiterate);

        //cost = currentCost;
        return new Schedule(tempAppts, currentCost);
    }


    private ArrayList<TravelMeanCostTimeInfo> GetUsableTravelMeansOrderedByCost(TemporaryAppointment a1, TemporaryAppointment a2, TravelMeansState state) {
        ArrayList<TravelMeanEnum> availableMeans = new ArrayList<>( Arrays.asList(TravelMeanEnum.values()) );

        /** discard travel means that are not allowed by CONSTRAINTS on SCHEDULES */
        for(ConstraintOnSchedule constraint : constraints) {
            if(constraint.maxDistance == 0 &&
                constraint.weather.contains(weatherConditions.getWeatherForTime(a1.endingTime()))
                || (constraint.timeSlot.endingTime.isAfter(a1.endingTime()) && constraint.timeSlot.startingTime.isBefore(a2.startingTime))
                )
                availableMeans.remove(constraint.mean);
        }
        /** discard travel means that are not allowed by CONSTRAINTS on APPOINTMENTS */
        for(ConstraintOnAppointment c : a2.incrementalConstraints) {
            if(c.maxDistance == 0) availableMeans.remove(c.mean);
        }
        /** discard travel means that are not allowed by CURRENT STATE (mean-weather remaining distance / means order) */
        Weather currentWeather = weatherConditions.getWeatherForTime(a1.endingTime());
        for(int i=0; i < availableMeans.size(); i++) {
            TravelMeanEnum tm = availableMeans.get(i);
            // remove a mean if it's remaining distance is negative OR the current mean indicator is greater than state indicator
            if( TravelMean.isMeanUsable(state.currentMean,tm)){
                availableMeans.remove(i);
            } else {        // check remaining distance under current weather conditions
                for (TravelMeanWeatherCouple mwCouple : state.meansState.keySet()) {
                    if (mwCouple.mean == tm && mwCouple.weather == currentWeather && state.meansState.get(mwCouple) < 0) {
                        availableMeans.remove(i);
                    }
                }
            }
        }
        /** compute cost for each remaining mean */
        ArrayList<TravelMeanCostTimeInfo> meansQueue = new ArrayList<>();
        for ( TravelMeanEnum mean: availableMeans ) {
            TravelMean tm = TravelMean.getTravelMean(mean);
            float distance = distances.get(new AppointmentCouple(a1.originalAppt,a2.originalAppt));
            float time = tm.EstimateTime(a1.originalAppt, a2.originalAppt, distance);

            switch (criteria) {
                case OPTIMIZE_TIME:
                    meansQueue.add(new TravelMeanCostTimeInfo(tm, time, time));
                    break;
                case OPTIMIZE_CARBON:
                    meansQueue.add(new TravelMeanCostTimeInfo(tm, tm.EstimateCarbon(a1.originalAppt, a2.originalAppt, distance), time));
                    break;
                case OPTIMIZE_COST:
                    meansQueue.add(new TravelMeanCostTimeInfo(tm, tm.EstimateCost(a1.originalAppt, a2.originalAppt, distance), time));
                    break;
            }
        }
        /** order the remaining means by cost */
        Collections.sort( meansQueue );
        TravelMeanCostTimeInfo.CleanUncovenientMeans(meansQueue);
        return meansQueue;
    }

    /**
     * Update the state by taking the best mean in the usable means collection
     * @param a1: appointment 1
     * @param a2: appointment 2
     * @param state
     * @return ordered collection of TravelMeanCostTimeInfo
     */
    private ArrayList<TravelMeanCostTimeInfo> UpdateStateWithBestMean(TemporaryAppointment a1, TemporaryAppointment a2, TravelMeansState state, float distance) {
        ArrayList<TravelMeanCostTimeInfo> means = GetUsableTravelMeansOrderedByCost(a1, a2, state);
        /** update state! */
        TravelMeanCostTimeInfo bestMean = means.get(0);
        Weather currentWeather = weatherConditions.getWeatherForTime(a1.endingTime());
        TravelMeanWeatherCouple key = new TravelMeanWeatherCouple(bestMean.getMean().descr, currentWeather);
        float newDistance = state.meansState.get(key) - distance;       // state.distance -= distance;
        state.meansState.remove(key);
        state.meansState.put(key, newDistance);
        // set flag for mean conflict
        if(newDistance < 0) a1.isMeanConflicting = true;

        return means;
    }

    private void SetFlagForTimeConflicts(ArrayList<TemporaryAppointment> appts, int index) {
        appts.get(index).isTimeConflicting = true;
        int i = index-1;
        while (i>=0 && !appts.get(i).originalAppt.isDeterministic()) {
            appts.get(i).isTimeConflicting = true;
            i--;
        }
    }

    /**
     * Elaborate predecessor matrix and return the appointment ordered by precedence
     * @param appts
     * @param pred
     * @return arrangement (ArrayList<Appointment>)
     */
    private ArrayList<Appointment> ConvertPredMatrixToList(ArrayList<Appointment> appts, byte[][] pred) {
        ArrayList<Appointment> res = new ArrayList<>();

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



    public boolean addConstraintToUnfeasibleSchedule (ArrayList<TemporaryAppointment> arrangment, TravelMeansState state) {

        ArrayList<TemporaryAppointment> subArrangmentTimeFlaged = null;
        ArrayList<TemporaryAppointment> subArrangmentMeanFlaged = null;


        boolean arrangmentIsTimeConflicting = false;
        boolean arrangmentIsMeanConflicting = false;
        boolean mustReiterate = true;

        /**
         * Check if an appointment has a TimeConflict Flag UP
         */
        for (TemporaryAppointment appt : arrangment) {
            if (appt.isTimeConflicting == true) {
                subArrangmentTimeFlaged.add(appt);
                arrangmentIsTimeConflicting = true;
            }
        }

        /**
         * If at least one appointment has a TimeConflict Flag UP
         */
        if (arrangmentIsTimeConflicting) {
            float value = 0;
            int index = -1;
            for (int i = 0; i < subArrangmentTimeFlaged.size(); i++) {
                TemporaryAppointment appt = subArrangmentTimeFlaged.get(i);
                if (appt.means.size() > 1) {
                    TravelMeanCostTimeInfo tmcti = appt.means.get(1);
                    if (tmcti.relativeCost > value) {
                        value = tmcti.relativeCost;
                        index = i;
                    }
                }
            }

            // not possible to add constraint so the schedule is unfeasible
            if (value == 0) {
                mustReiterate = false;
            }
            // add the constraint to the most convinient appointment
            else {
                subArrangmentTimeFlaged.get(index).incrementalConstraints.add(new ConstraintOnAppointment(
                        arrangment.get(index).means.get(0).getMean().descr, 0));
            }
            /**
             * If there aren't appointment with TimeConflict we must check for Mean conflicts
             */
        } else {
            TravelMeanEnum conflictingMean = null;
            Weather appointmentWeather = null;
            for (TravelMeanWeatherCouple a : state.meansState.keySet()) {
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
            if (arrangmentIsMeanConflicting) {
                for (TemporaryAppointment appt : arrangment) {
                    if (appt.means.get(0).getMean() == getTravelMean(conflictingMean) &&
                            weatherConditions.getWeatherForTime(appt.startingTime) == appointmentWeather) {
                        subArrangmentMeanFlaged.add(appt);
                    }
                }
                float value = 0;
                int index = -1;
                for (int i = 0; i < subArrangmentMeanFlaged.size(); i++) {
                    TemporaryAppointment appt = subArrangmentMeanFlaged.get(i);
                    if (appt.means.size() > 1) {
                        TravelMeanCostTimeInfo tmcti = appt.means.get(1);
                        if (tmcti.relativeCost > value) {
                            value = tmcti.relativeCost;
                            index = i;
                        }
                    }
                }

                // not possible to add constraint so the schedule is unfeasible
                if (value == 0) {
                    mustReiterate = false;
                }
                // add the constraint to the most convinient appointment
                else {
                    subArrangmentMeanFlaged.get(index).incrementalConstraints.add(new ConstraintOnAppointment(
                            arrangment.get(index).means.get(0).getMean().descr, 0));

                }
            }
        }
        return mustReiterate;
    }



    /**
    **  +++++ AUXILIARY FUNCTIONS +++++
    **
    */

    private int SumRow(byte[][] matrix, int riga) {
        int sum = 0;
        for(int i=riga+1; i < matrix.length; i++) {
            sum += matrix[riga][i];
        }
        return sum;
    }
    private int SumInvertedCol(byte[][] matrix, int col) {
        int sum = 0;
        for(int i=0; i < col; i++) {
            sum += matrix[i][col];
        }
        return col - sum;
    }

    /**
     * Create a clone of a matrix
     */
    private byte[][] CloneMatrix(byte[][] orig) {
        byte[][] clone = new byte[orig.length][orig.length];

        for(int i=0; i < orig.length; i++) {
            for(int j=0; j < orig.length; j++) {
                clone[i][j] = orig[i][j];
            }
        }
        return clone;
    }

    private void StampaArray(byte[][] m) {
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