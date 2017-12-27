package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.ConstraintOnAppointment;
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

    Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_constraint_on_appointment);

        position = getIntent().getIntExtra("position",-1);

        constraints = new ArrayList<ConstraintOnAppointment>();

        setButton = findViewById(R.id.setButton);

        trainCheckBox = findViewById(R.id.trainCheckBox);
        carCheckBox = findViewById(R.id.carCheckBox);
        busCheckBox= findViewById(R.id.busCheckBox);
        carSharingCheckBox= findViewById(R.id.carSharingCheckBox);
        bikeSharingCheckBox= findViewById(R.id.bikeSharingCheckBox);
        tramCheckBox = findViewById(R.id.tramCheckBox);
        walkingCheckBox = findViewById(R.id.walkingCheckBox);
        undergroundCheckBox= findViewById(R.id.undergroundCheckBox);
        bikeCheckBox = findViewById(R.id.bikeCheckBox);

        //check if it is an editing
        if(position >= 0) {
            appointment = AppointmentManager.GetInstance().GetAppointment(position);
            ArrayList<ConstraintOnAppointment> constraintOnAppointments = appointment.getConstraints();

            //scan the arraylist and set the check on the checkbox
            for (ConstraintOnAppointment c: constraintOnAppointments){
                setCheckBox(c);
            }

        }

        // onClick of the swtButton
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create the constraints arraylist to pass back
                createConstraint(trainCheckBox,TravelMeanEnum.TRAIN);
                createConstraint(carCheckBox,TravelMeanEnum.CAR);
                createConstraint(busCheckBox,TravelMeanEnum.BUS);
                createConstraint(tramCheckBox,TravelMeanEnum.TRAM);
                //createConstraint(carSharingCheckBox,TravelMeanEnum.CAR_SHARING);
                //createConstraint(bikeSharingCheckBox,TravelMeanEnum.BIKE_SHARING);
                //createConstraint(taxiCheckBox,TravelMeanEnum.TAXI);
                createConstraint(walkingCheckBox,TravelMeanEnum.WALK);
                createConstraint(undergroundCheckBox,TravelMeanEnum.METRO);
                createConstraint(bikeCheckBox,TravelMeanEnum.BIKE);

                //Create the Intent to pass back
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("constraints",constraints);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });
    }

    public void createConstraint (CheckBox checkBox, TravelMeanEnum mean){
        if(checkBox.isChecked()){
            ConstraintOnAppointment constr = new ConstraintOnAppointment(mean,0);
            constraints.add(constr);
        }
    }

    public void setCheckBox(ConstraintOnAppointment constraint){
        TravelMeanEnum mean = constraint.mean;
        switch(mean){
            case TRAIN:
                trainCheckBox.setChecked(true);
                break;
            case CAR:
                carCheckBox.setChecked(true);
                break;
            case BUS:
                busCheckBox.setChecked(true);
                break;
            case TRAM:
                tramCheckBox.setChecked(true);
                break;
            /*case CAR_SHARING:
                carSharingCheckBox.setChecked(true);
                break;
            case BIKE_SHARING:
                bikeSharingCheckBox.setChecked(true);
                break;
            case TAXI:
                taxiCheckBox.setChecked(true);
                break;*/
            case WALK:
                walkingCheckBox.setChecked(true);
                break;
            case METRO:
                undergroundCheckBox.setChecked(true);
                break;
            case BIKE:
                bikeCheckBox.setChecked(true);
                break;
        }
    }


}
