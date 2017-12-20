/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap map;
    boolean mLocationPermissionGranted=false;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // reference ui control here
        // TextField f = view.findViewById(...);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //called when the map is ready
        map=googleMap;
        MapUtils.putMapMarkersGivenAppointmentsAndSetMapZoomToThose(googleMap, AppointmentManager.GetInstance().apptList);
        MapUtils.disableNavigationButtons(map);
        askForPermissionAndShowUserPositionOnMap();

        if(ScheduleManager.GetInstance().runningSchedule!=null){
            /**
             * TODO: show the current running schedule with directions
             */
            for(ScheduledAppointment sa : ScheduleManager.GetInstance().runningSchedule.getScheduledAppts())
                if(sa.dataFromPreviousToThis!=null)
                    MapUtils.drawPolyline(googleMap, sa.dataFromPreviousToThis.getPolyline(), sa.travelMeanToUse.meanEnum);
        }
    }

    private void askForPermissionAndShowUserPositionOnMap(){
        if (mLocationPermissionGranted) {
            try {
                map.setMyLocationEnabled(true);
            }
            catch (SecurityException e) {e.printStackTrace();}
        }
        else {
            getLocationPermission();
            askForPermissionAndShowUserPositionOnMap();
        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
}