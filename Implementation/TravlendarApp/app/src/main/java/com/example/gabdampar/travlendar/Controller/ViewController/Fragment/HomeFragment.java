/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gabdampar.travlendar.Controller.MappingServiceAPIWrapper;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // reference ui control here
        // TextField f = view.findViewById(...);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<TravelMeanEnum> l = new ArrayList<TravelMeanEnum>();
        l.add(TravelMeanEnum.BUS);

        MappingServiceAPIWrapper.getInstance().getTravelOptionData(l, "Viale delle Rimembranze di Lambrate", "Duomo di Milano", new DateTime(2017,12,6,12,50));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.4781108,9.2250824), 12.0f));
    }
}