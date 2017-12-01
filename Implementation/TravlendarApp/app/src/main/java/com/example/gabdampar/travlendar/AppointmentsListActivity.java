/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class AppointmentsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_list);
    }

    //Method activation on the click of the new Button
    public void AppointmentInsertionRequest(View view){
        Intent intent = new Intent(this, AppointmentCreationActivity.class);
        getApplicationContext().startActivity(intent);
    }
}
