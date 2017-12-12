package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnAppointment;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;

import java.util.ArrayList;

public class AddConstraintOnAppointmentActivity extends AppCompatActivity {

    public ArrayList<ConstraintOnAppointment> constraints;

    int position;
    Appointment appointment;

    //view elements
    CheckBox trainCheckBox;
    CheckBox carCheckBox;
    CheckBox busCheckBox;
    CheckBox carSharingCheckBox;
    CheckBox bikeSharingCheckBox;
    CheckBox tramCheckBox;
    CheckBox taxiCheckBox;
    CheckBox walkingCheckBox;
    CheckBox undergroundCheckBox;
    CheckBox bikeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_constraint_on_appointment);

        position = getIntent().getIntExtra("position",-1);
        appointment = AppointmentManager.GetInstance().GetAppointment(position);

        trainCheckBox = findViewById(R.id.trainCheckBox);
        carCheckBox = findViewById(R.id.carCheckBox);
        busCheckBox= findViewById(R.id.busCheckBox);
        carSharingCheckBox= findViewById(R.id.carSharingCheckBox);
        bikeSharingCheckBox= findViewById(R.id.bikeSharingCheckBox);
        tramCheckBox = findViewById(R.id.tramCheckBox);
        taxiCheckBox = findViewById(R.id.taxiCheckBox);
        walkingCheckBox = findViewById(R.id.walkingCheckBox);
        undergroundCheckBox= findViewById(R.id.undergroundCheckBox);
        bikeCheckBox = findViewById(R.id.bikeCheckBox);

    }

    public void createConstraint (CheckBox checkBox, TravelMeanEnum mean){
        if(checkBox.isChecked()){
            ConstraintOnAppointment constr = new ConstraintOnAppointment(mean,0);
            constraints.add(constr);
        }
    }



    @Override
    public void onBackPressed(){

        //create the constraints arraylist to pass back
        /*createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();
        createConstraint();*/

        //Create the Intent to pass back
        Intent returnIntent = new Intent();
        returnIntent.putExtra("constraints",constraints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        super.onBackPressed();
    }
}
