package com.example.gabdampar.travlendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.gabdampar.travlendar.R;

public class AppointmentCreationActivity extends AppCompatActivity {

    CheckBox checkBoxStartingTime;
    CheckBox checkBoxTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        checkBoxStartingTime = findViewById(R.id.checkBoxStartingTime);
        checkBoxTimeSlot = findViewById(R.id.checkBoxTimeSlot);
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
}
