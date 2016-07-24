package com.sam_chordas.android.stockhawk.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

/**
 * Created by sheshloksamal on 23/07/16.
 * Involves using PreferenceManager
 */
public class PrefUtils {

    @SuppressLint("CommitPrefEdits")
    public static void setQuoteStatus(Context context, @StockTaskService.StockQuoteStatus int quoteStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_quote_status_key), quoteStatus);

        // Since it will be called from TaskService, which is running on a background thread, call 'commit'
        spe.commit();
    }

    /**
     * Note that we need to suppress warnings for out IntDef because we are reading an integer out of
     * our SharedPreferences, which could be out of the range of our quote Status
     */

    @SuppressWarnings("ResourceType")
    @StockTaskService.StockQuoteStatus
    public static int getQuoteStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_quote_status_key), StockTaskService.STOCK_QUOTE_SERVER_UNKNOWN);
    }

    @StockTaskService.StockQuoteStatus
    public static void resetLocationStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_quote_status_key), StockTaskService.STOCK_QUOTE_SERVER_UNKNOWN);

        // Since it will be called from the foreground/UI thread, call '.apply' here
        spe.apply();
    }


}
