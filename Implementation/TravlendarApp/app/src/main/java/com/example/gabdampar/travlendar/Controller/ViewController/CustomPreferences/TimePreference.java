package com.example.gabdampar.travlendar.Controller.ViewController.CustomPreferences;

import android.preference.DialogPreference;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import org.joda.time.LocalTime;

/**
 * Created by gabbo on 14/12/2017.
 */

public class TimePreference extends DialogPreference {
    private int lastHour;
    private int lastMinute;
    private TimePicker picker;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(@NonNull View v) {
        super.onBindDialogView(v);

        picker.setHour(lastHour);
        picker.setMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getHour();
            lastMinute = picker.getMinute();

            LocalTime localTime = new LocalTime(lastHour, lastMinute);
            String time = localTime.toString();

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        LocalTime time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = LocalTime.parse(getPersistedString("08:00:00.000"));
            }
            else {
                time = LocalTime.parse(getPersistedString(defaultValue.toString()));
            }
        } else {
            time = LocalTime.parse(defaultValue.toString());
        }

        lastHour = time.getHourOfDay();
        lastMinute = time.getMinuteOfHour();
    }
}
