package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;

import org.joda.time.LocalTime;

public class SettingStartingTimeActivity extends AppCompatActivity {

    TimePicker timePiker;
    int position;
    //appointment to be modified
    Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_starting_time);

        timePiker = findViewById(R.id.timePicker);

        position = getIntent().getIntExtra("position",-1);

        // Check if it is an editing of an exsisting appointment
        if(position != -1){
            appointment = AppointmentManager.GetInstance().GetAppointment(position);
            if(appointment.getStartingTime() != null) {
                timePiker.setHour(appointment.getStartingTime().getHourOfDay());
                timePiker.setMinute(appointment.getStartingTime().getMinuteOfHour());
            }
        }
    }

    // method to send back parameter chosen by the user to the AppointmentCreationActivity
    public void onSaveButtonPressed(View view){
        //create the object to pass back to the previous view
        LocalTime startingTime = new LocalTime(timePiker.getHour(),timePiker.getMinute());
        //create the intent to be sent back
        Intent returnIntent = new Intent();
        returnIntent.putExtra("startingTime",startingTime);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

}
