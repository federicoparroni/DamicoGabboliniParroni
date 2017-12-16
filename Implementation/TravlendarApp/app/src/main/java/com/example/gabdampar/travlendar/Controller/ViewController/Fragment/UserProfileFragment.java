package com.example.gabdampar.travlendar.Controller.ViewController.Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gabdampar.travlendar.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserProfileFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{ //, OnMapReadyCallback {

    /**retrieve params from outside this class doing this:
     * SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
     * boolean b=sharedPref.getBoolean("has_car", false);
     *
     * to recover starting time do like this:
     * String time= sharedPref.getString("wake_up_time","");
     * LocalTime l = new LocalTime(time);
     */

    PlaceAutocompleteFragment autocompleteFragment;
    MapFragment appointmentMap;
    GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("starting_location", "50,50").apply();

        //String s=PreferenceManager.getDefaultSharedPreferences(getContext()).getString("starting_location", "30,30");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        /*autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_settings);
        appointmentMap =  (MapFragment) getFragmentManager().findFragmentById(R.id.appointment_map_settings);
        appointmentMap.getMapAsync(this);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();
                String placeName = place.getName().toString();
                LatLng latLng = place.getLatLng();
                map.addMarker(new MarkerOptions().position(latLng).title(placeName));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                LatLng coords = place.getLatLng();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        */
        return view;
    }

    /**
     * https://developer.android.com/guide/topics/ui/settings.html
     * not working. if needed to make it work follow the suggestions of the link above
     * in particular, in the activity should be registered the events implementing onPause and onResume
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //disable the zoom option
        map.getUiSettings().setZoomGesturesEnabled(false);
        //disable the scroll gesture in the minimap
        map.getUiSettings().setScrollGesturesEnabled(false);
        //disable the google map button
        map.getUiSettings().setMapToolbarEnabled(false);
    }*/
}
