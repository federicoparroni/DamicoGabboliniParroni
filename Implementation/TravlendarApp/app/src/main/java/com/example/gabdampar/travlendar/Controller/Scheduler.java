/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.gabdampar.travlendar.Controller.ViewController.MainActivity;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.AppointmentCouple;
import com.example.gabdampar.travlendar.Model.ConstraintOnAppointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.TemporaryAppointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.Model.TravelOptionData;
import com.example.gabdampar.travlendar.Model.Weather;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanCostTimeInfo;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanWeatherCouple;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeansState;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class Scheduler{

    public LocalTime scheduleStartingTime;
    public LatLng startingLocation;
    public ArrayList<Appointment> appts = new ArrayList<>();
    public ArrayList<ConstraintOnSchedule> constraints = new ArrayList<>();
    public OptCriteria criteria;
    public TimeWeatherList weatherConditions;

    // intermediate computation result
    private byte[][] pred;
    private HashMap<AppointmentCouple, Float> distances = new HashMap<>();
    /** contains the arrangement computed until now */
    private ArrayList<ArrayList<Appointment>> arrangements = new ArrayList<>();
    /** contains the schedules computed until now */
    private ArrayList<Schedule> schedules = new ArrayList<>();

    private Appointment wakeUpAppt;
    private int sync;


    /** constructors */
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

    public void ComputeSchedule(Context context, final ScheduleCallbackListener listener) {
        if(this.appts.size() > 0) {
            sync = 2;


            // 1
            /** create and set wake-up dummy appointment */
            wakeUpAppt = new Appointment("WakeUp", appts.get(0).date, scheduleStartingTime, 0, startingLocation);
            AppointmentManager.GetInstance().setAllStopsCloseToAppointment(wakeUpAppt, new AppointmentManager.StopsListener() {
                @Override
                public void callbackStopListener(Appointment app) {
                    CommonCallback(listener);
                }
            });

            /** get weather conditions for select date and baricentre of the daily appointments */
            WeatherForecastAPIWrapper.getInstance().getWeather(context, new WeatherForecastAPIWrapper.WeatherForecastAPIWrapperCallBack() {
                @Override
                public void onWeatherResults(TimeWeatherList weatherConditionList) {
                    weatherConditions = weatherConditionList;
                    CommonCallback(listener);
                }
            }, appts.get(0).getDate(), MapUtils.baricentre(appts));

        } else {
            throw new IllegalArgumentException("Empty appointment list");
        }
    }

    private void CommonCallback(final ScheduleCallbackListener listener) {
        sync--;
        if(sync==0) Compute(listener);
    }

    private void Compute(final ScheduleCallbackListener listener) {
        // 2
        CalculatePredecessorsAndDistanceMatrix(appts);

        // 3
        CalculateArrangements(appts, pred.clone(), 0, 1);

        // 4
        //WaitForWeatherAPI();
        GetSchedulesFromArrangements();

        // 5
        /** order schedules to update current minimum cost */
        Collections.sort(schedules);

        // debug
        for (Schedule s : schedules) {
            System.out.printf("%s\n", s.toString());
        }

        /**
         * for the current schedule, retrieve all the data for the travel means linking the two appointments
         * if the time-bounds of the heuristical schedule is exceeded, the schedule to be considered not valid
         */
        i=0;
        j=1;
        getBestScheduleAsync(listener);
    }

    private int i=0;
    private int j=1;
    private void getBestScheduleAsync(final ScheduleCallbackListener listener){
        if(schedules.size()>0) {
            MappingServiceAPIWrapper.getInstance().getTravelOptionData(
                new MappingServiceAPIWrapper.MappingServiceCallbackListener() {
                    @Override
                    public void MappingServiceCallback(ArrayList<TravelOptionData> travelData) {
                        if(travelData.size()>0) {
                            /**
                             * in case of bike usage, we use our estimates of times (google doesnt provide estimates for bikes so far)
                             * also in the case of the car our estimation are used, until we wont have more accurate estimates
                             */
                            if (schedules.get(i).getScheduledAppts().get(j).travelMeanToUse.meanEnum==TravelMeanEnum.BIKE
                                    ||schedules.get(i).getScheduledAppts().get(j).travelMeanToUse.meanEnum==TravelMeanEnum.CAR){
                                schedules.get(i).getScheduledAppts().get(j).dataFromPreviousToThis = travelData.get(0);
                                schedules.get(i).getScheduledAppts().get(j).dataFromPreviousToThis.setTime(new TimeSlot(
                                        schedules.get(i).getScheduledAppts().get(j).startingTime,
                                        schedules.get(i).getScheduledAppts().get(j).ETA)
                                );
                                if ((j == schedules.get(i).getScheduledAppts().size()-1))
                                    listener.ScheduleCallback(schedules.get(i));
                                j++;
                            }
                            else {
                                int usefulTime = schedules.get(i).getScheduledAppts().get(j).ETA.getMillisOfDay() -
                                                    schedules.get(i).getScheduledAppts().get(j-1).endingTime().getMillisOfDay();
                                int travelTime = travelData.get(0).getTime().getDuration()*1000;
                                if(travelTime<usefulTime){
                                    if (travelData.get(0).getTime().endingTime.isAfter(
                                            schedules.get(i).getScheduledAppts().get(j).ETA)) {
                                        int slack = travelData.get(0).getTime().endingTime.getMillisOfDay() -
                                                schedules.get(i).getScheduledAppts().get(j).ETA.getMillisOfDay();
                                        travelData.get(0).getTime().startingTime.minusMillis(slack);
                                        travelData.get(0).getTime().endingTime.minusMillis(slack);
                                    }
                                    schedules.get(i).getScheduledAppts().get(j).dataFromPreviousToThis = travelData.get(0);
                                    if ((j == schedules.get(i).getScheduledAppts().size() - 1))
                                        listener.ScheduleCallback(schedules.get(i));
                                    j++;
                                } else {
                                    i++;
                                    j=1;
                                }
                            }
                        }
                        else{
                            i++;
                            j=1;
                        }

                        if(i==schedules.size())
                            listener.ScheduleCallback(null);
                        else
                            getBestScheduleAsync(listener);

                        /**
                         * an api call should be performed again
                         * except in the case of j being equal to the number of scheduled appointments of that schedule (the method should end in this case)
                         * and also in case of i beign large as the number of possible schedules
                         */

                    }},
                    new ArrayList<TravelMeanEnum>(Arrays.asList(schedules.get(i).getScheduledAppts().get(j).travelMeanToUse.meanEnum)),
                    schedules.get(i).getScheduledAppts().get(j - 1).originalAppointment.coords,
                    schedules.get(i).getScheduledAppts().get(j).originalAppointment.coords,
                    schedules.get(i).getScheduledAppts().get(j).originalAppointment.date.toDateTime(
                            schedules.get(i).getScheduledAppts().get(j).startingTime,
                            DateTimeZone.forID("Europe/Rome")));
        }
        else
            listener.ScheduleCallback(null);
    }

    /**
     * Populate pred matrix and distances list with data from the original list of appointments
     * @param apps: list of appointments
     */
    private void CalculatePredecessorsAndDistanceMatrix(ArrayList<Appointment> apps) {
        pred = new byte[apps.size()][apps.size()];

        for(int i=0; i < apps.size()-1; i++) {
            for(int j=i+1; j < apps.size(); j++) {
                Appointment a1 = apps.get(i);
                Appointment a2 = apps.get(j);

                if(a1.isDeterministic() && a2.isDeterministic()) {
                    if(a1.endingTime().isBefore(a2.startingTime)) pred[i][j] = 1;
                    else pred[i][j] = 0;
                } else
                if(a1.isDeterministic() && !a2.isDeterministic()) {
                    if(a1.endingTime().isBefore(a2.timeSlot.startingTime.plusSeconds(a2.duration))) pred[i][j] = 1;
                    else if(a1.startingTime.isAfter(a2.timeSlot.endingTime.minusSeconds(a2.duration))) pred[i][j] = 0;
                    else pred[i][j] = 2;
                } else
                if(!a1.isDeterministic() && a2.isDeterministic()) {
                    if(a1.timeSlot.endingTime.minusSeconds(a1.duration).isBefore(a2.startingTime)) pred[i][j] = 1;
                    else if(a1.timeSlot.startingTime.plusSeconds(a1.duration).isAfter(a2.endingTime())) pred[i][j] = 0;
                    else pred[i][j] = 2;
                } else
                if(!a1.isDeterministic() && !a2.isDeterministic()) {
                    if(a1.timeSlot.endingTime.minusSeconds(a1.duration).isBefore(a2.timeSlot.startingTime)) pred[i][j] = 1;
                    else if(a1.timeSlot.startingTime.plusSeconds(a1.duration).isAfter(a2.timeSlot.endingTime)) pred[i][j] = 0;
                    else pred[i][j] = 2;
                }

                // distance
                distances.put(new AppointmentCouple(a1,a2), MapUtils.distance(a1.coords, a2.coords));
                distances.put(new AppointmentCouple(a2,a1), MapUtils.distance(a2.coords, a1.coords));
            }
        }
    }


    /**
     * Recursively compute dispositions of appointment (arrangement)
     */
    private void CalculateArrangements(ArrayList<Appointment> appts, byte[][] pr, int curri, int currj) {

        for(int i=curri; i < appts.size()-1; i++) {
            for(int j=currj; j < appts.size(); j++) {
                if(pr[i][j] == 2) {

                    byte[][] pred0 = CloneMatrix(pr);
                    pred0[i][j] = 0;
                    CalculateArrangements(appts, pred0, i, j);

                    byte[][] pred1 = CloneMatrix(pr);
                    pred1[i][j] = 1;
                    CalculateArrangements(appts, pred1, i, j);

                    return;
                }
            }
            currj = i+1;
        }

        // all appointments have been ordered, add this arrangement to the list if valid
        ArrayList<Appointment> arrangement = ConvertPredMatrixToArrangement(appts, pr);
        if(arrangement != null) arrangements.add( arrangement );

    }


    /**
     * Get schedules for each arrangement in the list
     */
    private void GetSchedulesFromArrangements() {
        for(ArrayList<Appointment> arrgmt : arrangements) {
            Schedule s = GetScheduleFromArrangement(arrgmt);
            if(s != null) schedules.add(s);
            // else unfeasible schedule
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
        /** add wake up appointment to arrangement */
        AddWakeUpDistances(wakeUpAppt, arrangement.get(0));
        ArrayList<TemporaryAppointment> tempAppts = TemporaryAppointment.Create(arrangement);
        tempAppts.add(0, new TemporaryAppointment(wakeUpAppt, wakeUpAppt.startingTime, wakeUpAppt.startingTime, null));

        //float currentCost;
        boolean mustReiterate;       // current arrangement has conflicts

        /**
         *  | arrangement[0] | arrangement[1] | arrangement[2] | .....
         *  |     wake-up    |  tempAppts[0]  |  tempAppts[1]  |  tempAppts[2]  | .....
         **/

        do {
            //currentCost = 0;
            mustReiterate = false;
            /** keep track of means usage (to not violate constraints) */
            TravelMeansState state = new TravelMeansState(constraints);

            // only appt2 must be scheduled for each iteration,
            for (int i = 0; i < tempAppts.size()-1; i++) {
                TemporaryAppointment appt1 = tempAppts.get(i);
                Appointment appt2 = arrangement.get(i);

                float distance = distances.get(new AppointmentCouple(appt1.originalAppt, appt2));
                ArrayList<TravelMeanCostTimeInfo> means = UpdateStateWithBestMean(appt1, tempAppts.get(i+1), state, distance);
                if(means == null) {
                    TemporaryAppointment firstAppt = tempAppts.get(1);
                    if(firstAppt.means.size() > 1) {
                        firstAppt.incrementalConstraints.add(new ConstraintOnAppointment(firstAppt.means.get(0).getMean().meanEnum, 0));
                        mustReiterate = true;
                        break;
                    } else return null;
                } else {
                    if (means.size() == 0) return null;      // no more means available
                    // get best travel mean (first in list) travel time
                    int travelTime = (int) means.get(0).getTime(); //(int) bestMean.EstimateTime(appt1.originalAppt, appt2, distance);

                    if (appt2.isDeterministic()) {
                        // both deterministic, only creates scheduledAppointment with recalculated startingTime and ETA
                        LocalTime fixedStartingTime = appt2.startingTime.minusSeconds(travelTime);
                        tempAppts.get(i + 1).Set(appt2, fixedStartingTime, appt2.startingTime, means);
                        //currentCost += means.get(0).getCost();

                        // previous appt is overlapping with current one (deterministic)
                        if (appt1.endingTime().isAfter(fixedStartingTime)) {
                            mustReiterate = true;
                            SetFlagForTimeConflicts(tempAppts, i + 1);
                        }

                    } else {
                        // 2nd non deterministic, assing startingTime at max possible
                        LocalTime fixedStartingTime = DateManager.MaxTime(appt1.endingTime(), appt2.timeSlot.startingTime.minusSeconds(travelTime));
                        LocalTime ETA2 = fixedStartingTime.plusSeconds(travelTime);
                        tempAppts.get(i + 1).Set(appt2, fixedStartingTime, ETA2, means);
                        //currentCost += means.get(0).getCost();

                        // appt is out of its time slot bounds
                        if (appt2.timeSlot.endingTime.isBefore(ETA2.plusSeconds(appt2.duration))) {
                            mustReiterate = true;
                            SetFlagForTimeConflicts(tempAppts, i + 1);
                        }

                    }

                    if (mustReiterate) {
                        mustReiterate = addConstraintToUnfeasibleSchedule(tempAppts, state);
                        if (!mustReiterate) return null;
                    }

                }
            }
        }
        while (mustReiterate);

        //cost = currentCost;

        return new Schedule(tempAppts, criteria);
    }


    /**
     * Retrn the list of usable means to travel from a1 to a2 according to the specified state, ordered by cost
     * @param a1: starting appointment
     * @param a2: arrival appointment
     * @param state: current means state
     * @return ordered list of usable travel means with relative cost and travel time
     */
    private ArrayList<TravelMeanCostTimeInfo> GetUsableTravelMeansOrderedByCost(TemporaryAppointment a1, TemporaryAppointment a2, TravelMeansState state) {
        ArrayList<TravelMeanEnum> availableMeans = new ArrayList<>( Arrays.asList(TravelMeanEnum.values()) );

        /** discard travel means that are not allowed by MEANS OWNAGE */
        //CAR
        if(!IdentityManager.GetInstance().user.hasCar) availableMeans.remove(TravelMeanEnum.CAR);
        //BIKE
        if(!IdentityManager.GetInstance().user.hasBike) availableMeans.remove(TravelMeanEnum.BIKE);

        /** discard travel means that are not allowed by CONSTRAINTS on SCHEDULES */
        for(ConstraintOnSchedule constraint : constraints) {
            if (constraint.maxDistance == 0 && constraint.weather.contains(weatherConditions.getWeatherForTime(a1.endingTime()))) {
                availableMeans.remove(constraint.mean);
            }
            /** remove if it is not allowed by time slot, i.e.: |t.start ---- a1.ending -----t.end|  */
            if(constraint.timeSlot != null) {
                if((constraint.timeSlot.endingTime.isAfter(a1.endingTime()) && constraint.timeSlot.startingTime.isBefore(a1.endingTime())))
                    availableMeans.remove(constraint.mean);
            }
        }

        for(ConstraintOnAppointment c : a2.originalAppt.constraints) {
            if(c.maxDistance == 0) availableMeans.remove(c.mean);
        }
        /** discard travel means that are not allowed by CONSTRAINTS on APPOINTMENTS */
        for(ConstraintOnAppointment c : a2.incrementalConstraints) {
            if(c.maxDistance == 0) availableMeans.remove(c.mean);
        }
        /** discard travel means that are not allowed by CURRENT STATE (mean-weather remaining distance / means order) */
        Weather currentWeather = weatherConditions.getWeatherForTime(a1.endingTime());
        Iterator<TravelMeanEnum> it = availableMeans.iterator();
        while (it.hasNext()) {
            TravelMeanEnum tm = it.next();
            // remove a mean if it's remaining distance is negative OR the current mean indicator is greater than state indicator
            if( !TravelMean.isMeanUsable(state.currentMean, tm) ) {
                it.remove();
            } else {        // check remaining distance under current weather conditions
                for (TravelMeanWeatherCouple mwCouple : state.meansState.keySet()) {
                    if (mwCouple.mean == tm && mwCouple.weather == currentWeather && state.meansState.get(mwCouple) <= 0) {
                        it.remove();
                    }

                    // TODO: check if cost computation (under this) can be done here

                }
            }
        }
        /** compute cost for each remaining mean */
        ArrayList<TravelMeanCostTimeInfo> meansQueue = new ArrayList<>();
        it = availableMeans.iterator();
        while (it.hasNext()) {
            TravelMeanEnum mean = it.next();
            TravelMean tm = TravelMean.getTravelMean(mean);
            float distance = distances.get(new AppointmentCouple(a1.originalAppt,a2.originalAppt));
            float time = tm.EstimateTime(a1, a2, distance);

            if(time >= 0) {
                switch (criteria) {
                    case OPTIMIZE_TIME:
                        meansQueue.add(new TravelMeanCostTimeInfo(tm, time, time));
                        break;
                    case OPTIMIZE_CARBON:
                        meansQueue.add(new TravelMeanCostTimeInfo(tm, tm.EstimateCarbon(a1, a2, distance), time));
                        break;
                    case OPTIMIZE_COST:
                        meansQueue.add(new TravelMeanCostTimeInfo(tm, tm.EstimateCost(a1, a2, distance), time));
                        break;
                }
            } else {        // if time is negative, the current mean cannot be chosen because there are no near stops
                it.remove();
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
        if(means.size() == 0) {     // the first appointment has taken a mean that brings to an unfeasible configuration
            return null;
        } else {
            TravelMeanCostTimeInfo bestMean = means.get(0);
            Weather currentWeather = weatherConditions.getWeatherForTime(a1.endingTime());

            TravelMeanWeatherCouple key = new TravelMeanWeatherCouple(bestMean.getMean().meanEnum, currentWeather);
            if (state.meansState.containsKey(key)) {     // if means is contrained
                float newDistance = state.meansState.get(key) - distance;       // state.distance -= distance;
                state.meansState.remove(key);
                state.meansState.put(key, newDistance);

                // set flag for mean conflict
                if (newDistance < 0) a1.isMeanConflicting = true;
            }

            state.currentMean = bestMean.getMean().meanEnum;
            return means;
        }
    }

    /**
     * Set time conflicts flag for the appointment at index and its predecessors until the first deterministic
     * @param appts: list of appointments
     * @param index: index of conflicting appointment
     */
    private void SetFlagForTimeConflicts(ArrayList<TemporaryAppointment> appts, int index) {
        appts.get(index).isTimeConflicting = true;
        int i = index-1;
        while (i>=1 && !appts.get(i).originalAppt.isDeterministic()) {
            appts.get(i).isTimeConflicting = true;
            i--;
        }
    }

    /**
     * Elaborate predecessor matrix and return the appointment ordered by precedence
     * @param appts
     * @param pred
     * @return arrangement (ArrayList<Appointment>) if valid pred matrix, null if not a valid pred matrix
     */
    private ArrayList<Appointment> ConvertPredMatrixToArrangement(ArrayList<Appointment> appts, byte[][] pred) {
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

        if(res.size() == appts.size()) return res;
        else return null;
    }


    /**
     * Set a dummy constraint when a time-conflicting or mean-conflicting appointment is found in the arrangement
     * @param arrangment
     * @param state
     * @return true if the new arrangement must be recomputed with new added constraint, false if the arrangement is unfeasible
     */
    private boolean addConstraintToUnfeasibleSchedule (ArrayList<TemporaryAppointment> arrangment, TravelMeansState state) {

        boolean timeConflictFound = false;

        /** Take the best second mean between all appointments with a TimeConflict flag */
        float bestRelativeCost = -1;
        int index = -1;

        for (int i = 0; i < arrangment.size(); i++) {
            TemporaryAppointment appt = arrangment.get(i);
            if (appt.isTimeConflicting && appt.means.size() > 1) {
                timeConflictFound = true;
                TravelMeanCostTimeInfo tmcti = appt.means.get(1);
                if (tmcti.relativeCost > bestRelativeCost) {
                    bestRelativeCost = tmcti.relativeCost;
                    index = i;
                }
            }
        }

        if(timeConflictFound) {
            if (bestRelativeCost == -1) {
                return false;   // mustReiterate = false, because cannot add any constraints, so the schedule is unfeasible
            }
            else {
                // add the constraint to the most convinient appointment
                arrangment.get(index).incrementalConstraints.add(new ConstraintOnAppointment(
                        arrangment.get(index).means.get(0).getMean().meanEnum, 0));

                return true;
            }
        } else {
            /** if there aren't appointments with TimeConflict, we must check for Mean conflicts */

            boolean meanConflictFound = false;

            // get conflicting travel mean and weather
            TravelMeanEnum conflictingMean = null;
            Weather conflictingWeather = null;
            for (int i=1; i < arrangment.size(); i++) {
                TemporaryAppointment appt = arrangment.get(i);
                if (appt.isMeanConflicting) {
                    meanConflictFound = true;
                    conflictingMean = appt.means.get(0).getMean().meanEnum;
                    conflictingWeather = weatherConditions.getWeatherForTime(arrangment.get(i-1).endingTime());
                    break;
                }
            }
            /** if at least one appointment has a MeanConflict Flag UP */
            if (meanConflictFound) {
                float bestRelativeCost2 = -1;
                int index2 = -1;

                for (int i=1; i < arrangment.size(); i++) {
                    TemporaryAppointment appt = arrangment.get(i);
                    //if (appt.means != null) { // wakeup appt has means = null
                        TravelMean m = appt.means.get(0).getMean();
                        Weather w = weatherConditions.getWeatherForTime(arrangment.get(i-1).endingTime());
                        if ( m.meanEnum == conflictingMean && w == conflictingWeather && appt.means.size() > 1) {
                            TravelMeanCostTimeInfo tmcti = appt.means.get(1);
                            if (tmcti.relativeCost > bestRelativeCost2) {
                                bestRelativeCost2 = tmcti.relativeCost;
                                index2 = i;
                            }
                        }
                    //}
                }

                if (bestRelativeCost2 == 0) {
                    return false;   // mustReiterate = false, because cannot add any constraints, so the schedule is unfeasible
                }
                else {
                    // add the constraint to the most convinient appointment
                    arrangment.get(index2).incrementalConstraints.add(new ConstraintOnAppointment(
                            arrangment.get(index2).means.get(0).getMean().meanEnum, 0));
                    return true;
                }
            }
        }

        return false;
    }


    private void AddWakeUpDistances(Appointment wakeUpAppt, Appointment appt) {
        AppointmentCouple key = new AppointmentCouple(wakeUpAppt, appt);
        if(!distances.containsKey(key)) {
            distances.put(key, MapUtils.distance(wakeUpAppt.coords, appt.coords));
        }
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

    public interface ScheduleCallbackListener{

        void ScheduleCallback(Schedule schedule);

    }

}