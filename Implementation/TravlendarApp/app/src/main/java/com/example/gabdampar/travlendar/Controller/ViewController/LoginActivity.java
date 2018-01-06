/**
 * Created by gabdampar on 29/11/2017.
 */

package com.example.gabdampar.travlendar.Controller.ViewController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.Controller.NetworkManager;
import com.example.gabdampar.travlendar.R;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener, DialogInterface.OnClickListener {

    EditText emailField;
    EditText passwordField;
    ProgressBar bar;
    Button loginBtn;
    Button registerBtn;
    CheckBox ckRemember;
    TextView txtForgotPassword;
    PopupImageView imgOK;
    PopupImageView imgError;

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
        ckRemember = findViewById(R.id.ck_remember_me);
        txtForgotPassword = findViewById(R.id.txt_recover_password);
        imgOK = findViewById(R.id.img_ok);
        imgError = findViewById(R.id.img_error);

        imgOK.setScaleX(0); imgOK.setScaleY(0);
        imgError.setScaleX(0); imgError.setScaleY(0);

        LoadUserPreference();
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

    @Override
    protected void onNewIntent(Intent intent) {
        // if the user has been logged out and redirected to login acivity, clear user session
        if(intent.getIntExtra("calling-activity", 0) > 0) {
            ClearUserPreference(true);
        }
        super.onNewIntent(intent);
    }

    //called when the user click on the login button
    public void onLoginClick(View view) {
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
                DateTime token_expir_date = DateTime.now().plusSeconds(token_expir);
                SaveUserPreference(token, token_expir_date);
                StartMainActivity();

            } catch (JSONException e) {
                Log.d("JSONError","Error in getting token expiration from response");
            }
        } catch (JSONException e) {
            Log.d("JSONError","Error in getting token from response");
        }
    }

    void StartMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        LoginActivity.this.startActivity(intent);
    }

    // called when the user click on the registration Button
    public void onRegistrationClick(View view){
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            // confirm password showing an alert dialog
            Pattern pattern = Pattern.compile(getString(R.string.email_regex), Pattern.CASE_INSENSITIVE);
            if(pattern.matcher(email).matches()) {
                ShowConfirmationPassword();
            } else {
                Snackbar.make(view, "Email is not valid!", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(view, "Email or password not valid", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onErrorResponse(VolleyError error) {
        // ON ERROR RESPONSE
        bar.setVisibility(View.INVISIBLE);
        imgError.ShowPopup();
        if(error.networkResponse != null) {
            if(error.networkResponse.statusCode == 400 || error.networkResponse.statusCode == 401) {
                Snackbar.make(findViewById(R.id.progressBarLogin), "Invalid username or password", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(findViewById(R.id.progressBarLogin), error.getMessage(), Snackbar.LENGTH_LONG).show();
            Log.e("ServerError", error.getMessage());
        }
    }

    void ShowConfirmationPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate and set the layout for the dialog, parent is null because its going in the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Confirm password:")
                .setView(inflater.inflate(R.layout.dialog_registration_confirm, null))
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
                    imgOK.ShowPopup();
                    Snackbar.make(findViewById(R.id.progressBarLogin), "Registration successfully done", Snackbar.LENGTH_LONG).show();
                }
            }, this);
        } else {
            Snackbar.make(findViewById(R.id.progressBarLogin), "Passwords are not matching", Snackbar.LENGTH_LONG).show();
        }
    }

    void LoadUserPreference() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        ckRemember.setChecked( settings.getBoolean("remember", false) );
        if(ckRemember.isChecked()) {
            emailField.setText(settings.getString("email", ""));
            passwordField.setText(settings.getString("password", ""));

            String token = settings.getString("token","");
            if(!token.isEmpty()) {
                //check if token has expired
                DateTime token_expir = new DateTime(settings.getLong("token_exp", Long.MAX_VALUE));
                if (token_expir.isAfter(DateTime.now().toInstant())) {
                    // token has not expired and still valid
                    long tokenRemainingDuration = token_expir.getMillis() - DateTime.now().getMillis();
                    IdentityManager.GetInstance().SetUserSession(emailField.getText().toString(), passwordField.getText().toString(), token, tokenRemainingDuration);
                    StartMainActivity();
                }
            }
        }
    }
    void SaveUserPreference(String token, DateTime tokenExipirationDate) {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("remember", ckRemember.isChecked());
        if(ckRemember.isChecked()) {
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putString("token", token);
            editor.putLong("token_exp", tokenExipirationDate.getMillis());
        }
        editor.commit();
    }
    void ClearUserPreference(boolean onlyToken) {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        if(onlyToken) {
            editor.remove("token");
            editor.remove("token_exp");
        } else {
            editor.clear();
        }
        editor.commit();
    }

    // disable buttons and show progress bar while a request is pending
    public void SetViewState(Boolean active) {
        loginBtn.setActivated(active);
        registerBtn.setActivated(active);
        bar.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
    }

}