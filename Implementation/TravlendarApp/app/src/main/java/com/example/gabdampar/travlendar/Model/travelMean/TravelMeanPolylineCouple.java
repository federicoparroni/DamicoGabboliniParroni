package com.example.gabdampar.travlendar.Model.travelMean;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by gabbo on 22/12/2017.
 */

public class TravelMeanPolylineCouple {
    public TravelMeanEnum travelMeanEnum;
    public PolylineOptions polylineOptions;

    public TravelMeanPolylineCouple(TravelMeanEnum travelMeanEnum, PolylineOptions polylineOptions) {
        this.travelMeanEnum = travelMeanEnum;
        this.polylineOptions = polylineOptions;
    }
}
