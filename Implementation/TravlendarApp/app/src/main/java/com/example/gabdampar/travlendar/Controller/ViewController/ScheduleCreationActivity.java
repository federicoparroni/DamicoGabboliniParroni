package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.Scheduler;
import com.example.gabdampar.travlendar.Controller.WeatherForecastAPIWrapper;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ScheduleCreationActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, TimePicker.OnTimeChangedListener, RadioGroup.OnCheckedChangeListener, OnMapReadyCallback, PlaceSelectionListener, WeatherForecastAPIWrapper.WeatherForecastAPIWrapperCallBack{

    // view controls
    CalendarView calendar;
    TimePicker timePickerWakeUp;
    SupportMapFragment startingLocationMap;
    PlaceAutocompleteFragment autocompleteFragment;
    SegmentedGroup group;
    ListView constraintsListView;
    FloatingActionButton fab;

    ConstraintScheduleListViewAdapter constraintsAdapter;

    // schedule data
    protected Scheduler scheduler = new Scheduler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_creation);

        // reference ui control and set listeners
        /** schedule date calendar view */
        calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(this);
        /** wake up time picker */
        timePickerWakeUp = findViewById(R.id.wakeUpTimePicker);
        timePickerWakeUp.setOnTimeChangedListener(this);
        /** optimizing criteria segmented control */
        group = findViewById(R.id.segmentedGroup);
        group.setTintColor( ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null) );
        group.setOnCheckedChangeListener(this);
        /** map */
        startingLocationMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.starting_location_map);
        startingLocationMap.getMapAsync(this);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        /** constraints list view */
        constraintsListView = findViewById(R.id.constraintsListView);
        constraintsAdapter = new ConstraintScheduleListViewAdapter(getApplicationContext(), R.id.constraintsListView, scheduler.constraints);
        constraintsListView.setAdapter(constraintsAdapter);
        /** start schedule computation */
        fab = findViewById(R.id.fab);

        // LoadUserDefaults();
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
        LocalDate date = new LocalDate(year, month+1, dayOfMonth);
        scheduler.appts = AppointmentManager.GetInstance().GetAppointmentsByDate(date);

        if(scheduler.appts.size() == 0) {
            // no appointments for the specified date
            Snackbar.make(findViewById(R.id.schedule_creation_inner_scrollview), "You have not saved any appointment for the chosen date", Snackbar.LENGTH_LONG).show();
        } else {
            // check for weather data
            //SharedPreferences settings = getSharedPreferences("ApiData", 0);
            //String savedDateString = settings.getString("weatherApiDate","");
            //if(savedDateString.isEmpty()) {
                /** calls to weather because no data found */
                WeatherForecastAPIWrapper.getInstance().getWeather(this, date, scheduler.appts.get(0).coords);

            //} else {
//                LocalDate savedDate = LocalDate.parse(savedDateString);
//                LocalDate now = LocalDate.now();
//                if(savedDate.getYear() != now.getYear() || savedDate.getDayOfYear() != now.getDayOfYear()) {
//                    /** calls to weather because data found are of a different day */
//                    WeatherForecastAPIWrapper.getInstance().getWeather(this, date, scheduler.appts.get(0).coords);
//
//                } else {
//                    // load data from file
//                    Log.e("weather", "loading weather from file");
//                    this.setSchedulerWeather( TimeWeatherList.readFromFile(getApplicationContext(), "timeWeatherList.ser"));
//                }
//            }



        }
    }

    /**
     * On weather results callback
     * @param weatherConditionList
     */
    @Override
    public void onWeatherResults(TimeWeatherList weatherConditionList) {
        // save to preferences the received data
        /*SharedPreferences settings = getSharedPreferences("ApiData", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("weatherApiDate", new LocalDate( calendar.getDate() ).toString());

        weatherConditionList.saveToFile(getApplicationContext(), "timeWeatherList.ser");

        editor.commit();
        */

        Log.e("weather", "weather callback");

        this.setSchedulerWeather(weatherConditionList);
    }

    private void setSchedulerWeather(TimeWeatherList weatherConditionList) {
        Log.e("weather", "set weather to scheduler");
        scheduler.weatherConditions = weatherConditionList;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hours, int minutes) {
        scheduler.scheduleStartingTime = new LocalTime(hours, minutes);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.optCriteria1:
                scheduler.criteria = OptCriteria.OPTIMIZE_TIME;
                break;
            case R.id.optCriteria2:
                scheduler.criteria = OptCriteria.OPTIMIZE_COST;
                break;
            case R.id.optCriteria3:
                scheduler.criteria = OptCriteria.OPTIMIZE_CARBON;
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String time = pref.getString("wake_up_time", "");
        if(!time.isEmpty()) scheduler.scheduleStartingTime = new LocalTime( time );

        // TODO get starting location from preferences
        scheduler.startingLocation = new LatLng(45.4809352, 9.233779);
        onSelectedDayChange(calendar, 2017, 10, 18);
        scheduler.criteria = OptCriteria.OPTIMIZE_COST;

        // enable fab click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scheduler.isConsistent()) {
                    // start schedule computation

                    scheduler.ComputeSchedule();
                    //Intent intent = new Intent();
                    //intent.putExtra("scheduler", scheduler);
                } else {
                    Snackbar.make(view, "Missing fields", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        scheduler.startingLocation = place.getLatLng();
    }

    @Override
    public void onError(Status status) {
        Snackbar.make(findViewById(R.id.schedule_creation_inner_scrollview), "Error retrieving place information", Snackbar.LENGTH_LONG).show();
        Log.e("AutocompleteFragmError", status.getStatusMessage());
    }

    /** called when user click on ADD CONSTRAINT */
    public void OnAddConstraintClick(View view) {
        // show dialog to create new constraint
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleCreationActivity.this);
        builder.setView(R.layout.dialog_constraint_creation).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // create and add new constraint
                ConstraintOnSchedule c = new ConstraintOnSchedule();
                //c.mean = ;
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
        // update list view adapter
        constraintsAdapter.notifyDataSetChanged();
    }

}