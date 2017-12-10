package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.model.LatLng;
import com.here.android.mpa.common.GeoCoordinate;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


public class AppointmentCreationActivity extends AppCompatActivity {

    //Appointment creation activity view elements
    CheckBox checkBoxStartingTime;
    CheckBox checkBoxTimeSlot;
    CheckBox isRecurrentCheckBox;

    EditText appointmentNameField;
    EditText locationField;
    EditText numberInvolvedPeopleField;
    EditText durationHours;
    EditText durationMinutes;

    TimePicker durationTimePicker;

    DatePicker datePicker;

    Button addConstraintButton;
    Button saveButton;

    //Appointment Field

    public String name;
    public LocalDate date;
    public LocalTime startingTime;
    public TimeSlot timeSlot;
    public int duration;   // seconds
    public int involvedPeople;
    public LatLng coords;
    public Boolean isRecurrent;

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
        locationField = findViewById(R.id.locationField);
        durationTimePicker = findViewById(R.id.durationTimePicker);
        numberInvolvedPeopleField = findViewById(R.id.numberInvolvedPeopleField);
        datePicker = findViewById(R.id.datePicker);
        addConstraintButton = findViewById(R.id.addConstraintButton);
        saveButton = findViewById(R.id.saveButton);

        //position of the appointment in the list
        position = getIntent().getIntExtra("position",-1);

        durationTimePicker.setIs24HourView(true);
        durationTimePicker.setHour(1);
        durationTimePicker.setMinute(0);


        //setting for the editing
        if(position != -1){
            //appointment to be modified
            Appointment appointment = AppointmentManager.GetInstance().GetAppointment(position);

            appointmentNameField.setText(appointment.toString());
            //TODO: locationField.setText(appointment.);
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

    public void OnAddConstraintClick(View view){
        final Intent intent = new Intent(this, AddConstraintOnAppointmentActivity.class);
        startActivityForResult(intent,3);
    }

    public void OnSaveCliCk(View view){
        name = appointmentNameField.getText().toString();
        date = new LocalDate(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
        duration = durationTimePicker.getHour()*3600 + durationTimePicker.getMinute()*60 ;
        involvedPeople= Integer.parseInt(numberInvolvedPeopleField.getText().toString());
        isRecurrent = isRecurrentCheckBox.isChecked();
        // TODO: coords = locationField.getCoordinate();

        //need for know if the appointment is new or is been editing
        //int position = getIntent().getIntExtra("position",-1);

        //creation of a new appointment
        if(position == -1) {
            //the new appointment created
            Appointment appointment;

            // TODO: CONTROLLARE CHE L'UTENTE ABBIA INSERITO TUTTI I CAMPI.

            //Check if the appointment has a starting time or a time slot
            if (checkBoxStartingTime.isChecked()) {
                appointment = new Appointment(name, date, startingTime,null, duration, coords,involvedPeople,isRecurrent);
            } else {
                appointment = new Appointment(name, date,null, timeSlot, duration, coords,involvedPeople,isRecurrent);
            }

            AppointmentManager.GetInstance().AddAppointment(appointment);
            //verifying that the appointment is added to the appointment list
            Log.e("addAppointmentToTheList", String.valueOf(AppointmentManager.GetInstance().GetAppointmentList().size()));
            super.onBackPressed();
        }
        //editing of an exsisting appointment
        else{
            //appointment that must be modified
            Appointment appointment = AppointmentManager.GetInstance().GetAppointment(position);
            if (checkBoxStartingTime.isChecked()) {
                if (startingTime == null)
                    startingTime = appointment.getStartingTime();
                appointment.EditAppointment(name, date, startingTime,null, duration, coords,involvedPeople,isRecurrent);
            } else {
                if(timeSlot == null)
                    timeSlot = appointment.getTimeSlot();
                appointment.EditAppointment(name, date,null,timeSlot, duration, coords,involvedPeople,isRecurrent);
            }
        }
        super.onBackPressed();
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
            if(resultCode == Activity.RESULT_OK){

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
