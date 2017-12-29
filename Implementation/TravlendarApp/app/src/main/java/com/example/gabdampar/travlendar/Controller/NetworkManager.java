/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/*
// Singleton class that offers a centralized request queue
*/
public class NetworkManager {

    // global application request queue
    private static RequestQueue requestQueue;


    // CONTEXT MUST BE SET AT FIRST
    public static void SetContext(Context c) {
        requestQueue = Volley.newRequestQueue(c);
    }

    public static synchronized RequestQueue GetQueue()
    {
        if (requestQueue == null)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return requestQueue;
    }

    /** Set json request custom timeout (ms) */
    public static JsonObjectRequest SetRequestTimeout(JsonObjectRequest request, int timeout) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return request;
    }

    /** Checks whether an internet connection is available and the device is connected */
    public static Boolean isOnline(Context c ) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null)
            connected = false;
        else
            connected = true;
        return connected;
    }
}
