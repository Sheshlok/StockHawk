package com.sam_chordas.android.stockhawk.ui.chartHelper;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sheshloksamal on 15/07/16.
 * From doc on MPAndroidChart
 */
public class CustomMarkerView extends MarkerView{

    private TextView mPriceBubble;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        mPriceBubble = (TextView) findViewById(R.id.price_bubble);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
       mPriceBubble.setText(getResources().getString(R.string.format_marker_view, Utils.formatNumber(e.getY(), 1, true)));
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
