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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Controller.Scheduler;
import com.example.gabdampar.travlendar.Controller.WeatherForecastAPIWrapper;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.TimeWeatherList;
import com.example.gabdampar.travlendar.Model.Weather;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ScheduleCreationActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener,
        TimePicker.OnTimeChangedListener, RadioGroup.OnCheckedChangeListener,
        OnMapReadyCallback, PlaceSelectionListener,
        WeatherForecastAPIWrapper.WeatherForecastAPIWrapperCallBack {

    // view controls
    CalendarView calendar;
    TimePicker timePickerWakeUp;
    SupportMapFragment startingLocationMap;
    PlaceAutocompleteFragment autocompleteFragment;
    SegmentedGroup group;
    ListView constraintsListView;
    FloatingActionButton fab;
    ProgressBar bar;

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
        /** progress bar */
        bar = findViewById(R.id.progressBarScheduleCreation);
        bar.setVisibility(View.INVISIBLE);
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
        FixListviewInScrollview(constraintsListView);
        /** start schedule computation */
        fab = findViewById(R.id.fab);
        fab.setClickable(false);
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

        fab.setClickable(true);
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
        onSelectedDayChange(calendar, 2017, 11, 26);
        scheduler.criteria = OptCriteria.OPTIMIZE_TIME;

        // enable fab click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scheduler.isConsistent()) {
                    // start schedule computation

                    SetViewState(false);

                    scheduler.ComputeSchedule(new Scheduler.ScheduleCallbackListener() {
                        @Override
                        public void ScheduleCallback(final Schedule schedule) {
                            ScheduleManager.GetInstance().schedulesList.add(schedule);

                            //only the main thread can touch the view
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SetViewState(true);
                                    Toast.makeText(getApplicationContext(),"Computed schedule of date " + schedule.getDate().toString(), Toast.LENGTH_LONG);
                                }
                            });

                            //to remove, just trying
                            ScheduleManager.GetInstance().runningSchedule = schedule;
                        }
                    });
                    //Intent intent = new Intent();
                    //intent.putExtra("scheduler", scheduler);
                } else {
                    Snackbar.make(view, "Missing fields", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void SetViewState(Boolean active) {
        fab.setActivated(active);
        bar.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
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
    public void OnAddConstraintClick(final View view) {
        // create and add new constraint
        final ConstraintOnSchedule newConstraint = new ConstraintOnSchedule();
        newConstraint.maxDistance = 0;

        // build dialog to create new constraint
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_constraint_creation, null);

        final ArrayList<RadioButton> radioButtonsMean = new ArrayList<>();
        final EditText txtMaxDistance = dialoglayout.findViewById(R.id.txt_maxDistance);

        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbWalk) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbBike) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbCar) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbBus) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbTram) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbTrain) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbMetro) );
        radioButtonsMean.add( (RadioButton) dialoglayout.findViewById(R.id.rbBikeSharing) );
        for(RadioButton rb : radioButtonsMean) {
            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    onConstraintViewTravelMeanRadioButtonClicked(compoundButton, checked, radioButtonsMean, newConstraint);
                }
            });
        }

        final ArrayList<CheckBox> checksButtonsWeather = new ArrayList<>();
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckSunny) );
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckCloudy) );
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckRainy) );
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckSnowy) );
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckFoggy) );
        checksButtonsWeather.add( (CheckBox) dialoglayout.findViewById(R.id.ckWindy) );
        for(CheckBox ck : checksButtonsWeather) {
            ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    newConstraint.weather.clear();

                    for(CheckBox ck : checksButtonsWeather) {

                        if(ck.isChecked()) {
                            switch (ck.getId()) {
                                case R.id.ckSunny:
                                    newConstraint.weather.add( Weather.CLEAN );
                                    break;
                                case R.id.ckCloudy:
                                    newConstraint.weather.add( Weather.CLOUDY );
                                    break;
                                case R.id.ckRainy:
                                    newConstraint.weather.add( Weather.RAINY );
                                    break;
                                case R.id.ckSnowy:
                                    newConstraint.weather.add( Weather.SNOWY );
                                    break;
                                case R.id.ckFoggy:
                                    newConstraint.weather.add( Weather.FOGGY );
                                    break;
                                case R.id.ckWindy:
                                    newConstraint.weather.add( Weather.WINDY );
                                    break;
                            }
                        }

                    }
                }
            });
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleCreationActivity.this);
        builder.setView(dialoglayout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // set fields on constraint as set in the view
                newConstraint.maxDistance = Float.parseFloat( txtMaxDistance.getText().toString() );

                if(newConstraint.isConsistent()) {
                    scheduler.constraints.add(newConstraint);
                    // update list view adapter
                    constraintsAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(view, "Missing fields on the constraint", Snackbar.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }


    /**
     * Called when a radiobutton in constraint dialog change its checked state
     * @param compoundButton: radiobutton which its state has changed
     * @param checked: state (checked or not)
     * @param radios: all others radiobuttons on the dialog
     * @param newConstraint: new constraint in creation
     */
    void onConstraintViewTravelMeanRadioButtonClicked(CompoundButton compoundButton, boolean checked, ArrayList<RadioButton> radios, ConstraintOnSchedule newConstraint) {
        if(checked) {
            switch (compoundButton.getId()) {
                case R.id.rbWalk:
                    newConstraint.mean = TravelMeanEnum.WALK;
                    break;
                case R.id.rbCar:
                    newConstraint.mean = TravelMeanEnum.CAR;
                    break;
                case R.id.rbBike:
                    newConstraint.mean = TravelMeanEnum.BIKE;
                    break;
                case R.id.rbTrain:
                    newConstraint.mean = TravelMeanEnum.TRAIN;
                    break;
                case R.id.rbTram:
                    newConstraint.mean = TravelMeanEnum.TRAM;
                    break;
                case R.id.rbMetro:
                    newConstraint.mean = TravelMeanEnum.METRO;
                    break;
                case R.id.rbBus:
                    newConstraint.mean = TravelMeanEnum.BUS;
                    break;
                /*case R.id.rbBikeSharing:
                    newConstraint.mean = TravelMeanEnum.WALK;
                    break;*/
            }

            //set all others to !checked
            for (RadioButton radio : radios) {
                if(radio.getId() != compoundButton.getId()) radio.setChecked(false);
            }
        }
    }


    /**
     * Fix for scrollview with listview as child
     */
    void FixListviewInScrollview(ListView lv) {
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

}