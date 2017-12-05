/**
 * Created by gabdampar on 26/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/*
// IdentityManager offers methods to register, authenticate via bearer token and refresh it after expiration
*/
public class IdentityManager implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static String baseUrl = "http://travlendar.000webhostapp.com/travlendar/public";

    private String email = "";
    private String password = "";
    private String token = "";
    private String refreshToken = "";

    public enum AuthMethod {
        PASSWORD,
        CLIENT_CREDENTIALS
    }

    private static String client_id = "travlendar";
    private static String client_secret = "travlendar";

    // used to schedule the token refreshing
    private static Timer t;

    private Response.Listener onRegistrationResponse = null;
    private Response.ErrorListener onRegistrationError = null;

    // singleton
    private static IdentityManager instance;
    public static IdentityManager GetInstance() {
        if(instance == null) {
            instance = new IdentityManager();
        }
        return instance;
    }


    private static void TokenRequest(AuthMethod authMode, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String json = "";
        switch (authMode) {
            case PASSWORD:
                json = String.format("{\"grant_type\": \"password\", \"username\": \"%s\", \"password\": \"%s\", \"client_id\": \"%s\", \"client_secret\": \"%s\" }", IdentityManager.instance.email, IdentityManager.instance.password, IdentityManager.client_id, IdentityManager.client_secret);
                break;
            case CLIENT_CREDENTIALS:
                json = String.format("{\"grant_type\": \"client_credentials\", \"client_id\": \"%s\", \"client_secret\": \"%s\" }", IdentityManager.client_id, IdentityManager.client_secret);
                break;
        }

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


    /*==========================================================================================
                                                LOGIN
    ========================================================================================= */

    // Request a token to authenticate for future requests
    public void Login(String email, String password, Response.Listener onResponse, Response.ErrorListener onError)  {
        // TO-DO: should check wether online or not

        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        IdentityManager.TokenRequest(AuthMethod.PASSWORD, onResponse, onError);

    }

    public void SetUserSession(String email, String password, final String token, int tokenDuration) {
        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        IdentityManager.instance.token = token;
        IdentityManager.instance.RefreshToken(tokenDuration);
    }

    private void RefreshToken(int tokenDuration) {
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // TO-DO: call refresh token API
                IdentityManager.instance.token = "";
                IdentityManager.TokenRequest(AuthMethod.PASSWORD, IdentityManager.instance, IdentityManager.instance);
            }
        },tokenDuration * 1000 - 10);
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


    /*==========================================================================================
                                           REGISTRATION
    ========================================================================================= */

    public static void Register(String email, String password, Response.Listener onResponse, Response.ErrorListener onError){
        // get token and then register
        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        // reference to call listener in registration callback
        IdentityManager.instance.onRegistrationResponse = onResponse;
        IdentityManager.instance.onRegistrationError = onError;

        TokenRequest(AuthMethod.CLIENT_CREDENTIALS, registrationListener, onError);
    }
    // token callback on registration request
    private static Response.Listener<JSONObject> registrationListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try {
                String token = response.getString("access_token");
                SendRegistrationRequest(token);
            } catch (JSONException e) {
                Log.e("JSONError","Error in getting token from response");
                Log.e("JSONError", e.toString());
            }

        }
    };

    private static void SendRegistrationRequest(final String token) {
        String json = String.format("{ \"email\": \"%s\", \"password\": \"%s\" }", IdentityManager.instance.email, IdentityManager.instance.password);
        try {
            JsonObjectRequest registerRequest = new JsonObjectRequest(baseUrl.concat("/api/register"), new JSONObject(json),
                            IdentityManager.instance.onRegistrationResponse, IdentityManager.instance.onRegistrationError) {

                @Override   // add header for authentication
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    String headerValue = String.format("Bearer %s", token);
                    headers.put("Authorization", headerValue);
                    return headers;
                }
            };

            registerRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // adding the Login request to the queue of the request
            NetworkManager.GetQueue().add(registerRequest);
        } catch (JSONException e) {
            Log.e("JSONError","Error in creating json object request");
            Log.e("JSONError", e.toString());
        }
    }





    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("RequestError", error.toString());
    }

}