package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.R;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * Created by gabbo on 21/12/2017.
 */

public class ScheduleListViewAdapter extends ArrayAdapter<Schedule> implements Filterable {

    private ArrayList<Schedule> filteredData = new ArrayList<>();
    private ScheduleListViewAdapter.ItemFilter mFilter = new ScheduleListViewAdapter.ItemFilter();

    /**
     * used in filtering
     */
    public boolean showCurrentSchedules=true;

    public ScheduleListViewAdapter(Context context, ArrayList<Schedule> scheduleList) {
        super(context, 0, scheduleList);
        this.filteredData = scheduleList;
    }

    // getter and setter
    public int getCount() {
        return filteredData == null ? 0 : filteredData.size();
    }

    public Schedule getItem(int position) {
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
            FilterResults results = new FilterResults();

            final ArrayList<Schedule> filteredList = new ArrayList<>(ScheduleManager.GetInstance().schedulesList.size());

            for(Schedule s:ScheduleManager.GetInstance().schedulesList)
                if(showCurrentSchedules)
                    if(s.getDate().equals(LocalDate.now()) || s.getDate().isAfter(LocalDate.now()))
                        filteredList.add(s);
                else
                    if(s.getDate().isBefore(LocalDate.now()))
                        filteredList.add(s);

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Schedule>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Schedule schedule = filteredData.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_schedule_list, parent, false);
        }
        TextView scheduleNameField = (TextView) convertView.findViewById(R.id.scheduleNameField);
        TextView optimizationCriteriaField = (TextView) convertView.findViewById(R.id.optimizationCriteriaField);
        TextView runningField = (TextView) convertView.findViewById(R.id.runningField);
        // Populate the data into the template view using the data object
        scheduleNameField.setText("Schedule of " + schedule.getDate().toString());
        optimizationCriteriaField.setText(schedule.criteria.toString());
        if(schedule.equals(ScheduleManager.GetInstance().runningSchedule)) {
            runningField.setText("Running");
            runningField.setTextColor(Color.GRAY);
        }
        else
            runningField.setText("");
        // Return the completed view to render on screen
        return convertView;
    }
}
