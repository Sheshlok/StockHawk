package com.sam_chordas.android.stockhawk.ui.chartHelper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;

import java.text.DecimalFormat;

/**
 * Created by sheshloksamal on 14/07/16.
 */
public class CustomYAxisValueFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;
    private FormattedStringCache.PrimFloat mFormattedStringCache;

    public CustomYAxisValueFormatter() {
        // use one decimal
        mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,##0.0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        /** Use this section to:
         * 1. Implement custom logic
         * 2. Access the YAxis object to get more information
         */
//        Timber.e("Value: %f", value);
        return mFormattedStringCache.getFormattedValue(value) + " $"; // Append a $ sign
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
