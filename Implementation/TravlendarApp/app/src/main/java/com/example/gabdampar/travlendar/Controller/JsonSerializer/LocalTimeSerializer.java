package com.example.gabdampar.travlendar.Controller.JsonSerializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;

import java.lang.reflect.Type;

/**
 * Created by federico on 23/12/17.
 */

public class LocalTimeSerializer implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime>
{

    private static final String TIME_PATTERN = "HH:mm:SS";

    @Override
    public LocalTime deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException
    {
        final String timeAsString = je.getAsString();
        if (timeAsString.length() == 0)
        {
            return null;
        }
        else
        {
            return LocalTime.parse(timeAsString);
        }
    }

    @Override
    public JsonElement serialize(final LocalTime src, final Type typeOfSrc,
                                 final JsonSerializationContext context)
    {
        String retVal;
        if (src == null)
        {
            retVal = "";
        }
        else
        {
            retVal = src.toString(TIME_PATTERN);
        }
        return new JsonPrimitive(retVal);
    }

}