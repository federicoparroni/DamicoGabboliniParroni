package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.MapUtils;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnAppointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class AppointmentCreationActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Appointment creation activity view elements
    CheckBox checkBoxStartingTime;
    CheckBox checkBoxTimeSlot;
    CheckBox isRecurrentCheckBox;

    EditText appointmentNameField;
    EditText numberInvolvedPeopleField;
    TimePicker durationTimePicker;

    DatePicker datePicker;

    Button addConstraintButton;
    Button saveButton;

    PlaceAutocompleteFragment autocompleteFragment;
    SupportMapFragment appointment_map;
    GoogleMap map;

    //Appointment Field

    public String name;
    public LocalDate date;
    public LocalTime startingTime;
    public TimeSlot timeSlot;
    public int duration;   // seconds
    public int involvedPeople;
    public LatLng coords;
    public Boolean isRecurrent;
    public String location;
    public ArrayList<ConstraintOnAppointment> constraints;

    //position of the appointment in the list (for the editing)
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        checkBoxStartingTime = findViewById(R.id.checkBoxStartingTime);
        checkBoxTimeSlot = findViewById(R.id.checkBoxTimeSlot);
        isRecurrentCheckBox = findViewById(R.id.isRecurrent);
        appointmentNameField = findViewById(R.id.appointmentNameField);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        appointment_map =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.appointment_map);
        appointment_map.getMapAsync(this);

        durationTimePicker = findViewById(R.id.durationTimePicker);
        numberInvolvedPeopleField = findViewById(R.id.numberInvolvedPeopleField);
        datePicker = findViewById(R.id.datePicker);
        addConstraintButton = findViewById(R.id.addConstraintButton);
        saveButton = findViewById(R.id.saveButton);

        //position of the appointment in the list
        position = getIntent().getIntExtra("position",-1);

        //initialize the constraints
        if(position >= 0)
            constraints = AppointmentManager.GetInstance().getAppointmentList().get(position).constraints;
        else
            constraints = new ArrayList<>();


        durationTimePicker.setIs24HourView(true);
        durationTimePicker.setHour(1);
        durationTimePicker.setMinute(0);

        //for the autocomplete fragment
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();
                String placeName = place.getName().toString();
                coords = place.getLatLng();
                map.addMarker(new MarkerOptions().position(coords).title(placeName));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords,15));
                location = (String) place.getAddress();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
    }

    // when the user check the startingtime checkbox he will be sent to SettingStartingTimeActivity to set the time
    public void onCheckboxOfStartingTimeClicked(View view) {
        //the if below need for not open the page on dischek the checkbox
        if (checkBoxStartingTime.isChecked()) {
            //XOR of timeslot and startingtime
            checkBoxTimeSlot.setClickable(false);
            final Intent intent = new Intent(this, SettingStartingTimeActivity.class);
            intent.putExtra("position",position);
            //launch the activity to retrive the startingTime
            startActivityForResult(intent,1);
        }else{
            checkBoxTimeSlot.setClickable(true);
        }
    }

    // when the user check the timeslot checkbox he will be sent to SettingTimeSlotActivity to set the Time slot
    public void onCheckboxOfTimeSlotClicked(View view){
        //the if below need for not open the page on dischek the checkbox
        if(checkBoxTimeSlot.isChecked()){
            //XOR of timeslot and startingtime
            checkBoxStartingTime.setClickable(false);
            final Intent intent = new Intent(this, SettingTimeSlotActivity.class);
            intent.putExtra("position",position);
            startActivityForResult(intent,2);
        }else{
            checkBoxStartingTime.setClickable(true);
        }
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

        //setting for the editing
        if(position != -1){
            this.initEditAppointment();
        }

    }

    public void OnAddConstraintClick(View view){
        final Intent intent = new Intent(this, AddConstraintOnAppointmentActivity.class);
        intent.putExtra("position",position);
        startActivityForResult(intent,3);
    }

    public void OnSaveCliCk(View view){

        name = appointmentNameField.getText().toString();
        date = new LocalDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        duration = durationTimePicker.getHour() * 3600 + durationTimePicker.getMinute() * 60;
        involvedPeople = Integer.parseInt(numberInvolvedPeopleField.getText().toString());
        isRecurrent = isRecurrentCheckBox.isChecked();

            //need for know if the appointment is new or is been editing
            //int position = getIntent().getIntExtra("position",-1);

            //creation of a new appointment
            if (position == -1) {
                if ( name.isEmpty() || date == null || startingTime == null && timeSlot == null || duration == -1 ||
                        coords == null || involvedPeople <= -1 || isRecurrent == null) {
                    //Missing fields
                    Snackbar.make(view, "WARNING: Missing Field", Snackbar.LENGTH_LONG).show();
                } else {

                    //the new appointment created
                    Appointment appointment;
                    //Check if the appointment has a starting time or a time slot
                    if (checkBoxStartingTime.isChecked()) {
                        appointment = new Appointment(name, date, startingTime, null, duration, coords, location, involvedPeople, isRecurrent);
                    } else {
                        appointment = new Appointment(name, date, null, timeSlot, duration, coords, location, involvedPeople, isRecurrent);
                    }

                    AppointmentManager.GetInstance().setAllStopsCloseToAppointment(appointment, new AppointmentManager.StopsListener() {
                        @Override
                        public void callbackStopListener(Appointment app) {
                            // save appointments to file
                            AppointmentManager.GetInstance().saveAppointments(getApplicationContext());
                        }
                    });

                    AppointmentManager.GetInstance().apptList.add(appointment);

                    if(constraints.size() > 0)
                        appointment.setConstraints(constraints);

                    //verifying that the appointment is added to the appointment list
                    Log.e("addAppointmentToTheList", String.valueOf(AppointmentManager.GetInstance().apptList.size()));
                    super.onBackPressed();
                }
            }
            //editing of an existing appointment
            else {
                //appointment that must be modified
                Appointment appointment = AppointmentManager.GetInstance().GetAppointment(position);


                if (checkBoxStartingTime.isChecked()) {
                    if (startingTime == null)
                        startingTime = appointment.getStartingTime();
                    if (coords == null)
                        coords = appointment.getCoords();
                    if (name.isEmpty() || date == null || startingTime == null && timeSlot == null || duration == -1 ||
                            coords == null || involvedPeople <= -1 || isRecurrent == null) {
                        //Missing fields
                        Snackbar.make(view, "WARNING: Missing Field", Snackbar.LENGTH_LONG).show();
                    }else {
                        appointment.EditAppointment(name, date, startingTime, null, duration, coords, location, involvedPeople, isRecurrent);
                        //if the constraints has been changed update that ones
                        appointment.setConstraints(constraints);
                        super.onBackPressed();
                    }
                } else {
                    if (timeSlot == null) timeSlot = appointment.getTimeSlot();
                    if (coords == null) coords = appointment.getCoords();
                    if (name.isEmpty() || date == null || startingTime == null && timeSlot == null || duration == -1 ||
                            coords == null || involvedPeople <= -1 || isRecurrent == null) {
                        //Missing fields
                        Snackbar.make(view, "WARNING: Missing Field", Snackbar.LENGTH_LONG).show();
                    }else {
                        appointment.EditAppointment(name, date, null, timeSlot, duration, coords, location, involvedPeople, isRecurrent);
                        // if the constraints has been changed update that ones
                        appointment.setConstraints(constraints);
                        super.onBackPressed();
                    }
                }

                // clear the previous place stops from the hashmap
                appointment.distanceOfEachTransitStop.clear();
                //recreate the stops hashmap
                AppointmentManager.GetInstance().setAllStopsCloseToAppointment(appointment, new AppointmentManager.StopsListener() {
                    @Override
                    public void callbackStopListener(Appointment app) {
                        // save appointments to file
                        AppointmentManager.GetInstance().saveAppointments(getApplicationContext());
                    }
                });
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //onActivityResult
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                //assignment to the parameter of the new appointment the result
                startingTime = (LocalTime) data.getExtras().getSerializable("startingTime");
                timeSlot = null;
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2){
            if(resultCode == RESULT_OK){
                //Bundle extras = data.getExtras();
                LocalTime startingTimeSlotTime = (LocalTime) data.getBundleExtra("timeSlot").getSerializable("startingTime");
                LocalTime endingTimeSlotTime = (LocalTime)data.getBundleExtra("timeSlot").getSerializable("endingTime");
                timeSlot = new TimeSlot(startingTimeSlotTime,endingTimeSlotTime);
                startingTime = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 3){
            if(resultCode == RESULT_OK){
                constraints = data.getParcelableArrayListExtra("constraints");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void initEditAppointment(){
        //appointment to be modified
        Appointment appointment = AppointmentManager.GetInstance().GetAppointment(position);

        appointmentNameField.setText(appointment.toString());

        //init the marker on the postition of the appointment on the map
        MapUtils.putMapMarkersGivenAppointment(map,appointment);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(appointment.getCoords(),15));
        autocompleteFragment.setText(appointment.getLocation());

        datePicker.init(appointment.getDate().getYear(),appointment.getDate().getMonthOfYear()-1,
                appointment.getDate().getDayOfMonth(),null);

        durationTimePicker.setHour((int) appointment.getDuration()/3600);
        durationTimePicker.setMinute((appointment.getDuration()/60)%60);

        numberInvolvedPeopleField.setText(String.valueOf(appointment.involvedPeople));
        isRecurrentCheckBox.setChecked(appointment.isRecurrent);

        //check wheter the appointment is deterministic or not
        if(appointment.getTimeSlot() == null){
            checkBoxStartingTime.setChecked(true);

        }else{
            checkBoxTimeSlot.setChecked(true);
        }

        //xor between starting time and timeslot
        if (checkBoxStartingTime.isChecked()) {
            //XOR of timeslot and startingtime
            checkBoxTimeSlot.setClickable(false);
        }

        if(checkBoxTimeSlot.isChecked()) {
            //XOR of timeslot and startingtime
            checkBoxStartingTime.setClickable(false);
        }
    }

}
