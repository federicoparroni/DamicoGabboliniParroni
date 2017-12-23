package com.example.gabdampar.travlendar.Controller.ViewController;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabdampar.travlendar.Model.ConstraintOnSchedule;
import com.example.gabdampar.travlendar.R;

import java.util.ArrayList;

/**
 * Created by federico on 13/12/17.
 */

public class ConstraintScheduleListViewAdapter extends ArrayAdapter<ConstraintOnSchedule> {

    private ArrayList<ConstraintOnSchedule> constraintsList;

    public ConstraintScheduleListViewAdapter(Context context, int resource, ArrayList<ConstraintOnSchedule> constraintsList) {
        super(context, resource, constraintsList);
        this.constraintsList = constraintsList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_constraint_schedule_list, null);
        }

        ConstraintOnSchedule constraint = constraintsList.get(position);

        // set row controls
        ImageView meanIcon = convertView.findViewById(R.id.img_constraint_schedule);
        TextView textView = convertView.findViewById(R.id.txt_constraint_schedule);

        switch (constraint.mean) {
            case WALK:
                meanIcon.setImageResource(R.drawable.ic_action_walk);
                break;
            case CAR:
                meanIcon.setImageResource(R.drawable.ic_action_car);
                break;
            case BIKE:
                meanIcon.setImageResource(R.drawable.ic_action_bike);
                break;
            case BUS:
                meanIcon.setImageResource(R.drawable.ic_action_bus);
                break;
            case TRAIN:
                meanIcon.setImageResource(R.drawable.ic_action_train);
                break;
            case TRAM:
                meanIcon.setImageResource(R.drawable.ic_action_tram);
                break;
            case METRO:
                meanIcon.setImageResource(R.drawable.ic_action_subway);
                break;

        }

        String maxDist = constraint.maxDistance > 1000 ? String.format("%.1fKm", constraint.maxDistance/1000)
                                                       : String.format("%.0fm", constraint.maxDistance);

        final StringBuilder weatherList = new StringBuilder();
        for (int i=0; i < constraint.weather.size(); i++) {
            if(i == constraint.weather.size()-1) {
                weatherList.append(String.format("%s", constraint.weather.get(i).toString()));
            } else {
                weatherList.append(String.format("%s, ", constraint.weather.get(i).toString()));
            }
        }

        if(constraint.timeSlot == null) {
            textView.setText( String.format("Travel by %s for max %s when weather is: %s",
                    constraint.mean.toString().toUpperCase(), maxDist, weatherList.toString() ));
        } else {
            textView.setText( String.format("Travel by %s for max %s when weather is: %s or between %s and %s",
                    constraint.mean.toString().toUpperCase(), maxDist, weatherList.toString(),
                    constraint.timeSlot.startingTime.toString("HH:mm"), constraint.timeSlot.endingTime.toString("HH:mm")));
        }


        return convertView;
    }

}