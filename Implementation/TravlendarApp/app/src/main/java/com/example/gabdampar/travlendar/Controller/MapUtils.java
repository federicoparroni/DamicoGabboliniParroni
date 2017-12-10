package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by gabbo on 10/12/2017.
 */

public class MapUtils {
    public static void putMapMarkersGivenAppointment(GoogleMap map, Appointment appointment){
        map.addMarker(new MarkerOptions().position(appointment.coords)
                .title(appointment.toString()));
    }

    public static void putMapMarkersGivenAppointments(GoogleMap map, List<Appointment> appointments){
        for(Appointment a: appointments)
            putMapMarkersGivenAppointment(map,a);
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
}
