package com.sam_chordas.android.stockhawk.utilities;

/**
 * Created by sheshloksamal on 20/07/16.
 */

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;

import com.sam_chordas.android.stockhawk.data.provider.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.provider.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sheshloksamal on 12/03/16.
 * @see  <a href="https://github.com/square/sqlbrite/blob/master/sample%2Fsrc%2Fmain%2Fjava%2Fcom%2Fexample%2Fsqlbrite%2Ftodo%2Fdb%2FDb.java">
 *     SqlBrite Sample db>Db.java</a>
 */
public final class DbUtils {

    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    private DbUtils() {
        throw new AssertionError("No instances.");
    }

    @SuppressLint("DefaultLocale")
    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(Context context, JSONObject jsonObject, String created) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE,
                    truncateChange(jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }
            builder.withValue(QuoteColumns.CREATED, created);
            builder.withValue(QuoteColumns.COMPANYNAME, jsonObject.getString("Name"));

        } catch (JSONException e) {
            e.printStackTrace();
            // Server is malfunctioning
            PrefUtils.setQuoteStatus(context, StockTaskService.STOCK_QUOTE_SERVER_INVALID);
        }
        return builder.build();
    }
}

