package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;

public class AppointmentCreationActivity extends AppCompatActivity {

    //Appointment creation activity view elements
    CheckBox checkBoxStartingTime;
    CheckBox checkBoxTimeSlot;
    CheckBox isRecurrent;

    EditText appointmentNameField;
    EditText locationField;
    EditText durationField;
    EditText numberInvolvedPeople;

    DatePicker datePicker;

    Button addConstraintButton;
    Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        checkBoxStartingTime = findViewById(R.id.checkBoxStartingTime);
        checkBoxTimeSlot = findViewById(R.id.checkBoxTimeSlot);
        isRecurrent = findViewById(R.id.isRecurrent);
        appointmentNameField = findViewById(R.id.appointmentNameField);
        locationField = findViewById(R.id.locationField);
        durationField = findViewById(R.id.durationField);
        numberInvolvedPeople = findViewById(R.id.numberInvolvedPeople);
        datePicker = findViewById(R.id.datePicker);
        addConstraintButton = findViewById(R.id.addConstraintButton);
        saveButton = findViewById(R.id.saveButton);
    }

    // when the user check the startingtime checkbox he will be sent to SettingStartingTimeActivity to set the time
    public void onCheckboxOfStartingTimeClicked(View view) {
        //the if below need for not open the page on dischek the checkbox
        if (checkBoxStartingTime.isChecked()) {
            //XOR of timeslot and startingtime
            checkBoxTimeSlot.setClickable(false);
            final Intent intent = new Intent(this, SettingStartingTimeActivity.class);
            getApplicationContext().startActivity(intent);
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
            getApplicationContext().startActivity(intent);
        }else{
            checkBoxStartingTime.setClickable(true);
        }
    }

    public void OnAddConstraintClick(View view){
        final Intent intent = new Intent(this, AddConstraintOnAppointmentActivity.class);
        getApplicationContext().startActivity(intent);
    }

    public void OnSaveCliCk(View view){
        //Check if the appointment has a starting time or a time slot
        if(checkBoxStartingTime.isChecked()){
            // DEVO CONTROLLARE COSA CAMBIA TRA MAX E MIN DATA
            //Appointment = new Appointment(appointmentNameField.getText(),datePicker.getMaxDate(), )
        }
        else{

        }



         //LocalTime startingTime, int duration, GeoCoordinate coord

    }
}
