package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.here.android.mpa.common.GeoCoordinate;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


public class AppointmentCreationActivity extends AppCompatActivity {

    //Appointment creation activity view elements
    CheckBox checkBoxStartingTime;
    CheckBox checkBoxTimeSlot;
    CheckBox isRecurrent;

    EditText appointmentNameField;
    EditText locationField;

    EditText durationHours;
    EditText durationMinutes;
    TimePicker durationTimePicker;

    EditText numberInvolvedPeople;

    DatePicker datePicker;

    Button addConstraintButton;
    Button saveButton;

    //New Appointment Field

    public String name;
    public LocalDate date;
    public LocalTime startingTime;
    public TimeSlot timeSlot;
    public int duration;   // seconds
    public int involvedPeople;
    public GeoCoordinate coords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        checkBoxStartingTime = findViewById(R.id.checkBoxStartingTime);
        checkBoxTimeSlot = findViewById(R.id.checkBoxTimeSlot);
        isRecurrent = findViewById(R.id.isRecurrent);
        appointmentNameField = findViewById(R.id.appointmentNameField);
        locationField = findViewById(R.id.locationField);
        durationTimePicker = findViewById(R.id.durationTimePicker);
        numberInvolvedPeople = findViewById(R.id.numberInvolvedPeople);
        datePicker = findViewById(R.id.datePicker);
        addConstraintButton = findViewById(R.id.addConstraintButton);
        saveButton = findViewById(R.id.saveButton);

        durationTimePicker.setIs24HourView(true);
        durationTimePicker.setHour(1);
        durationTimePicker.setMinute(0);

    }

    // when the user check the startingtime checkbox he will be sent to SettingStartingTimeActivity to set the time
    public void onCheckboxOfStartingTimeClicked(View view) {
        //the if below need for not open the page on dischek the checkbox
        if (checkBoxStartingTime.isChecked()) {
            //XOR of timeslot and startingtime
            checkBoxTimeSlot.setClickable(false);
            final Intent intent = new Intent(this, SettingStartingTimeActivity.class);
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
        //must
        date = new LocalDate(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth()) ;
        duration = durationTimePicker.getHour()*3600 + durationTimePicker.getHour()*60 ;
        // TODO: coords = locationField.getCoordinate();

        //the new appointment created
        Appointment appointment;

        //Check if the appointment has a starting time or a time slot
        if(checkBoxStartingTime.isChecked()){
            appointment = new Appointment(name,date,startingTime,duration,coords);
        }
        else{
            appointment = new Appointment(name,date,timeSlot,duration,coords);
        }

        AppointmentManager.GetInstance().AddAppointment(appointment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //onActivityResult
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //assignment to the parameter of the new appointment the result
                startingTime = (LocalTime) data.getExtras().getSerializable("startingTime");
                timeSlot = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                LocalTime startingTimeSlotTime = (LocalTime)extras.getSerializable("startingTime");
                LocalTime endingTimeSlotTime = (LocalTime)extras.getSerializable("endingTime");
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
