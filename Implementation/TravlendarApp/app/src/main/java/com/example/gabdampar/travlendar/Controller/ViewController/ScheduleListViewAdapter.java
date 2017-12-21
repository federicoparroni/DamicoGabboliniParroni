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
import com.example.gabdampar.travlendar.Controller.AppointmentManager;
import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Controller.ViewController.Fragment.AppointmentsListFragment;
import com.example.gabdampar.travlendar.Model.Appointment;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.R;
import org.joda.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by gabbo on 21/12/2017.
 */

public class ScheduleListViewAdapter extends ArrayAdapter<Schedule>{
    //private ArrayList<Schedule> filteredData = null;
    //private ScheduleListViewAdapter.ItemFilter mFilter = new ScheduleListViewAdapter.ItemFilter();

    public ScheduleListViewAdapter(Context context, ArrayList<Schedule> scheduleList) {
        super(context, 0, scheduleList);
        //this.filteredData = scheduleList;
    }

    // getter and setter
    /*public int getCount() {
        return filteredData.size();
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
            filteredData = (ArrayList<Schedule>) results.values;
            notifyDataSetChanged();
        }

    }
*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Schedule schedule = getItem(position);
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
