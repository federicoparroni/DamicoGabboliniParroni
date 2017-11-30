package com.example.gabdampar.travlendar.Controller;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gabdampar on 16/11/2017.
 */

public class IdentityManager implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static String baseUrl = "http://travlendar.000webhostapp.com/travlendar/public";

    private String email = "";
    private String password = "";
    private String token = "";
    private String refreshToken = "";

    private static String client_id = "travlendar";
    private static String client_secret = "travlendar";

    private static Timer t = new Timer();

    private static IdentityManager instance;

    public static IdentityManager GetInstance() {
        if(instance == null) {
            instance = new IdentityManager();
        }
        return instance;
    }

    public void Login(String email, String password, Response.Listener onResponse, Response.ErrorListener onError)  {
        //if(!IdentityManager.instance.email.isEmpty() && !IdentityManager.instance.password.isEmpty()) {
            IdentityManager.instance.email = email;
            IdentityManager.instance.password = password;

            IdentityManager.TokenRequest(onResponse, onError);
        //} else {
            Log.d("EmptyCredentials", "Email o password missing");
        //}
    }

    public void SetUserSession(String email, String password, final String token, int tokenDuration) {
        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        IdentityManager.instance.token = token;
        IdentityManager.instance.RefreshToken(tokenDuration);
    }

    private void RefreshToken(int tokenDuration) {
        t.cancel();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // TO-DO: call refresh token API
                IdentityManager.TokenRequest(IdentityManager.instance, IdentityManager.instance);
            }
        },tokenDuration * 1000 - 10);
    }

    private static void TokenRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String json = String.format("{\"grant_type\": \"password\", \"username\": \"%s\", \"password\": \"%s\", \"client_id\": \"travlendar\", \"client_secret\": \"travlendar\" }", IdentityManager.instance.email, IdentityManager.instance.password);
        try {
            JsonObjectRequest loginRequest = new JsonObjectRequest(baseUrl.concat("/token"),
                    new JSONObject(json), listener, errorListener);
            loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // adding the Login request to the queue of the request
            NetworkManager.GetQueue().add(loginRequest);
        } catch (JSONException e) {
            Log.d("JSONError","Error in creating json object request");
        }
    }




    @Override
    public void onResponse(JSONObject response) {
        try {
            IdentityManager.instance.token = response.getString("access_token");
        } catch (JSONException e) {
            Log.d("JSONError","Error in getting token from response");
        }
        try {
            IdentityManager.instance.RefreshToken(response.getInt("expires_in"));
        } catch (JSONException e) {
            Log.d("JSONError","Error in getting token expiration from response");
        }
    }

    private static void Register(String email, String password){
        //chiama database e aggiunge agli utenti l'utente identificato dall'email inserita e dalla password

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("RequestError", error.toString());
    }

}