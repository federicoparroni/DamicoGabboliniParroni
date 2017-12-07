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
import android.widget.ListView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.AppointmentsListViewAdapter;
import com.example.gabdampar.travlendar.Controller.Synchronizer;
import com.example.gabdampar.travlendar.Controller.ViewController.AppointmentCreationActivity;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;

import java.util.ArrayList;


public class AppointmentsListFragment extends Fragment {

    ListView appointmentListView;
    // appointmentList taken from the AppointmentManger
    ArrayList<Appointment> appointmentsList;

    // the adapter that will manage the appointmentListView
    ArrayAdapter<Appointment> arrayAdapter;//DEVO METTERCI QUELLO MIO CUSTOMIZATO

    public AppointmentsListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //TODO: AGGIUSTARE COORDS (METODO PER FINTO SYNCHRONIZER)
        Synchronizer.GetInstance().Synchronize();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_appointments_list, container, false);

        appointmentListView = fragmentView.findViewById(R.id.appointmentListView);
        appointmentsList = AppointmentManager.GetInstance().GetAppointmentList();

        //adapter of the listView
        arrayAdapter = new AppointmentsListViewAdapter(getActivity(), R.layout.appointment_list_row, appointmentsList);

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
                                AppointmentManager.GetInstance().RemoveFromList(pos);
                                //the adapter must redraw the list
                                appointmentListView.setAdapter(arrayAdapter);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
                LayoutInflater inflater = LayoutInflater.from(builder.getContext());
                View inflatedView = inflater.inflate(R.layout.appointment_list_on_click, null);

                builder.setTitle("Appointment details")
                        .setView(inflatedView)
                        // Set up the view
                        .setPositiveButton("EDIT",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent editAppointment = new Intent(getActivity().getApplicationContext(),AppointmentCreationActivity.class);
                                editAppointment.putExtra("position",i);
                                startActivity(editAppointment);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                //Fields of the inflated view
                TextView onClickListViewName = inflatedView.findViewById(R.id.onClickListViewName);
                TextView onClickListViewDate = inflatedView.findViewById(R.id.onClickListViewDate);
                TextView onClickListViewDuration = inflatedView.findViewById(R.id.onClickListViewDuration);
                TextView onClickListViewLocation = inflatedView.findViewById(R.id.onClickListViewLocation);
                TextView onClickListViewStartingTimeOrTimeSlot = inflatedView.findViewById(R.id.onClickListViewStartingTimeOrTimeSlot);
                TextView onClickListViewStartingTimeOrTimeSlotField = inflatedView.findViewById(R.id.onClickListViewStartingTimeOrTimeSlotField);

                //The clicked Appointment
                Appointment clickedAppointment = AppointmentManager.GetInstance().GetAppointment(i);

                onClickListViewName.setText(clickedAppointment.toString());
                onClickListViewDate.setText(clickedAppointment.getDate().toString());
                onClickListViewDuration.setText(clickedAppointment.getStringDuration());
                //TODO:onClickListViewLocation.setText(clickedAppointment.getLocation().toString());
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //needed for the refresh of the appointments list
    @Override public void onResume() {
        super.onResume();
        //during the resume of the view the adapter of the appointmentList is set
        appointmentListView.setAdapter(arrayAdapter);
    }

}
