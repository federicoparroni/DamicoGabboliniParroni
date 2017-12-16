package com.example.gabdampar.travlendar.Controller.ViewController.CustomPreferences;


import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import com.example.gabdampar.travlendar.R;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by gabbo on 14/12/2017.
 */

public class LocationPreference extends DialogPreference {
    private String locationId;
    private LatLng locationCoordinates;
    private int mDialogLayoutResId = R.layout.pref_dialog_location;

    public LocationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.pref_dialog_location);
    }
}

