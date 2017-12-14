package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;

import java.util.ArrayList;

/**
 * Created by Edoardo D'Amico on 03/12/2017.
 */

public class AppointmentsListViewAdapter extends ArrayAdapter<Appointment> {


    public AppointmentsListViewAdapter(Context context, int resource, ArrayList<Appointment> appointmentList) {
        super(context, resource, appointmentList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_appointment_list, null);
        }

        Appointment appointment = AppointmentManager.GetInstance().apptList.get(position);

        TextView appointmentNameField = convertView.findViewById(R.id.appointmentNameField);
        TextView dateField = convertView.findViewById(R.id.dateField);
        TextView timeField = convertView.findViewById(R.id.timeField);

        appointmentNameField.setText(appointment.toString());

        dateField.setText(appointment.getDate().toString());

        /** deterministic appointment */
        if(appointment.getTimeSlot() == null) {
            timeField.setText(appointment.getStartingTime().toString("HH:mm"));
        }
        /** non deterministic appointment */
        else {
            timeField.setText(appointment.getTimeSlot().toString());
        }

        return convertView;
    }

}