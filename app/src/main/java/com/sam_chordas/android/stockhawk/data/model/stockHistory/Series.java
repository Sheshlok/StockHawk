package com.sam_chordas.android.stockhawk.data.model.stockHistory;

import android.support.annotation.Nullable;

import com.example.autoparcel.AutoParcel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheshloksamal on 12/07/16.
 */

@AutoParcel
public class Series {

    @Nullable @SerializedName("Date") private String mDate;
    @SerializedName("close") private String mClose;
    @Nullable @SerializedName("Timestamp") private String mTimestamp;

    public Series() {}

    public String getDate() {
        return this.mDate;
    }

    public Series setDate(String date) {
        this.mDate = date;
        return this;
    }

    public String getClose() {
        return this.mClose;
    }

    public Series setClose(String close) {
        this.mClose = close;
        return this;
    }

    public String getTimeStamp() {
        return this.mTimestamp;
    }

    public Series setTimeStamp(String timeStamp) {
        this.mTimestamp = timeStamp;
        return this;
    }

    /** Implemented our custom annotation processor for implementing Parcelable, hence no need
     * of the boiler plate code below
     * */

//    // ---------------------------------Parcelable Implementation-----------------------------------
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.mDate);
//        dest.writeString(this.mClose);
//        dest.writeString(this.mTimestamp);
//    }
//
//    protected Series(Parcel in) {
//        this.mDate = in.readString();
//        this.mClose = in.readString();
//        this.mTimestamp = in.readString();
//    }
//
//    public static final Creator<Series> CREATOR = new Creator<Series>() {
//        @Override
//        public Series createFromParcel(Parcel in) {
//            return new Series(in);
//        }
//
//        @Override
//        public Series[] newArray(int size) {
//            return new Series[size];
//        }
//    };

    // ---------------------Wrapper Class for Retrofit to parse API response ----------------------
    public static class Response {
        @SerializedName("series") public List<Series> seriesList = new ArrayList<>();
    }

}
