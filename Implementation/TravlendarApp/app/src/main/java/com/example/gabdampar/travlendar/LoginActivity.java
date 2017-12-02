/**
 * Created by gabdampar on 29/11/2017.
 */

package com.example.gabdampar.travlendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText emailField;
    EditText passwordField;
    ProgressBar bar;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the Network manager
        NetworkManager.SetContext(getApplicationContext());

        setContentView(R.layout.activity_login);
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.passwordText);
        bar = findViewById(R.id.progressBarLogin);

    }

    //called when the user click on the login button
    public void LoginAttempt(View view) {
        bar.setVisibility(View.VISIBLE);

        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        IdentityManager.GetInstance().Login(email, password, this, this);
    }


    //these two methods must be implemented due to the implements of the interface
    public void onResponse(JSONObject response) {
        // ON RESPONSE
        bar.setVisibility(View.INVISIBLE);

        try {
            String token = response.getString("access_token");
            try {
                int token_expir = response.getInt("expires_in");
                //IdentityManager.GetInstance().SetUserSession(email, password, token, token_expir);

                // if login was ok, show main view
                final Intent intent = new Intent(this, MainPageActivity.class);
                getApplicationContext().startActivity(intent);

            } catch (JSONException e) {
                Log.d("JSONError","Error in getting token expiration from response");
            }
        } catch (JSONException e) {
            Log.d("JSONError","Error in getting token from response");
        }

    }

    // called when the user click on the registration Button
    public void RegistrationAttempt(View view){
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        //IdentityManager.Register(email.getText().toString(),password.getText().toString());
    }

    public void onErrorResponse(VolleyError error) {
        // ON ERROR RESPONSE
        bar.setVisibility(View.INVISIBLE);

        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
        toast.show();
    }

}