/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.AppointmentsListViewAdapter;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Controller.Synchronizer;
import com.example.gabdampar.travlendar.Controller.ViewController.AppointmentCreationActivity;
import com.example.gabdampar.travlendar.Controller.ViewController.MainActivity;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import static java.lang.Boolean.TRUE;


public class AppointmentsListFragment extends Fragment implements OnMapReadyCallback {

    ListView appointmentListView;
    // appointmentList taken from the AppointmentManger
    ArrayList<Appointment> appointmentsList;

    // the adapter that will manage the appointmentListView
    AppointmentsListViewAdapter arrayAdapter;

    //used for the map on the onClick of an appointment
    MapFragment appointment_map;
    GoogleMap map;

    //clicked appointment position
    int pos;

    //the date of the appointment that has to be shown, if it is null all appointment are shown
    public static LocalDate appointmentsDate;

    //text view of the filtered date
    TextView dateFilteredView;

    public AppointmentsListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        appointmentsDate = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_appointments_list, container, false);

        // initializing the filter date to all date the first time the view has been created
        dateFilteredView = fragmentView.findViewById(R.id.dateFilterView);
        dateFilteredView.setText("Date: All");

        appointmentListView = fragmentView.findViewById(R.id.appointmentListView);

        appointmentsList = AppointmentManager.GetInstance().getAppointmentList();

        //adapter of the listView
        arrayAdapter = new AppointmentsListViewAdapter(getActivity(), R.layout.row_appointment_list, appointmentsList);

        appointmentListView.setAdapter(arrayAdapter);

        //set of the OnLongClick of one item of the listView
        appointmentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Appointment Deletion")
                        // Set up the view
                        .setMessage("Do you want to delete this appointment?")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                AppointmentManager.GetInstance().apptList.remove(pos);
                                //the adapter must redraw the list
                                arrayAdapter.removeFromFilteredData(pos);
                                arrayAdapter.notifyDataSetChanged();
                                AppointmentManager.GetInstance().saveAppointments(getContext());
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

        //set onClick of one item of the listView event
        appointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
                LayoutInflater inflater = LayoutInflater.from(builder.getContext());
                View inflatedView = inflater.inflate(R.layout.appointment_list_on_click, null);

                //for the map in the onClick on an appointment
                appointment_map = (MapFragment) getFragmentManager().findFragmentById(R.id.appointment_map_on_click);
                appointment_map.getMapAsync(AppointmentsListFragment.this);

                builder.setTitle("Appointment details")
                        .setView(inflatedView)
                        // Set up the view
                        .setPositiveButton("EDIT",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                //trick for the fragment nested in another fragment
                                getFragmentManager().beginTransaction().remove(appointment_map).commit();

                                Intent editAppointment = new Intent(getActivity().getApplicationContext(),AppointmentCreationActivity.class);
                                editAppointment.putExtra("position",i);
                                startActivity(editAppointment);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                //trick for the fragment nested in another fragment
                                getFragmentManager().beginTransaction().remove(appointment_map).commit();
                            }
                        })
                        .setCancelable(false);

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

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        arrayAdapter.getFilter().filter("");

        return fragmentView;
    }

    //Set the Icon on the action bar for the creation of a new appointment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_appointment_list, menu);
    }

    //Method activation on the click of a Button on the actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAppointmentButton:
                // User chose to add a new appointment
                Intent createAppointment = new Intent(getActivity(), AppointmentCreationActivity.class);
                startActivity(createAppointment);
                return TRUE;
            case R.id.sortByDateButton:
                //button used to sort the appointments chosen a date
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
                LayoutInflater inflater = LayoutInflater.from(builder.getContext());
                View inflatedView = inflater.inflate(R.layout.appointment_list_date_sorter, null);

                final DatePicker appointmentsSorterDatePicker = inflatedView.findViewById(R.id.datePickerSorter);

                builder.setTitle("Select Date")
                        .setView(inflatedView)
                        // Set up the view
                        .setPositiveButton("SELECT",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                appointmentsDate =  new LocalDate(new LocalDate(appointmentsSorterDatePicker.getYear(),
                                        appointmentsSorterDatePicker.getMonth()+1,appointmentsSorterDatePicker.getDayOfMonth()));
                                arrayAdapter.getFilter().filter("");
                                dateFilteredView.setText("Date: " + appointmentsDate.toString());
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Show All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                appointmentsDate = null;
                                arrayAdapter.getFilter().filter("");
                                dateFilteredView.setText("Date: All");
                                dialog.cancel();
                            }
                        });

                //Fields of the inflated view

                //for show the alert
                AlertDialog alert = builder.create();
                alert.show();
                return TRUE;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //needed for the refresh of the appointments list
    @Override public void onResume() {
        //during the resume of the view the adapter of the appointmentList is set
        appointmentsDate=null;
        arrayAdapter.getFilter().filter("");
        arrayAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //disable the zoom option
        map.getUiSettings().setZoomGesturesEnabled(false);

        //disable the scroll gesture in the minimap
        map.getUiSettings().setScrollGesturesEnabled(false);

        //disable the google map button
        map.getUiSettings().setMapToolbarEnabled(false);

        //init the marker on the postition of the appointment on the map
        MapUtils.putMapMarkersGivenAppointment(map,AppointmentManager.GetInstance().GetAppointment(pos));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(AppointmentManager.GetInstance().GetAppointment(pos).getCoords(),15));
    }
}

