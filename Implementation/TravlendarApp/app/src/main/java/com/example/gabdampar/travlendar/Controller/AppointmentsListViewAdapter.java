package com.example.gabdampar.travlendar.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.AppointmentsListFragment;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.R;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * Created by Edoardo D'Amico on 03/12/2017.
 */

public class AppointmentsListViewAdapter extends ArrayAdapter<Appointment> implements Filterable {
    private ArrayList<Appointment> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();

    public AppointmentsListViewAdapter(Context context, int resource, ArrayList<Appointment> appointmentList) {
        super(context, resource, appointmentList);
        this.filteredData = appointmentList;
    }

    // getter and setter
    public int getCount() {
        return filteredData==null? 0 : filteredData.size();
    }

    public Appointment getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public void removeFromFilteredData(int position) {
        this.filteredData.remove(position);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            LocalDate filterDate = AppointmentsListFragment.appointmentsDate;

            FilterResults results = new FilterResults();

            final ArrayList<Appointment> list = AppointmentManager.GetInstance().getAppointmentList();

            int count = list.size();
            final ArrayList<Appointment> filteredList = new ArrayList<Appointment>(count);

            Appointment a;

            for (int i = 0; i < count; i++) {
                a = list.get(i);
                if (a.getDate().equals(filterDate) || filterDate == null) {
                    filteredList.add(a);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Appointment>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_appointment_list, null);
        }


        Appointment appointment = filteredData.get(position);

        TextView appointmentNameField = convertView.findViewById(R.id.appointmentNameField);
        TextView dateField = convertView.findViewById(R.id.dateField);
        TextView timeField = convertView.findViewById(R.id.timeField);

        appointmentNameField.setText(appointment.toString());

        dateField.setText(appointment.getDate().toString());

        /** deterministic appointment */
        if (appointment.getTimeSlot() == null) {
            timeField.setText(appointment.getStartingTime().toString("HH:mm"));
        }
        /** non deterministic appointment */
        else {
            timeField.setText(appointment.getTimeSlot().toString());
        }

        if (appointment.getDate() == AppointmentsListFragment.appointmentsDate || AppointmentsListFragment.appointmentsDate == null) {

        }
        return convertView;

    }
}

