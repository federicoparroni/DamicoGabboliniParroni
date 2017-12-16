package com.example.gabdampar.travlendar.Controller;
import android.util.Log;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.TravelOptionData;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;
import com.google.maps.model.VehicleType;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gabbo on 30/11/2017.
 */

public class MappingServiceAPIWrapper {

    private static MappingServiceAPIWrapper ourInstance;
    private HashMap<TravelMeanEnum,Double> map;
    private ArrayList<TravelOptionData> ret=new ArrayList<TravelOptionData>();

    public static MappingServiceAPIWrapper getInstance() {
        if(ourInstance==null){ ourInstance=new MappingServiceAPIWrapper(); }
        return ourInstance;
    }

    private MappingServiceAPIWrapper() {}

    public void getTravelOptionData(final MappingServiceCallbackListener listener, List<TravelMeanEnum> admittedMeans, String startingLocation, String endingLocaton, DateTime departureTime){

        ret.clear();

        for (TravelMeanEnum t:admittedMeans){

            /**
             * TODO: lets do it for all the categories admitted by google maps, in particular we
             * can select among travel modes and in particular for the transit travel mode there's another
             * particular description, so far we have done distinction just for buses.
             * we should first perform the calls on the public travel means that we want (performing a unique call)
             * then should be checked if also calls for bikes and cars should be performed
             */

            GeoApiContext.Builder b=new GeoApiContext.Builder();
            b.apiKey("AIzaSyAs4xaJnBh5JEsVm1MmQjg6CpUdwwL_Txk");

            DirectionsApiRequest d=DirectionsApi.getDirections(
                b.build(),
                startingLocation,
                endingLocaton
            );
            d.alternatives(true);
            d.departureTime(departureTime);

            /**
             * this in general will become the call for all the public travel means
             */
            d.mode(getGoogleEnumValueFromTravelMeanEnum(t));
            if(getGoogleEnumValueFromTravelMeanEnum(t)==TravelMode.TRANSIT)
                d.transitMode(getGoogleVehicleTypeFromTravelMeanEnum(t));

            try{
                d.setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        for(DirectionsRoute r : result.routes){
                            map = new HashMap<TravelMeanEnum,Double>();
                            TravelOptionData n = new TravelOptionData();
                            n.setCarbonEmission(0); //TODO
                            n.setPrice(0); //TODO
                            n.setPolyline(new PolylineOptions()
                                    .addAll(decodePoly(
                                            r.overviewPolyline.getEncodedPath())));
                            n.setBounds(r.bounds);
                            n.setDirections(getTextualDirectionsGivenRouteAndUpdateMap(r));
                            n.setTime(new TimeSlot(
                                    r.legs[0].departureTime.toLocalTime(),
                                    r.legs[r.legs.length-1].arrivalTime.toLocalTime()));
                            n.setMeanToKmMap(map);
                            ret.add(n);
                        }
                        // callback return
                        listener.MappingServiceCallback(ret);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (Exception e){
                Log.e("GoogleError", "Error while retrieving mapping service data");
            }
        }
    }

    private TravelMeanEnum getTravelMeanEnumValueFromGoogleEnum(TravelMode toConvert) {
        if(toConvert==TravelMode.BICYCLING)
            return TravelMeanEnum.BIKE;
        else if(toConvert==TravelMode.DRIVING)
            return TravelMeanEnum.CAR;
        else if(toConvert==TravelMode.WALKING);
            return TravelMeanEnum.WALK;
    }

    private TravelMode getGoogleEnumValueFromTravelMeanEnum(TravelMeanEnum toConvert) {
        if(toConvert==TravelMeanEnum.BIKE)
            return TravelMode.BICYCLING;
        else if(toConvert==TravelMeanEnum.CAR)
            return TravelMode.DRIVING;
        else if(toConvert==TravelMeanEnum.WALK)
            return TravelMode.WALKING;
        else
            return TravelMode.TRANSIT;
    }

    private TravelMeanEnum getTravelMeanEnumValueFromGoogleEnum(VehicleType toConvert) {
        if(toConvert==VehicleType.BUS)
            return TravelMeanEnum.BUS;
        else if(toConvert==VehicleType.HEAVY_RAIL
                ||toConvert==VehicleType.HIGH_SPEED_TRAIN
                ||toConvert==VehicleType.METRO_RAIL
                ||toConvert==VehicleType.MONORAIL
                ||toConvert==VehicleType.RAIL)
            return TravelMeanEnum.TRAIN;
        else if(toConvert==VehicleType.SUBWAY)
            return TravelMeanEnum.METRO;
        else if(toConvert==VehicleType.TRAM)
            return TravelMeanEnum.TRAM;
        else
            return null;
    }

    private TransitMode getGoogleVehicleTypeFromTravelMeanEnum(TravelMeanEnum toConvert){
        TransitMode r = null;
        switch (toConvert) {
            case BUS:
                r = TransitMode.BUS;
                break;
            case METRO:
                r= TransitMode.SUBWAY;
                break;
            case TRAIN:
                r= TransitMode.TRAIN;
                break;
            case TRAM:
                r= TransitMode.TRAM;
                break;
        }
        return r;
    }

    private String getTextualDirectionsGivenRouteAndUpdateMap(DirectionsRoute r){
        String s = "";
        for (int i=0; i<r.legs.length;i++) {
            DirectionsLeg l = r.legs[i];
            s += getTextualDirectionsGivenLegsAndUpdateMap(l);
        }
        return s;
    }

    private String getTextualDirectionsGivenLegsAndUpdateMap(DirectionsLeg l){
        String s = "+ Sei in " + l.startAddress + "\n";
        if(l.steps != null)
            for(int i=0; i<l.steps.length; i++){
                DirectionsStep st = l.steps[i];
                TravelMeanEnum obj;
                if(st.travelMode!=TravelMode.TRANSIT)
                    obj = getTravelMeanEnumValueFromGoogleEnum(st.travelMode);
                else
                    obj = getTravelMeanEnumValueFromGoogleEnum(st.transitDetails.line.vehicle.type);

                if (map.containsKey(obj)) {
                    Double old = map.get(obj);
                    map.remove(obj);
                    map.put(obj, old + Double.valueOf(st.distance.inMeters));
                }
                else
                    map.put(obj, Double.valueOf(st.distance.inMeters));

                s += getTextualDirectionsGivenStepsAndUpdateMap(st, "++");
            }
        return s+"+ Arrivato in: " + l.endAddress + "\n";
    }

    private String getTextualDirectionsGivenStepsAndUpdateMap(DirectionsStep st, String token){
        String s = token + " " + parseHTMLInstruction(st.htmlInstructions) + "\n";
        if(st.transitDetails!=null)
            s+= token + "+ Stop at " + st.transitDetails.arrivalStop.name + " after " + String.valueOf(st.transitDetails.numStops) + " stops \n";
        if(st.steps != null)
            for(int i=0; i<st.steps.length; i++) {
                DirectionsStep  ds = st.steps[i];
                s+= getTextualDirectionsGivenStepsAndUpdateMap(ds, token + "+");
            }

        return s;
    }

    private String parseHTMLInstruction(String s){
        boolean consider = true;
        String r = "";
        if(s!=null)
            for(int i=0;i<s.length();i++){
                char c = s.charAt(i);
                if(c == '<' && consider==true)
                    consider=false;
                if (consider==true)
                    r+=c;
                if(c == '>' && consider==false)
                    consider=true;
            }
        return r;
    }

    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0;
        int length = encoded.length();

        int latitude = 0;
        int longitude = 0;

        while(index < length){
            int b;
            int shift = 0;
            int result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int destLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latitude += destLat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b > 0x20);

            int destLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            longitude += destLong;

            poly.add(new LatLng((latitude / 1E5),(longitude / 1E5) ));
        }
        return poly;
    }

    public interface MappingServiceCallbackListener {

        void MappingServiceCallback(List<TravelOptionData> travelData);

    }

}

//era la prova di here
    /*public void prova() {
        CoreRouter router = new CoreRouter();

        // Select routing options
        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.PUBLIC_TRANSPORT);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        // Select Waypoints for your routes
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(49.1966286, -123.0053635)));
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(49.1947289, -123.1762924)));
        router.calculateRoute(routePlan, new RouterListener());
    }

    private final class RouterListener implements CoreRouter.Listener {

        // Method defined in Listener
        public void onProgress(int percentage) {
            // Display a message indicating calculation progress
        }

        // Method defined in Listener
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError error) {

        }
    }*/

