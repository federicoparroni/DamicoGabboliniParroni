package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.gabdampar.travlendar.Model.ScheduledAppointment;
import com.example.gabdampar.travlendar.R;
import java.util.ArrayList;

public class ScheduledAppointmentsListViewAdapter extends ArrayAdapter<ScheduledAppointment>{

    public ScheduledAppointmentsListViewAdapter(Context context, ArrayList<ScheduledAppointment> appointmentList) {
        super(context, 0, appointmentList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_scheduled_appointment_list, null);
        }
        ScheduledAppointment appointment = getItem(position);
        TextView appointmentNameField = convertView.findViewById(R.id.appointmentNameField);
        TextView timeField = convertView.findViewById(R.id.timeField);
        TextView reachedBy = convertView.findViewById(R.id.reachedBy);
        reachedBy.setText("Reached by: " + appointment.travelMeanToUse.toString());
        appointmentNameField.setText(appointment.originalAppointment.toString());
        timeField.setText(appointment.ETA.toString("HH:mm" ) + " - " + appointment.endingTime().toString("HH:mm"));
        return convertView;
    }
}

