package com.example.gabdampar.travlendar.Model;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanPolylineCouple;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.Bounds;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by gabbo on 05/12/2017.
 */

public class TravelOptionData {

    private double price, carbonEmission;
    private TimeSlot time;
    private Map<TravelMeanEnum,Double> meanToKmMap;
    private ArrayList<TravelMeanPolylineCouple> travelMeanPolylineCouples;

    public void setTravelMeanPolylineCouples(ArrayList<TravelMeanPolylineCouple> travelMeanPolylineCouples) {
        this.travelMeanPolylineCouples = travelMeanPolylineCouples;
    }

    public ArrayList<TravelMeanPolylineCouple> getTravelMeanPolylineCouples() {
        return travelMeanPolylineCouples;
    }
    private Bounds bounds;
    private String directions;

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCarbonEmission(double carbonEmission) {
        this.carbonEmission = carbonEmission;
    }

    public void setTime(TimeSlot time) {
        this.time = time;
    }

    public void setMeanToKmMap(Map<TravelMeanEnum, Double> map) { this.meanToKmMap = map; }

    public double getCarbonEmission() {
        return carbonEmission;
    }

    public TimeSlot getTime() {
        return time;
    }

    public Map<TravelMeanEnum, Double> getMeanToKmMap() {
        return meanToKmMap;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
}
