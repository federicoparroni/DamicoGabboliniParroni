package com.example.gabdampar.travlendar.Controller.ViewController;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.Scheduler;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class ScheduleCreationActivity extends AppCompatActivity {

    // view controls


    // schedule data
    protected Schedule s;
    LocalDate scheduleDate;
    LocalTime wakeUpTime;
    LatLng startingLocation;
    ArrayList<ConstraintOnSchedule> constraints = new ArrayList<>();
    OptCriteria criteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_creation);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //Set the Icon on the action bar for the creation of a new schedule
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_new_schedule:
                // get appointments filtered by date
                ArrayList<Appointment> appts = AppointmentManager.GetInstance().GetAppointmentsByDate(scheduleDate);

                // compute schedule
                Scheduler scheduler = new Scheduler(wakeUpTime, startingLocation, appts, constraints, criteria);
                Schedule s = scheduler.ComputeSchedule();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


}