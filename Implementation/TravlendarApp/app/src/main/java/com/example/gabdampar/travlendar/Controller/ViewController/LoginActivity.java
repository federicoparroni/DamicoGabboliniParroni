/**
 * Created by gabdampar on 29/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.MappingServiceAPIWrapper;
import com.example.gabdampar.travlendar.Controller.NetworkManager;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;
import com.example.gabdampar.travlendar.R;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener, DialogInterface.OnClickListener {

    EditText emailField;
    EditText passwordField;
    ProgressBar bar;
    Button loginBtn;
    Button registerBtn;

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
        loginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_button);


        //try to use HERE APIs, to move away

        /*MapEngine mapEngine = MapEngine.getInstance();
        ApplicationContext appContext = new ApplicationContext(this.getApplicationContext());
        Log.d("checkpoint", "sto per inizializzare");

        mapEngine.init(appContext, new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                Log.d("checkpoint", "sono dentro al metodo");
                //MappingServiceAPIWrapper.getInstance().prova();
                if (error == OnEngineInitListener.Error.NONE) {
                    MappingServiceAPIWrapper.getInstance().prova();
                } else {
                    Log.e("InitializationError", error.getDetails());
                }
            }
        });*/
    }

    //called when the user click on the login button
    public void LoginAttempt(View view) {
        SetViewState(false);

        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        IdentityManager.GetInstance().Login(email, password, this, this);
    }


    //these two methods must be implemented due to the implements of the interface
    // called when the LOGIN response is returned
    public void onResponse(JSONObject response) {
        SetViewState(true);

        try {
            String token = response.getString("access_token");
            try {
                int token_expir = response.getInt("expires_in");
                IdentityManager.GetInstance().SetUserSession(email, password, token, token_expir);

                // if login was ok, show main view
                final Intent intent = new Intent(this, MainActivity.class);
                LoginActivity.this.startActivity(intent);

            } catch (JSONException e) {
                Log.d("JSONError","Error in getting token expiration from response");
            }
        } catch (JSONException e) {
            Log.d("JSONError","Error in getting token from response");
        }

    }

    // called when the user click on the registration Button
    public void RegistrationAttempt(View view){
        /*
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            // confirm password showing an alert dialog
            Pattern pattern = Pattern.compile(getString(R.string.email_regex), Pattern.CASE_INSENSITIVE);
            if(pattern.matcher(email).matches()) {
                ShowConfirmationPassword();
            } else {
                Toast.makeText(this, "Email is not valid!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Email or password not valid", Toast.LENGTH_LONG).show();
        }*/
        ArrayList<TravelMeanEnum> l=new ArrayList<TravelMeanEnum>();
        l.add(TravelMeanEnum.BUS);
        MappingServiceAPIWrapper.getInstance().getTravelOptionData(l,"Viale delle Rimembranze di Lambrate", "Duomo di Milano", new DateTime(2017,12, 7, 12, 10, 30));
    }

    public void onErrorResponse(VolleyError error) {
        // ON ERROR RESPONSE
        bar.setVisibility(View.INVISIBLE);

        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
        toast.show();
    }

    void ShowConfirmationPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Confirm password:")
                .setView(inflater.inflate(R.layout.registration_confirm_dialog, null))
            // Set up the view
                .setPositiveButton("OK", this)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // called when user click on the password confirmation dialog OK button
    @Override
    public void onClick(DialogInterface dialog, int which) {

        EditText passwordConfirmField = ((AlertDialog) dialog).findViewById(R.id.confirm_password);
        String passwordConfirm = passwordConfirmField.getText().toString();

        if(password.equals(passwordConfirm)) {
            IdentityManager.GetInstance().Register(email, password, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Toast.makeText(LoginActivity.this, "Registration successfully done", Toast.LENGTH_SHORT).show();
                }
            }, this);
        } else {
            Toast.makeText(LoginActivity.this, "Passwords not missing", Toast.LENGTH_SHORT).show();
        }
    }

    // disable buttons and show progress bar while a request is pending
    public void SetViewState(Boolean active) {
        loginBtn.setActivated(active);
        registerBtn.setActivated(active);
        bar.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
    }

}