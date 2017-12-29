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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Controller.NetworkManager;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Controller.Synchronizer;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    /**
     * infos about permission given to the user of the application
     */
    boolean mLocationPermissionGranted=false;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    /**
     * references to view elements
     */
    GoogleMap map;
    TextView directionTextView;
    LinearLayout layout;
    ScrollView scrollView;

    public void changeState(){
        if(ScheduleManager.GetInstance().runningSchedule==null) {
            layout.setWeightSum(4);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) scrollView.getLayoutParams();
            params.height = 0;
            scrollView.setLayoutParams(params);
            map.clear();
            askForPermissionAndShowUserPositionOnMap();
            directionTextView.setText("");
        }
        else {
            layout.setWeightSum(10);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) scrollView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            scrollView.setLayoutParams(params);
            map.clear();
            directionTextView.setText(ScheduleManager.GetInstance().getDirectionForRunningSchedule());
            askForPermissionAndShowUserPositionOnMap();
            MapUtils.drawScheduleOnMap(ScheduleManager.GetInstance().runningSchedule, map);
            MapUtils.putMapMarkersGivenScheduledAppointmentsAndSetMapZoomToThose(map, ScheduleManager.GetInstance().runningSchedule.getScheduledAppts());
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(ScheduleManager.GetInstance().runningSchedule==null){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
        else{
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.stop_schedule:
                ScheduleManager.GetInstance().runningSchedule=null;
                changeState();
                return true;
            case R.id.color_info:
                /**
                 * TODO: a view that shows the corrispondance between colors and travel means
                 */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppointmentManager.GetInstance().CreateDummyAppointments();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // synchronize appointments
        if(savedInstanceState == null) Synchronizer.GetInstance().Synchronize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // reference ui control here
        // TextField f = view.findViewById(...);
        directionTextView = view.findViewById(R.id.directionsTextView);
        layout=(LinearLayout)view.findViewById(R.id.linearLayoutMap);
        scrollView=view.findViewById(R.id.directionsScrollView);

        if(NetworkManager.isOnline(this.getContext())) {
            MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            //Snackbar.make(view, "Internet connection appears to be offline", Snackbar.LENGTH_LONG).show();
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //called when the map is ready
        map=googleMap;
        //MapUtils.putMapMarkersGivenAppointmentsAndSetMapZoomToThose(googleMap, AppointmentManager.GetInstance().apptList);
        MapUtils.disableNavigationButtons(map);
        askForPermissionAndShowUserPositionOnMap();
        changeState();
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
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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