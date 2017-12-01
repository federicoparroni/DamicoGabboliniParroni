package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by gabdampar on 30/11/2017.
 */

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

    // Checks whether an internet connection is available and the device is connected
    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}