package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.IdentityManager;
import com.example.gabdampar.travlendar.R;

public class UserProfileFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{ //, OnMapReadyCallback {

    /**retrieve params from outside this class doing this:
     * SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
     * boolean b=sharedPref.getBoolean("has_car", false);
     *
     * to recover starting time do like this:
     * String time= sharedPref.getString("wake_up_time","");
     * LocalTime l = new LocalTime(time);
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** init preference view in order to handle preferences from xml */
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        TextView txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserEmail.setText(IdentityManager.GetInstance().user.email);

        return view;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals("has_bike"))
            IdentityManager.GetInstance().user.hasBike = sharedPreferences.getBoolean(s, false);
        else if(s.equals("has_car"))
            IdentityManager.GetInstance().user.hasCar = sharedPreferences.getBoolean(s, false);
        else if(s.equals("has_pass"))
            IdentityManager.GetInstance().user.hasPass = sharedPreferences.getBoolean(s, false);
        else
            Log.e("InvalidPref", "Invalid user preference changedd");
    }

}