package com.example.gabdampar.travlendar.Controller;
import android.util.Log;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.TravelOptionData;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanPolylineCouple;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanWeatherCouple;
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
import com.google.maps.model.TransitRoutingPreference;
import com.google.maps.model.TravelMode;
import com.google.maps.model.VehicleType;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

/**
 * Created by gabbo on 30/11/2017.
 */

public class MappingServiceAPIWrapper{

    private static MappingServiceAPIWrapper ourInstance;
    private HashMap<TravelMeanEnum,Double> map;
    private ArrayList<TravelMeanPolylineCouple> polylineMeanList;
    private ArrayList<TravelOptionData> ret=new ArrayList<TravelOptionData>();

    public static MappingServiceAPIWrapper getInstance() {
        if(ourInstance==null){ ourInstance=new MappingServiceAPIWrapper(); }
        return ourInstance;
    }

    private MappingServiceAPIWrapper() {}

    public void getStopDistance(final StopServiceCallbackListener listener, TravelMeanEnum mean, final LatLng position, int radius){
        String type=null;
        switch (mean){
            case BUS:
                type=PlaceType.BUS_STATION;
                break;
            case TRAM:
                type=PlaceType.LIGHT_RAIL_STATION;
                break;
            case METRO:
                type=PlaceType.SUBWAY_STATION;
                break;
            case TRAIN:
                type=PlaceType.TRAIN_STATION;
                break;
        }
        if (type!=null)
            new NRPlaces.Builder()
                .listener(new PlacesListener() {
                    ArrayList<Place> list=new ArrayList<>();
                    @Override
                    public void onPlacesFailure(PlacesException e) {
                        listener.StopServiceCallback(null);
                    }

                    @Override
                    public void onPlacesSuccess(List<Place> places) {
                        for(Place p : places)
                            list.add(p);
                    }

                    @Override
                    public void onPlacesStart() {
                    }

                    @Override
                    public void onPlacesFinished() {
                        LatLng r=null;
                        float minDist = Float.MAX_VALUE;
                        for(Place p : list){
                            float actualDistance = MapUtils.distance(position,new LatLng(p.getLatitude(),p.getLongitude()));
                            if (actualDistance < minDist) {
                                minDist = actualDistance;
                                r = new LatLng(p.getLatitude(),p.getLongitude());
                            }
                        }
                        listener.StopServiceCallback(r);
                    }
                })
                .key("AIzaSyAs4xaJnBh5JEsVm1MmQjg6CpUdwwL_Txk")
                .latlng(position.latitude, position.longitude)
                .radius(radius)
                .type(type)
                .build()
                .execute();
        else
            listener.StopServiceCallback(null);
    }

    public void getTravelOptionData(final MappingServiceCallbackListener listener, List<TravelMeanEnum> admittedMeans, LatLng startingLocation, LatLng endingLocaton, final DateTime departureTime){

        ret.clear();

        for (TravelMeanEnum t:admittedMeans){

            GeoApiContext.Builder b=new GeoApiContext.Builder();
            b.apiKey("AIzaSyAs4xaJnBh5JEsVm1MmQjg6CpUdwwL_Txk");

            DirectionsApiRequest d=DirectionsApi.newRequest(
                b.build()
            );
            /**set this to false to speed up the whole scheduling alg
             * on the other hand we would have more alternatives among which to choose
             */
            d.alternatives(false);

            d.departureTime(departureTime);
            d.origin(new com.google.maps.model.LatLng(startingLocation.latitude,startingLocation.longitude));
            d.destination(new com.google.maps.model.LatLng(endingLocaton.latitude,endingLocaton.longitude));

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
                            polylineMeanList = new ArrayList<TravelMeanPolylineCouple>();
                            TravelOptionData n = new TravelOptionData();
                            n.setCarbonEmission(0); //TODO
                            n.setPrice(0); //TODO
                            n.setBounds(r.bounds);
                            n.setDirections(getTextualDirectionsGivenRouteAndUpdateMapAndUpdatePolylineList(r));
                            n.setMeanToKmMap(map);
                            /**
                             * special behaviour for bikes, we should replace walk mode with bike mode since the bicicle mode isnt available yet
                             * for milan zone in maps.
                             */
                            n.setTravelMeanPolylineCouples(polylineMeanList);
                            if(n.getMeanToKmMap().size()==1 && (n.getMeanToKmMap().containsKey(TravelMeanEnum.CAR))||(n.getMeanToKmMap().containsKey(TravelMeanEnum.WALK))){
                                n.setTime(new TimeSlot(departureTime.toLocalTime(),departureTime.plus(r.legs[0].duration.inSeconds*1000).toLocalTime()));
                            }
                            else{
                                n.setTime(new TimeSlot(
                                    r.legs[0].departureTime.toLocalTime(),
                                    r.legs[r.legs.length-1].arrivalTime.toLocalTime()));
                            }
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
            return TravelMode.WALKING;
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

    private String getTextualDirectionsGivenRouteAndUpdateMapAndUpdatePolylineList(DirectionsRoute r){
        String s = "";
        for (int i=0; i<r.legs.length;i++) {
            DirectionsLeg l = r.legs[i];
            s += getTextualDirectionsGivenLegsAndUpdateMapAndUpdatePolylineList(l);
        }
        return s;
    }

    private String getTextualDirectionsGivenLegsAndUpdateMapAndUpdatePolylineList(DirectionsLeg l){
        String s = "+ You are in: " + l.startAddress + "\n";
        if(l.steps != null)
            for(int i=0; i<l.steps.length; i++){
                DirectionsStep st = l.steps[i];
                TravelMeanEnum obj;
                if(st.travelMode!=TravelMode.TRANSIT)
                    obj = getTravelMeanEnumValueFromGoogleEnum(st.travelMode);
                else
                    obj = getTravelMeanEnumValueFromGoogleEnum(st.transitDetails.line.vehicle.type);

                /**
                 * update the distances map
                 */
                if (map.containsKey(obj)) {
                    Double old = map.get(obj);
                    map.remove(obj);
                    map.put(obj, old + Double.valueOf(st.distance.inMeters));
                }
                else
                    map.put(obj, Double.valueOf(st.distance.inMeters));

                /**
                 * update the polylines list
                 */
                polylineMeanList.add(new TravelMeanPolylineCouple(obj,new PolylineOptions().addAll(convertLatLong(st.polyline.decodePath()))));

                /**
                 * collect directions
                 */
                s += getTextualDirectionsGivenStepsAndUpdateMap(st, "++");
            }
        return s+"+ Arrived in: " + l.endAddress + "\n";
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

    private List<LatLng> convertLatLong(List<com.google.maps.model.LatLng> toConvert) {
        ArrayList<LatLng> r = new ArrayList<LatLng>();
        for (com.google.maps.model.LatLng latLng : toConvert)
            r.add(new LatLng(latLng.lat, latLng.lng));
        return r;
    }

    public interface MappingServiceCallbackListener {

        void MappingServiceCallback(ArrayList<TravelOptionData> travelData);

    }

    public interface StopServiceCallbackListener {

        void StopServiceCallback(LatLng latLng);

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