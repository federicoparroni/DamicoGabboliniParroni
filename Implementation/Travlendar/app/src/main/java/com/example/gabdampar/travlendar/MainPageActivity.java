package com.example.gabdampar.travlendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gabdampar.travlendar.R;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
    }

    //called when appointments Button is clicked
    public void AppointmentsListRequest(View view){
        Intent intent = new Intent(this, AppointmentsListActivity.class);
        startActivity(intent);
    }
}
