package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.R;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.lang.reflect.Array;

public class SettingTimeSlotActivity extends AppCompatActivity {

    TimePicker startingTimeSlotTimeField;
    TimePicker endingTimeSlotTimeField;

    int position;
    //appointment to be modified
    Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time_slot);
        startingTimeSlotTimeField = findViewById(R.id.startingTimeSlotTimeField);
        endingTimeSlotTimeField= findViewById(R.id.endingTimeSlotTimeField);

        startingTimeSlotTimeField.setIs24HourView(true);
        endingTimeSlotTimeField.setIs24HourView(true);

        position = getIntent().getIntExtra("position",-1);

        if(position != -1){
            appointment = AppointmentManager.GetInstance().GetAppointment(position);
            if(appointment.getTimeSlot() != null) {
                startingTimeSlotTimeField.setHour(appointment.getTimeSlot().startingTime.getHourOfDay());
                startingTimeSlotTimeField.setMinute(appointment.getTimeSlot().startingTime.getMinuteOfHour());

                endingTimeSlotTimeField.setHour(appointment.getTimeSlot().endingTime.getHourOfDay());
                endingTimeSlotTimeField.setMinute(appointment.getTimeSlot().endingTime.getMinuteOfHour());
            }
        }
    }

    // method to send back parameter chosen by the user to the AppointmentCreationActivity
    @Override
    public void onBackPressed(){

        //create the object to pass back to the previous view
        LocalTime startingTimeSlotTime = new LocalTime( startingTimeSlotTimeField.getHour(),startingTimeSlotTimeField.getMinute());
        LocalTime endingTimeSlotTime = new LocalTime(endingTimeSlotTimeField.getHour(),endingTimeSlotTimeField.getMinute());
        Bundle extras = new Bundle();
        extras.putSerializable("startingTime",startingTimeSlotTime);
        extras.putSerializable("endingTime",endingTimeSlotTime);

        //create the intent to be sent back
        Intent returnIntent = new Intent();
        returnIntent.putExtra("timeSlot",extras);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();

    }
}
