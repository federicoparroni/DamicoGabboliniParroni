package com.example.gabdampar.travlendar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Edoardo D'Amico on 16/11/2017.
 */

public class IdentityManager  {

    private Intent intent;

    private String email;
    private String password;
    private static String baseUrl = "http://travlendar.000webhostapp.com/travlendar/public";


    public static void Login(String email, String password,Context context, Response.Listener onResponse, Response.ErrorListener onError)  {
        RequestQueue queue = Volley.newRequestQueue(context); //NON SO SE è MEGLIO INSTANZIARLA NELLA LOGIN ACTIVITY E PASSARLA COME PARAMETRO FORSE SI

        try {
            JsonObjectRequest LoginRequest = new JsonObjectRequest(baseUrl.concat("/token"),
                    new JSONObject(String.format("{\"grant_type\": \"password\", \"username\": \"%s\", \"password\": \"%s\", \"client_id\": \"travlendar\", \"client_secret\": \"travlendar\" }",email,password)),
                    onResponse,onError);
            // adding the Login request to the queue of the request
            queue.add(LoginRequest);

        }catch(JSONException e){
            Log.d("error","error in Json Fomrat");
        }
    }


    public static void Register(String email, String password){
        //chiama database e aggiunge agli utenti l'utente identificato dall'email inserita e dalla password
    }

}
