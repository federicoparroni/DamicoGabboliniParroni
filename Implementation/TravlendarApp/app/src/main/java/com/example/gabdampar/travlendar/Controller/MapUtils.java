package com.example.gabdampar.travlendar.Controller;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanPolylineCouple;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gabbo on 10/12/2017.
 */

public class MapUtils {

    public static void disableNavigationButtons(GoogleMap map){
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    public static void drawScheduleOnMap(Schedule s,GoogleMap map){
        for(ScheduledAppointment sa : s.getScheduledAppts())
            if(sa.dataFromPreviousToThis!=null) {
                /**
                 * turnaround: necessary since we treat bicycle as a special case of walk. now we have to do the conversion-back
                 */
                if(sa.travelMeanToUse.meanEnum.equals(TravelMeanEnum.BIKE)){
                    for(TravelMeanPolylineCouple travelMeanPolylineCouple : sa.dataFromPreviousToThis.getTravelMeanPolylineCouples())
                        drawPolyline(map,travelMeanPolylineCouple.polylineOptions, TravelMeanEnum.BIKE);
                }
                else {
                    MapUtils.drawPolyline(map, sa.dataFromPreviousToThis.getTravelMeanPolylineCouples());
                }
            }
    }

    public static void drawPolyline(GoogleMap map, ArrayList<TravelMeanPolylineCouple> travelMeanPolylineCouples){
        for(TravelMeanPolylineCouple travelMeanPolylineCouple : travelMeanPolylineCouples)
            drawPolyline(map,travelMeanPolylineCouple.polylineOptions, travelMeanPolylineCouple.travelMeanEnum);
    }

    private static void drawPolyline(GoogleMap map, PolylineOptions polylineOptions, TravelMeanEnum meanEnum){
        polylineOptions.color(TravelMean.getTravelMean(meanEnum).color);
        polylineOptions.width(15);
        if(meanEnum.equals(TravelMeanEnum.WALK)) {
            List<PatternItem> pattern = Arrays.<PatternItem>asList(
                    new Dot(), new Gap(20));
            polylineOptions.pattern(pattern);
        }
        map.addPolyline(polylineOptions);
    }

    public static void putMapMarkersGivenAppointment(GoogleMap map, Appointment appointment){
        map.addMarker(new MarkerOptions()
                .position(appointment.coords)
                .title(appointment.toString()));
    }

    public static void putMapMarkersGivenAppointmentsAndSetMapZoomToThose(GoogleMap map, List<Appointment> appointments){
        if(appointments.size()>0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Appointment a : appointments) {
                MarkerOptions marker = new MarkerOptions()
                        .position(a.coords)
                        .title(a.toString());
                map.addMarker(marker);
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            map.animateCamera(cu);
        }
    }

    public static void putMapMarkersGivenScheduledAppointmentsAndSetMapZoomToThose(GoogleMap map, Schedule schedule){
        List<ScheduledAppointment> appointments = schedule.getScheduledAppts();
        if(appointments.size()>0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ScheduledAppointment a : appointments) {
                MarkerOptions marker = new MarkerOptions()
                        .position(a.originalAppointment.coords)
                        .title(a.toString());
                map.addMarker(marker);
                builder.include(marker.getPosition());
            }
            /**
             * special behaviour for the starting appointment
             */
            MarkerOptions marker = new MarkerOptions()
                    .position(schedule.startingLocation)
                    .title("Starting Location");
            map.addMarker(marker);
            builder.include(marker.getPosition());

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
            map.animateCamera(cu);
        }
    }


    /**
     * Calculate distance in meters between 2 location using Haversine formula: https://en.wikipedia.org/wiki/Haversine_formula
     * @param start: location 1 coordinates
     * @param end: location 2 coordinates
     * @return distance (meters)
     */
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



    public static LatLng baricentre(ArrayList<Appointment> appts) {
        float latSum = 0;
        float longSum = 0;
        for (Appointment a : appts) {
            latSum += a.getCoords().latitude;
            longSum += a.getCoords().longitude;
        }
        return new LatLng(latSum/appts.size(), longSum/appts.size());
    }

}
