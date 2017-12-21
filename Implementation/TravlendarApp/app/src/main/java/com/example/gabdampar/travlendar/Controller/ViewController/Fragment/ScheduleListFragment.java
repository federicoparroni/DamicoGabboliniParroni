package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Controller.ViewController.AppointmentCreationActivity;
import com.example.gabdampar.travlendar.Controller.ViewController.ScheduleCreationActivity;
import com.example.gabdampar.travlendar.Controller.ViewController.ScheduleListViewAdapter;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import static android.app.Activity.RESULT_OK;

public class ScheduleListFragment extends Fragment implements OnMapReadyCallback{

    final int SCHEDULE_CREATION_REQUEST = 0;    // request code for startActivityForResult
    ListView scheduleListView;
    ScheduleListViewAdapter arrayAdapter;

    //used for the map on the onClick of an appointment
    MapFragment appointment_map;
    GoogleMap map;

    //position of the clicked schedule
    int pos;

    public ScheduleListFragment() {
        setHasOptionsMenu(true);    // to inflate appbar options menu
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getTitle().toString().equals("Current schedules")){
                arrayAdapter.showCurrentSchedules=true;
                arrayAdapter.getFilter().filter("");
            }
            if(item.getTitle().toString().equals("Past schedules")){
                arrayAdapter.showCurrentSchedules=false;
                arrayAdapter.getFilter().filter("");
            }
            return true;
        }

    };

    @Override
    public void onResume(){
        super.onResume();
        arrayAdapter.getFilter().filter("");
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        scheduleListView = view.findViewById(R.id.scheduleListView);
        arrayAdapter = new ScheduleListViewAdapter(getActivity(), ScheduleManager.GetInstance().schedulesList);
        arrayAdapter.getFilter().filter("");
        scheduleListView.setAdapter(arrayAdapter);


        /**
         * Set on long click event -> DELETION of a schedule
         *
         **/

        scheduleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Schedule Deletion")
                        // Set up the view
                        .setMessage("Do you want to delete this schedule?")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ScheduleManager.GetInstance().schedulesList.remove(pos);
                                //the adapter must redraw the list
                                arrayAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });

        /**
         * Set onClick event -> SCHEDULE PREVIEW: minimap + basic info
         *
         **/

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
                LayoutInflater inflater = LayoutInflater.from(builder.getContext());
                View inflatedView = inflater.inflate(R.layout.schedule_list_on_click, null);

                //save the position of the clicked schedule
                pos = i;

                //for the map in the onClick on an appointment
                appointment_map = (MapFragment) getFragmentManager().findFragmentById(R.id.appointment_map_on_click);
                appointment_map.getMapAsync(ScheduleListFragment.this);

                builder.setTitle("Schedule details")
                        .setView(inflatedView)
                        // Set up the view
                        .setPositiveButton("RUN",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ScheduleManager.GetInstance().runningSchedule = ScheduleManager.GetInstance().getSchedule(pos);
                                dialog.cancel();

                                //trick for the fragment nested in another fragment
                                getFragmentManager().beginTransaction().remove(appointment_map).commit();

                                //TODO: METTERE LO SCHEDULE IN RUNNING
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                //trick for the fragment nested in another fragment
                                getFragmentManager().beginTransaction().remove(appointment_map).commit();
                            }
                        });

                /*
                TODO: THE SAME AS BELOW BUT FOR THE CLICKED SCHEDULE
                //Fields of the inflated view
                TextView onClickListViewName = inflatedView.findViewById(R.id.onClickListViewName);
                TextView onClickListViewDate = inflatedView.findViewById(R.id.onClickListViewDate);
                TextView onClickListViewDuration = inflatedView.findViewById(R.id.onClickListViewDuration);
                TextView onClickListViewLocation = inflatedView.findViewById(R.id.onClickListViewLocation);
                TextView onClickListViewStartingTimeOrTimeSlot = inflatedView.findViewById(R.id.onClickListViewStartingTimeOrTimeSlot);
                TextView onClickListViewStartingTimeOrTimeSlotField = inflatedView.findViewById(R.id.onClickListViewStartingTimeOrTimeSlotField);

                //The clicked Appointment
                Appointment clickedAppointment = AppointmentManager.GetInstance().GetAppointment(i);

                //position of the clicked appointment needed for know which appointment has been click from the googleMapCallback
                pos = i;

                onClickListViewName.setText(clickedAppointment.toString());
                onClickListViewDate.setText(clickedAppointment.getDate().toString());
                onClickListViewDuration.setText(clickedAppointment.getStringDuration());
                onClickListViewLocation.setText(clickedAppointment.getLocation());
                if(clickedAppointment.getTimeSlot() == null) {
                    onClickListViewStartingTimeOrTimeSlotField.setText("Starting Time");
                    onClickListViewStartingTimeOrTimeSlot.setText(clickedAppointment.getStartingTime().toString("HH:mm"));
                }else {
                    onClickListViewStartingTimeOrTimeSlotField.setText("Time Slot");
                    onClickListViewStartingTimeOrTimeSlot.setText(clickedAppointment.getTimeSlot().toString());
                }
                */

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    //Set the Icon on the action bar for the creation of a new schedule
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule_creation, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_new_schedule:
                // User chose to add a new schedule
                Intent intent = new Intent(getActivity(), ScheduleCreationActivity.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized, invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case SCHEDULE_CREATION_REQUEST:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // The user picked a contact.
                    // The Intent's data Uri identifies which contact was selected.

                    // refresh schedule list
                }
            break;
        }
    }


    /**
     * CALLBACK of the google API, this is done when the map is returned and it is ready to be showned
     *
     **/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //disable the zoom option
        map.getUiSettings().setZoomGesturesEnabled(false);

        //disable the scroll gesture in the minimap
        map.getUiSettings().setScrollGesturesEnabled(false);

        //disable the google map button
        map.getUiSettings().setMapToolbarEnabled(false);

        Schedule s = ScheduleManager.GetInstance().getSchedule(pos);
        MapUtils.putMapMarkersGivenScheduledAppointmentsAndSetMapZoomToThose(map,s.getScheduledAppts());
        MapUtils.drawScheduleOnMap(s,map);
    }


}