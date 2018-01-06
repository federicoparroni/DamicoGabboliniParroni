package com.example.gabdampar.travlendar.Model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gabdampar on 06/12/2017.
 */

public class User {

    public int id;
    public String email;
    public boolean hasCar;
    public boolean hasBike;
    public boolean hasPass;

    public static User ParseUser(JSONObject json) {
        User u = new User();
        try {
            u.id = json.getInt("id");
            u.email = json.getString("email");
            u.hasCar = json.getString("hasCar").equals("0") ? false : true;
            u.hasBike = json.getString("hasBike").equals("0") ? false : true;
            u.hasPass = json.getString("hasPass").equals("0") ? false : true;
            return u;
        } catch (JSONException e) {
            Log.e("JSONError", "Cannot parse user");
        }
        return null;
    }

}