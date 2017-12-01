package com.example.gabdampar.travlendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.example.gabdampar.travlendar.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    // Instantiate the RequestQueue.
    //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //called when the user click on the login button
    public void LoginAttempt(View view) {

        findViewById(R.id.progressBarLogin).setVisibility(View.VISIBLE);

        EditText email = (EditText) findViewById(R.id.emailText);
        EditText password = (EditText) findViewById(R.id.passwordText);

        // se il login va a buon fine devo passare alla main view
        final Intent intent = new Intent(this, MainPageActivity.class);
        IdentityManager.Login(email.getText().toString(),password.getText().toString(),getApplicationContext(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // ON RESPONSE
                        Log.d("DIOBELLO1", response.toString());
                        getApplicationContext().startActivity(intent);

                        findViewById(R.id.progressBarLogin).setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // ON ERROR RESPONSE
                        Log.d("DIOBELLO2", error.toString());

                        findViewById(R.id.progressBarLogin).setVisibility(View.INVISIBLE);

                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
    }

    // called when the user click on the registration Button
    public void RegistrationAttempt(View view){

        EditText email = (EditText) findViewById(R.id.emailText);
        EditText password = (EditText) findViewById(R.id.passwordText);

        //IdentityManager.Register(email.getText().toString(),password.getText().toString());
    }

    //these two methods must be implemented due to the implements of the interface
    public void onResponse(JSONObject response) {
        // ON RESPONSE
    }

    public void onErrorResponse(VolleyError error) {
        // ON ERROR RESPONSE

    }

}
