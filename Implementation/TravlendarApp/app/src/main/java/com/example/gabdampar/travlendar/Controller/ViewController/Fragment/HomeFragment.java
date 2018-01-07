/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

import org.joda.time.LocalDate;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    /**
     * infos about permission given to the user of the application
     */
    boolean mLocationPermissionGranted=false;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final int MAX_VISIBLE_DIRECTION_TEXTVIEW_LINES = 15;

    /**
     * references to view elements
     */
    GoogleMap map;
    TextView directionTextView;
    ConstraintLayout layout;
    ScrollView scrollView;

    public void changeState(){
        if(ScheduleManager.GetInstance().runningSchedule == null) {
            scrollView.getLayoutParams().height = 1;

            map.clear();
            askForPermissionAndShowUserPositionOnMap();
            MapUtils.putMapMarkersGivenAppointmentsAndSetMapZoomToThose(map, AppointmentManager.GetInstance().GetAppointmentsByDate(LocalDate.now()));
            directionTextView.setText("");
        }
        else {
            directionTextView.setText(ScheduleManager.GetInstance().getDirectionForRunningSchedule());

            int lineCount = Math.min(directionTextView.getLineCount(), MAX_VISIBLE_DIRECTION_TEXTVIEW_LINES);
            scrollView.getLayoutParams().height = lineCount * 55;

            map.clear();
            askForPermissionAndShowUserPositionOnMap();
            MapUtils.drawScheduleOnMap(ScheduleManager.GetInstance().runningSchedule, map);
            MapUtils.putMapMarkersGivenScheduledAppointmentsAndSetMapZoomToThose(map, ScheduleManager.GetInstance().runningSchedule);

            /** ask user if want to buy tickets */
            if(ScheduleManager.GetInstance().runningSchedule.canBuyTickets()) {
                // build dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tickets purchase")
                        .setMessage("Do you want to buy tickets for this schedule?")
                        .setPositiveButton("Yes, open ticket page", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ShowTicketsPurchaseDialog();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

            }
        }
        getActivity().invalidateOptionsMenu();
    }

    private void ShowTicketsPurchaseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        View inflatedView = inflater.inflate(R.layout.dialog_tickets_purchase, null);
        builder.setView(inflatedView)
                .setTitle("Tickets purchase page");
        final AlertDialog alert = builder.create();

        final WebView webView = inflatedView.findViewById(R.id.tickets_web_view);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("https://www.atm.it/it/ViaggiaConNoi/Abbonamenti/Pagine/Acquistionline.aspx");

        alert.show();
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
        layout = view.findViewById(R.id.linearLayoutMap);
        scrollView = view.findViewById(R.id.directionsScrollView);

        if(NetworkManager.isOnline(this.getContext())) {
            MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Snackbar.make(view, "Internet connection appears to be offline", Snackbar.LENGTH_LONG).show();
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
            if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                map.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

}