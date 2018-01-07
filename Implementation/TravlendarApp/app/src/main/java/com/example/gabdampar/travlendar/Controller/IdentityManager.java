/**
 * Created by gabdampar on 26/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gabdampar.travlendar.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
// IdentityManager offers methods to register, authenticate via bearer token and refresh it after expiration
**/
public class IdentityManager implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static String baseUrl = "http://travlendar.000webhostapp.com/travlendar/public";

    public User user;
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

    /** get token from api by using:
     * 1. username and password (cached in this instance) in order to auth the user
     * 2. client_credentials (to allow registration)
    **/
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


    /**==========================================================================================
                                                LOGIN
    ==========================================================================================**/

    /** Obtain a token by providing email and password */
    public void Login(String email, String password, Response.Listener onResponse, Response.ErrorListener onError)  {
        // TO-DO: should check wether online or not

        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        IdentityManager.TokenRequest(AuthMethod.PASSWORD, onResponse, onError);

    }

    /** Auto-refresh token for the provided credentials after expiration */
    public void SetUserSession(String email, String password, final String token, long tokenDuration) {
        IdentityManager.instance.email = email;
        IdentityManager.instance.password = password;

        IdentityManager.instance.token = token;
        IdentityManager.instance.RefreshToken(tokenDuration);
    }

    /** Set a scheduled task to refresh a token when expired */
    private void RefreshToken(long tokenDuration) {
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // TO-DO: call refresh token API
                IdentityManager.instance.token = "";
                IdentityManager.TokenRequest(AuthMethod.PASSWORD, IdentityManager.instance, IdentityManager.instance);
            }
        },tokenDuration - 10);
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


    /**==========================================================================================
                                           REGISTRATION
    ==========================================================================================**/

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
        JSONObject json;
        try {
            json = new JSONObject(String.format("{ \"email\": \"%s\", \"password\": \"%s\" }", IdentityManager.instance.email, IdentityManager.instance.password));
            JsonObjectRequest registerRequest = JsonObjectRequestWithAuth.Create(baseUrl.concat("/api/register"), json,
                            IdentityManager.instance.onRegistrationResponse,
                            IdentityManager.instance.onRegistrationError,
                            token);

            NetworkManager.SetRequestTimeout(registerRequest, 15000);
            NetworkManager.GetQueue().add(registerRequest);
        } catch (JSONException e) {
            Log.e("JSONError","Error in creating json object request");
            Log.e("JSONError", e.toString());
        }
    }


    /**==========================================================================================
                                            PROFILE
     ==========================================================================================**/
    public static void GetUserProfile(final UserProfileListener callback) {
        JSONObject json;
        try {
            json = new JSONObject(String.format("{ \"email\": \"%s\", \"password\": \"%s\" }", IdentityManager.instance.email, IdentityManager.instance.password));

            JsonObjectRequest profileRequest = JsonObjectRequestWithAuth.Create(baseUrl.concat("/api/user/profile"), json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                User user = User.ParseUser(response.getJSONObject("data"));
                                if(user != null) {
                                    IdentityManager.GetInstance().user = user;
                                    callback.UserProfileCallback(true, user);
                                } else {
                                    callback.UserProfileCallback(false, null);
                                }
                            } catch (JSONException e) {
                                Log.e("JSONError", "Cannot parse user profile json");
                                Log.e("JSONError", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR", "Cannot retrieve user profile");
                            Log.e("ERROR", error.getMessage());
                        }
                    }, IdentityManager.instance.token);

            NetworkManager.SetRequestTimeout(profileRequest, 15000);
            NetworkManager.GetQueue().add(profileRequest);
        } catch (JSONException e) {
            Log.e("JSONError","Error in creating json object request");
            Log.e("JSONError", e.toString());
        }
    }

    public static void Logout() {
        IdentityManager.instance.email = "";
        IdentityManager.instance.password = "";
        IdentityManager.instance.token = "";
        IdentityManager.instance.refreshToken = "";
        if(IdentityManager.instance.t != null) {
            t.cancel();
        }
        IdentityManager.instance.onRegistrationResponse = null;
        IdentityManager.instance.onRegistrationError = null;
    }


    public interface UserProfileListener {
        void UserProfileCallback(boolean success, User user);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("RequestError", error.toString());
    }

}