package com.example.gabdampar.travlendar.Controller.JsonSerializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;

/**
 * Created by federico on 23/12/17.
 */

public class LocalDateSerializer implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate>
{
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public LocalDate deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException
    {
        final String dateAsString = je.getAsString();
        if (dateAsString.length() == 0)
        {
            return null;
        }
        else
        {
            return LocalDate.parse(dateAsString);
        }
    }

    @Override
    public JsonElement serialize(final LocalDate src, final Type typeOfSrc, final JsonSerializationContext context)
    {
        String retVal;
        if (src == null)
        {
            retVal = "";
        }
        else
        {
            retVal = src.toString(DATE_PATTERN);
        }
        return new JsonPrimitive(retVal);
    }
}