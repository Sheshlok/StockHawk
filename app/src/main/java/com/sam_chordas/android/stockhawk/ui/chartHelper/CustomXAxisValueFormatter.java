package com.sam_chordas.android.stockhawk.ui.chartHelper;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.SimpleDateFormat;

/**
 * Created by sheshloksamal on 15/07/16.
 *
 */
public class CustomXAxisValueFormatter implements AxisValueFormatter {

    private String mHistory;

    public CustomXAxisValueFormatter(String history) {
        this.mHistory = history;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String date = "";
        SimpleDateFormat formatSDF;

        switch (mHistory) {
            case "1d":
                formatSDF = new SimpleDateFormat("H:mm");
                date = formatSDF.format((long) (value));
//                Timber.e("Timestamp: { Value: %d, time: %s }, History: %s", (long) value, date, mHistory);
                break;
            case "1m":
            case "3m":
            case "6m":
                formatSDF = new SimpleDateFormat("dd-MMM");
                date = formatSDF.format(value);
//                Timber.e("Date: { Value: %d, date: %s }, History: %s", (long) value, date, mHistory);
                break;
            case "1y":
            case "2y":
            case "5y":
            case "max":
                formatSDF = new SimpleDateFormat("MMM-yy");
                date = formatSDF.format(value);
//                Timber.e("Date: { Value: %d, date: %s }, History: %s", (long) value, date, mHistory);
                break;
        }

        return date;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }

}
