package com.example.gabdampar.travlendar;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by gabdampar on 30/11/2017.
 */

public class NetworkManager {

    //for Volley API
    private static RequestQueue requestQueue;

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

}