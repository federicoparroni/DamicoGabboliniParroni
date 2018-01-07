package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.NetworkManager;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Controller.Scheduler;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.OptCriteria;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.Model.TimeSlot;
import com.example.gabdampar.travlendar.Model.Weather;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class ScheduleCreationActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener,
        TimePicker.OnTimeChangedListener, RadioGroup.OnCheckedChangeListener,
        OnMapReadyCallback, PlaceSelectionListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    // view controls
    CalendarView calendar;

    TextView txtApptNumberForSelectedDay;
    TimePicker timePickerWakeUp;
    SupportMapFragment startingLocationFragment;
    PlaceAutocompleteFragment autocompleteFragment;
    GoogleMap startingLocationMap;
    SegmentedGroup group;
    ListView constraintsListView;
    FloatingActionButton fab;

    ConstraintScheduleListViewAdapter constraintsAdapter;

    // thread handler
    Handler schedulerHandler = new Handler();

    // schedule data
    protected Scheduler scheduler = new Scheduler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_creation);

        // reference ui control and set listeners
        /** schedule date calendar view */
        calendar = findViewById(R.id.calendarView);
        calendar.setMinDate(DateTime.now().getMillis() - 1000);
        calendar.setOnDateChangeListener(this);
        /** txtApptNumberForSelectedDay */
        txtApptNumberForSelectedDay = findViewById(R.id.txtApptNumberForSelectedDay);
        /** wake up time picker */
        timePickerWakeUp = findViewById(R.id.wakeUpTimePicker);
        timePickerWakeUp.setOnTimeChangedListener(this);
        /** optimizing criteria segmented control */
        group = findViewById(R.id.segmentedGroup);
        group.setTintColor( ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null) );
        group.setOnCheckedChangeListener(this);
        /** map */
        startingLocationFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.starting_location_map);
        startingLocationFragment.getMapAsync(this);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        /** constraints list view */
        constraintsListView = findViewById(R.id.constraintsListView);
        constraintsAdapter = new ConstraintScheduleListViewAdapter(getApplicationContext(), R.id.constraintsListView, scheduler.constraints);
        constraintsListView.setAdapter(constraintsAdapter);
        constraintsListView.setOnItemLongClickListener(this);
        FixListviewInScrollview(constraintsListView);
        /** start schedule computation */
        fab = findViewById(R.id.fab);
        fab.setClickable(false);

        scheduler.criteria = OptCriteria.OPTIMIZE_TIME;

        // LoadUserDefaults();
        final DateTime now = DateTime.now();
        onSelectedDayChange(calendar, now.getYear(), now.getMonthOfYear()-1, now.getDayOfMonth());

    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
        LocalDate date = new LocalDate(year, month+1, dayOfMonth);
        scheduler.appts = AppointmentManager.GetInstance().GetAppointmentsByDate(date);

        updateAppointmentNumberForSelectedDate(scheduler.appts.size());

        fab.setClickable( scheduler.appts.size() > 0 );
    }


    /**
     * Update the UI showing in a textview the number of appointments in the select date
     * @param numberOfAppts: number of appointments for the selected date
     */
    private void updateAppointmentNumberForSelectedDate(int numberOfAppts) {
        // no appointments for the specified date
        txtApptNumberForSelectedDay.setText( String.format("(%d appointments)", numberOfAppts) );

        if(numberOfAppts == 0) {
            txtApptNumberForSelectedDay.setTextColor(Color.RED);
            Snackbar.make(findViewById(R.id.schedule_creation_inner_scrollview), "You have not saved any appointment for the chosen date", Snackbar.LENGTH_LONG).show();
        } else {
            txtApptNumberForSelectedDay.setTextColor(Color.DKGRAY);
        }
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
        startingLocationMap = googleMap;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String time = pref.getString("wake_up_time", "");
        if(!time.isEmpty()) scheduler.scheduleStartingTime = new LocalTime( time );

        scheduler.criteria=OptCriteria.OPTIMIZE_TIME;

        // enable fab click listener
        fab.setOnClickListener(this);
    }

    /**
     * FAB button click listener: start scheduler computation
     * @param view: FAB button
     */
    @Override
    public void onClick(final View view) {
        if(NetworkManager.isOnline(getApplicationContext())) {
            if(scheduler.isConsistent()) {

                //creation of the waiting view through an alert dialogue
                final AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleCreationActivity.this);
                // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
                LayoutInflater inflater = LayoutInflater.from(builder.getContext());
                View inflatedView = inflater.inflate(R.layout.waiting_view, null);
                builder.setView(inflatedView)
                        .setTitle("Schedule Creation")
                        .setCancelable(false);
                final AlertDialog alert = builder.create();
                alert.show();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // start schedule computation
                        scheduler.ComputeSchedule(getApplicationContext(), new Scheduler.ScheduleCallbackListener() {
                            @Override
                            public void ScheduleCallback(final Schedule schedule) {
                                alert.dismiss();
                                if(schedule == null) {
                                    Snackbar.make(view, "Unfeasible schedule", Snackbar.LENGTH_LONG).show();
                                } else {
                                    ScheduleManager.GetInstance().schedulesList.add(schedule);
                                    finish();
                                }
                            }
                        });
                    }
                };
                if (runnable != null) {
                    schedulerHandler.post(runnable);
                }

            } else {
                Snackbar.make(view, "Missing fields", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(view, "Internet connection appears to be offline", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPlaceSelected(Place place) {
        startingLocationMap.clear();
        String placeName = place.getName().toString();
        LatLng coords = place.getLatLng();
        startingLocationMap.addMarker(new MarkerOptions().position(coords).title(placeName));
        startingLocationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords,15));
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
        final ConstraintOnSchedule editingConstraint = new ConstraintOnSchedule();
        editingConstraint.maxDistance = 0;

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
                    onConstraintViewTravelMeanRadioButtonClicked(compoundButton, checked, radioButtonsMean, editingConstraint);
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

        final TimePicker tpConstraintTimeSlotStart = dialoglayout.findViewById(R.id.tp_constraint_timeslot_start);
        final TimePicker tpConstraintTimeSlotEnd = dialoglayout.findViewById(R.id.tp_constraint_timeslot_end);
        tpConstraintTimeSlotStart.setIs24HourView(true);
        tpConstraintTimeSlotEnd.setIs24HourView(true);

        final CheckBox ckConstraintTimeSlot = dialoglayout.findViewById(R.id.ck_constraint_timeslot);

        final TextView txtSelectAllWeather = dialoglayout.findViewById(R.id.txtSelectAll);
        txtSelectAllWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** inverts the checked state of the weather checkboxes */
                boolean areAllChecked = true;
                for (CheckBox ck : checksButtonsWeather) {
                    if(!ck.isChecked()) areAllChecked = false;
                    break;
                }
                for (CheckBox ck : checksButtonsWeather) {
                    ck.setChecked( !areAllChecked );
                }

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleCreationActivity.this);
        builder.setView(dialoglayout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // set fields on constraint as set in the view
                editingConstraint.maxDistance = Float.parseFloat( txtMaxDistance.getText().toString() );
                // get checked weathers
                editingConstraint.weather.clear();
                for(CheckBox ck : checksButtonsWeather) {
                    if(ck.isChecked()) {
                        switch (ck.getId()) {
                            case R.id.ckSunny:
                                editingConstraint.weather.add( Weather.CLEAN );
                                break;
                            case R.id.ckCloudy:
                                editingConstraint.weather.add( Weather.CLOUDY );
                                break;
                            case R.id.ckRainy:
                                editingConstraint.weather.add( Weather.RAINY );
                                break;
                            case R.id.ckSnowy:
                                editingConstraint.weather.add( Weather.SNOWY );
                                break;
                            case R.id.ckFoggy:
                                editingConstraint.weather.add( Weather.FOGGY );
                                break;
                            case R.id.ckWindy:
                                editingConstraint.weather.add( Weather.WINDY );
                                break;
                        }
                    }
                }
                if(ckConstraintTimeSlot.isChecked()) {
                    LocalTime timeSlotStart = new LocalTime( tpConstraintTimeSlotStart.getHour(), tpConstraintTimeSlotStart.getMinute());
                    LocalTime timeSlotEnd = new LocalTime( tpConstraintTimeSlotEnd.getHour(), tpConstraintTimeSlotEnd.getMinute());
                    editingConstraint.timeSlot = new TimeSlot(timeSlotStart, timeSlotEnd);
                }

                if(editingConstraint.isConsistent()) {
                    scheduler.constraints.add(editingConstraint);
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

            //set all others radios to unchecked
            for (RadioButton radio : radios) {
                if(radio.getId() != compoundButton.getId()) radio.setChecked(false);
            }
        }
    }

    /**
     * Called when a long click on a constraint list view row item is performed
     * @param adapterView
     * @param view
     * @param index
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int index, long id) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Constraint deletion")
                .setMessage("Are you sure you want to delete this constraint?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scheduler.constraints.remove(index);
                        // update list view adapter
                        constraintsAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
        return true;
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