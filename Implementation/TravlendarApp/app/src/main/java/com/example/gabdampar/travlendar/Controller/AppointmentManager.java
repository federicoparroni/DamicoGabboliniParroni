package com.example.gabdampar.travlendar.Controller;


import android.content.Context;
import android.util.Log;

import com.example.gabdampar.travlendar.Controller.JsonSerializer.LatLngSerializer;
import com.example.gabdampar.travlendar.Controller.JsonSerializer.LocalDateSerializer;
import com.example.gabdampar.travlendar.Controller.JsonSerializer.LocalTimeSerializer;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Edoardo D'Amico on 02/12/2017.
 */

public class AppointmentManager {

    public ArrayList<Appointment> apptList;
    int sync=0;
    private static AppointmentManager instance;

    public AppointmentManager(){
        apptList=new ArrayList<>();
    }

    // singleton
    public static AppointmentManager GetInstance() {
        if(instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public ArrayList<Appointment> getAppointmentList(){
        return apptList;
    }

    public Appointment GetAppointment(int position){
        return apptList.get(position);
    }

    public ArrayList<Appointment> GetAppointmentsByDate(LocalDate date) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a: apptList) {
            if( a.getDate().equals(date) ) result.add(a);
        }
        return result;
    }

    /**
     * TODO: synchronize the results of these call, a schedule can be created only after the results of these calls
     */
    public void setAllStopsCloseToAppointment(final Appointment app, final StopsListener listener){
        sync=0;
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null) {
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, latLng);
                }
                if(sync==4){
                    listener.callbackStopListener(app);
                }
            }
        }, TravelMeanEnum.TRAM, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null) {
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, latLng);
                }
                if(sync==4){
                    listener.callbackStopListener(app);
                }
            }
        }, TravelMeanEnum.BUS, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if(latLng!=null) {
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, latLng);
                }
                if(sync==4){
                    listener.callbackStopListener(app);
                }
            }
        }, TravelMeanEnum.METRO, app.coords, 2000);
        MappingServiceAPIWrapper.getInstance().getStopDistance(new MappingServiceAPIWrapper.StopServiceCallbackListener() {
            @Override
            public void StopServiceCallback(LatLng latLng) {
                sync++;
                if (latLng != null){
                    app.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, latLng);
                }
                if(sync==4){
                    listener.callbackStopListener(app);
                }
            }
        }, TravelMeanEnum.TRAIN, app.coords, 2000);
    }

    /** APPOINTMENT PERSISTENCE */
    public boolean saveAppointments(Context context) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
                .registerTypeAdapter(LatLng.class, new LatLngSerializer());
        final Gson gson = builder.create();

        try {

            String json = gson.toJson(AppointmentManager.GetInstance().apptList.toArray());

            FileOutputStream fos = context.openFileOutput("appointments.json", Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();

        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            return false;
        }
        return true;
    }

    public ArrayList<Appointment> loadAppointments(Context context) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
                .registerTypeAdapter(LatLng.class, new LatLngSerializer());
        final Gson gson = builder.create();

        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(context.openFileInput("appointments.json")));
            StringBuffer text = new StringBuffer();
            String line;
            while ((line = bReader.readLine()) != null) {
                text.append(line + "\n");
            }

            Appointment[] result = gson.fromJson(text.toString(), Appointment[].class);
            return new ArrayList<>(Arrays.asList(result));
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            return null;
        }
    }



    /**
     * dummy appointments creational part
     */
    public void CreateDummyAppointments() {
        Appointment A = new Appointment("A", new LocalDate(2018, 1, 10),
                new LocalTime(11, 30), 20 * 60, new LatLng(45.4372464, 9.165939));
        A.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.4432113,9.1674042));
        A.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4456313,9.1638094));
        A.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4372427,9.168133));
        A.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4486643,9.1797234));


        Appointment B = new Appointment("B", new LocalDate(2018, 1, 10),
                new LocalTime(15, 0), 15 * 60, new LatLng(45.4781108, 9.2250824));
        B.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.4847227,9.236799899999998));
        B.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4811073,9.2160672));
        B.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4810088,9.2249933));
        B.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.47140590000001,9.2378056));


        Appointment C = new Appointment("C", new LocalDate(2018, 1, 10),
                new LocalTime(16, 0), 10 * 60, new LatLng(45.4641013, 9.1897325));
        C.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.468359,9.175595999999999));
        C.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4560192,9.1871891));
        C.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4641821,9.1896284));
        C.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4577231,9.1931098));


        Appointment D = new Appointment("D", new LocalDate(2018, 1, 10),
                new TimeSlot(new LocalTime(13, 30), new LocalTime(23, 40)), 5 * 60, new LatLng(45.4955892, 9.1919801));
        D.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.495387,9.175915));
        D.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.49571580000001,9.1919748));
        D.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.49614560000001,9.194823600000001));
        D.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.488764,9.183057));


        Appointment E = new Appointment("E", new LocalDate(2018,1,10),
                new TimeSlot(new LocalTime(10,0),new LocalTime(15,10)),
                10*60, new LatLng(45.4704799,9.1771438));
        E.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.468359,9.175595999999999));
        E.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4708503,9.164894199999999));
        E.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4781414,9.1566191));
        E.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.47812130000001,9.180900800000002));

        Appointment F = new Appointment("F", new LocalDate(2018,1,10),
                new TimeSlot(new LocalTime(14,30),new LocalTime(19,0)),
                25*60, new LatLng(45.5061279,9.1500127));
        F.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.50205820000001,9.150917999999997));
        F.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4976291,9.152635499999999));
        F.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4945885,9.141471199999998));

        apptList.add(A);
        apptList.add(B);
        apptList.add(C);
        apptList.add(D);
        apptList.add(E);
        apptList.add(F);

    }
    public interface StopsListener{
        void callbackStopListener(Appointment app);
    }
}
