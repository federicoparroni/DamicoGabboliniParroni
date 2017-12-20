package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by Edoardo D'Amico on 04/12/2017.
 */

public class Synchronizer {

    // singleton
    private static Synchronizer instance;
    public static Synchronizer GetInstance() {
        if(instance == null) {
            instance = new Synchronizer();
        }
        return instance;
    }

    // TODO: (DUMMY METHOD) insert coord

    public void Synchronize() {
        /**
         * dummy appointments creational part
         */
        if(AppointmentManager.GetInstance().apptList.size() == 0) {

            Appointment A = new Appointment("A", new LocalDate(2017, 11, 18),
                    new LocalTime(11, 30), 20 * 60, new LatLng(45.4372464, 9.165939));
            A.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.4432113,9.1674042));
            A.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4456313,9.1638094));
            A.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4372427,9.168133));
            A.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4486643,9.1797234));


            Appointment B = new Appointment("B", new LocalDate(2017, 11, 18),
                    new LocalTime(15, 0), 15 * 60, new LatLng(45.4781108, 9.2250824));
            B.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.4847227,9.236799899999998));
            B.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4811073,9.2160672));
            B.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4810088,9.2249933));
            B.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.47140590000001,9.2378056));


            Appointment C = new Appointment("C", new LocalDate(2017, 11, 18),
                    new LocalTime(16, 0), 10 * 60, new LatLng(45.4641013, 9.1897325));
            C.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.468359,9.175595999999999));
            C.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4560192,9.1871891));
            C.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4641821,9.1896284));
            C.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4577231,9.1931098));


            Appointment D = new Appointment("D", new LocalDate(2017, 11, 18),
                    new TimeSlot(new LocalTime(13, 30), new LocalTime(23, 40)),
                    5 * 60, new LatLng(45.4955892, 9.1919801));
            D.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.495387,9.175915));
            D.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.49571580000001,9.1919748));
            D.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.49614560000001,9.194823600000001));
            D.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.488764,9.183057));


            Appointment E = new Appointment("E", new LocalDate(2017,11,18),
                    new TimeSlot(new LocalTime(10,0),new LocalTime(15,10)),
                    10*60, new LatLng(45.4704799,9.1771438));
            E.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.468359,9.175595999999999));
            E.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4708503,9.164894199999999));
            E.distanceOfEachTransitStop.put(TravelMeanEnum.METRO, new LatLng(45.4781414,9.1566191));
            E.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.47812130000001,9.180900800000002));


            Appointment F = new Appointment("F", new LocalDate(2017,11,18),
                    new TimeSlot(new LocalTime(14,30),new LocalTime(19,0)),
                    25*60, new LatLng(45.5061279,9.1500127));
            F.distanceOfEachTransitStop.put(TravelMeanEnum.TRAIN, new LatLng(45.50205820000001,9.150917999999997));
            F.distanceOfEachTransitStop.put(TravelMeanEnum.BUS, new LatLng(45.4976291,9.152635499999999));
            F.distanceOfEachTransitStop.put(TravelMeanEnum.TRAM, new LatLng(45.4945885,9.141471199999998));



            AppointmentManager.GetInstance().apptList.add(A);
            AppointmentManager.GetInstance().apptList.add(B);
            AppointmentManager.GetInstance().apptList.add(C);
            AppointmentManager.GetInstance().apptList.add(D);
            AppointmentManager.GetInstance().apptList.add(E);
            AppointmentManager.GetInstance().apptList.add(F);
        }
    }

}