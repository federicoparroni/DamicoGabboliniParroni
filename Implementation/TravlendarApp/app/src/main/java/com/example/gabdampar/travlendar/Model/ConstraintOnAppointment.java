/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.gabdampar.travlendar.Model.travelMean.TravelMean;
import com.example.gabdampar.travlendar.Model.travelMean.TravelMeanEnum;

public class ConstraintOnAppointment extends Constraint implements Parcelable {

    public ConstraintOnAppointment(TravelMeanEnum mean, float maxDistance){
        this.mean = mean;
        this.maxDistance = maxDistance;
    }

    protected ConstraintOnAppointment(Parcel in) {
        mean = (TravelMeanEnum) in.readValue(TravelMeanEnum.class.getClassLoader());
        maxDistance = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mean);
        dest.writeFloat(maxDistance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ConstraintOnAppointment> CREATOR = new Parcelable.Creator<ConstraintOnAppointment>() {
        @Override
        public ConstraintOnAppointment createFromParcel(Parcel in) {
            return new ConstraintOnAppointment(in);
        }

        @Override
        public ConstraintOnAppointment[] newArray(int size) {
            return new ConstraintOnAppointment[size];
        }
    };
}