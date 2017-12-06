/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gabdampar.travlendar.Controller.MappingServiceAPIWrapper;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MainPageActivity extends FragmentActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

    }

    //called when appointments Button is clicked
    public void AppointmentsListRequest(View view){

        //TO ACTIVATE MAP
        /*MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        Intent intent = new Intent(this, AppointmentsListActivity.class);
        startActivity(intent);
    }

    public void ShowSchedulePage(View view) {
        Intent intent = new Intent(this, ScheduleListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<TravelMeanEnum> l = new ArrayList<TravelMeanEnum>();
        l.add(TravelMeanEnum.BUS);

        MappingServiceAPIWrapper.getInstance().getTravelOptionData(l, "Viale delle Rimembranze di Lambrate", "Duomo di Milano", new DateTime(2017,12,6,12,50));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.4781108,9.2250824), 12.0f));
    }
}