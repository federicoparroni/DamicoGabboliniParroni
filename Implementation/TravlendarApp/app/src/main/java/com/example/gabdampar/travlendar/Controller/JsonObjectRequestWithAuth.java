package com.example.gabdampar.travlendar.Controller;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gabdampar on 06/12/2017.
 */

public class JsonObjectRequestWithAuth {

    public static JsonObjectRequest Create(String url, JSONObject json, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError, final String token) {

        JsonObjectRequest request = new JsonObjectRequest(url, json, onSuccess, onError) {
            @Override   // add header for authentication
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String headerValue = String.format("Bearer %s", token);
                headers.put("Authorization", headerValue);
                return headers;
            }
        };

        return request;
    }

}
