package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.gabdampar.travlendar.Controller.ScheduleManager;
import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.Model.Schedule;
import com.example.gabdampar.travlendar.R;

import java.util.ArrayList;

/**
 * Created by federico on 13/12/17.
 */

public class ConstraintScheduleListViewAdapter extends ArrayAdapter<ConstraintOnSchedule> {

    public ConstraintScheduleListViewAdapter(Context context, int resource, ArrayList<ConstraintOnSchedule> constraintsList) {
        super(context, resource, constraintsList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_constraint_schedule_list, null);
        }

        Schedule s = ScheduleManager.GetInstance().getSchedule(position);

        // set row controls


        return convertView;
    }

}