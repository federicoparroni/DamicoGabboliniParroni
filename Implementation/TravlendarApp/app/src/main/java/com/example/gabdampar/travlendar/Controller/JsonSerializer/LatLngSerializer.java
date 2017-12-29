package com.example.gabdampar.travlendar.Controller.JsonSerializer;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Created by federico on 23/12/17.
 */

public class LatLngSerializer implements JsonDeserializer<LatLng>, JsonSerializer<LatLng> {

    @Override
    public LatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final String latlngAsString = json.getAsString();
        if (latlngAsString.length() == 0)
        {
            return null;
        }
        else
        {
            String[] coords = latlngAsString.split(",");
            if(coords.length == 2) {
                return new LatLng(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
            } else {
                throw new JsonParseException("Error in parsing json LatLng");
            }
        }
    }

    @Override
    public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {
        String res;
        if (src == null)
        {
            res = "";
        }
        else
        {
            res = String.format(Locale.US,"%f,%f", src.latitude, src.longitude);
        }
        return new JsonPrimitive(res);
    }
}
