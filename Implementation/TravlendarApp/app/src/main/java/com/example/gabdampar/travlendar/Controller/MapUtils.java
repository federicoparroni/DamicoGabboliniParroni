package com.example.gabdampar.travlendar.Controller;

import android.graphics.Color;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by gabbo on 10/12/2017.
 */

public class MapUtils {

    public static void disableNavigationButtons(GoogleMap map){
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    public static void drawPolyline(GoogleMap map, PolylineOptions polylineOptions, TravelMeanEnum meanEnum){
        polylineOptions.color(Color.RED);
        polylineOptions.width(25);
        map.addPolyline(polylineOptions);
    }

    public static void putMapMarkersGivenAppointment(GoogleMap map, Appointment appointment){
        map.addMarker(new MarkerOptions().position(appointment.coords)
                .title(appointment.toString()));
    }

    public static void putMapMarkersGivenAppointmentAndSetZoomOnIt(GoogleMap map, Appointment appointment){
        map.addMarker(new MarkerOptions().position(appointment.coords)
                .title(appointment.toString()));
        CameraUpdate cu = CameraUpdateFactory.newLatLng(appointment.getCoords());
        map.animateCamera(cu);
    }

    public static void putMapMarkersGivenAppointments(GoogleMap map, List<Appointment> appointments){
        for(Appointment a: appointments) {
            putMapMarkersGivenAppointment(map, a);
        }
    }

    public static void putMapMarkersGivenAppointmentsAndSetMapZoomToThose(GoogleMap map, List<Appointment> appointments){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Appointment a: appointments) {
            MarkerOptions marker = new MarkerOptions().position(a.coords)
                    .title(a.toString());
            map.addMarker(marker);
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        map.animateCamera(cu);
    }

    public static void putMapMarkersGivenScheduledAppointmentsAndSetMapZoomToThose(GoogleMap map, List<ScheduledAppointment> appointments){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(ScheduledAppointment a: appointments) {
            MarkerOptions marker = new MarkerOptions().position(a.originalAppointment.coords)
                    .title(a.toString());
            map.addMarker(marker);
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
        map.animateCamera(cu);
    }

    public static float distance(LatLng start, LatLng end){
        double lat_a = start.latitude;
        double lat_b = end.latitude;
        double lng_a = start.longitude;
        double lng_b = end.longitude;

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public static void drawScheduleOnMap(Schedule s,GoogleMap map){
        for(ScheduledAppointment sa : s.getScheduledAppts())
            if(sa.dataFromPreviousToThis!=null)
                MapUtils.drawPolyline(map, sa.dataFromPreviousToThis.getPolyline(), sa.travelMeanToUse.meanEnum);
    }
}
