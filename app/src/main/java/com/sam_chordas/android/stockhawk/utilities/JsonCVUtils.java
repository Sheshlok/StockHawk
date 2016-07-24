package com.sam_chordas.android.stockhawk.utilities;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.sam_chordas.android.stockhawk.service.StockTaskService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class JsonCVUtils {

    public static boolean showPercent = true;

    public static ArrayList quoteJsonToContentVals(Context context, String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                String created = jsonObject.getString("created");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results").getJSONObject("quote");
                    batchOperations.add(DbUtils.buildBatchOperation(context, jsonObject, created));
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(DbUtils.buildBatchOperation(context, jsonObject, created));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Server is malfunctioning
            PrefUtils.setQuoteStatus(context, StockTaskService.STOCK_QUOTE_SERVER_INVALID);
            Timber.e("String to JSON failed: %s", e.getMessage());
        }
        return batchOperations;
    }

}
