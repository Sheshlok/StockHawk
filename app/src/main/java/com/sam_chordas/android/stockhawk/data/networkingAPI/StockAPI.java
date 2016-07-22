package com.sam_chordas.android.stockhawk.data.networkingAPI;

import com.sam_chordas.android.stockhawk.data.model.stockHistory.Series;
import com.sam_chordas.android.stockhawk.data.model.stockQuote.StockResultWrapper;
import com.sam_chordas.android.stockhawk.utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sheshloksamal on 11/07/16.
 * Base code modified from {@linkplain "https://gist.github.com/eneim/bfe81b4bfa87e1699077"}
 */

public class StockAPI {


    /**
     * For simpler reference purpose only. If there are 1000 URLs, just use index
     */
    private final int STOCK_DETAILS_API_INDEX = 0;
    private final int STOCK_HISTORY_API_INDEX = 1;

    /**
     * And 2 API helper instances. APIs themselves are private, invisible to the user
     */
    private final StockQuoteAPI mStockQuoteApi;
    private final StockHistoryAPI mStockHistoryApi;

    public StockAPI(Retrofit.Builder retrofitBuilder) {
        this.mStockQuoteApi = retrofitBuilder
                .baseUrl(Constants.API_BASE_URLs[/* 0 */ STOCK_DETAILS_API_INDEX])
//            .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StockQuoteAPI.class);
        this.mStockHistoryApi = retrofitBuilder
                .baseUrl(Constants.API_BASE_URLs[/* 1 */ STOCK_HISTORY_API_INDEX])
//            .addConverterFactory(JsonpGsonConverterFactory.create())
                .build()
                .create(StockHistoryAPI.class);

    }

    public Observable<StockResultWrapper> getStockDetails(String query, String format, String env) {
        return mStockQuoteApi.getStockDetails(query, format, env);
    }

    public Observable<Series.Response> getStockHistory(String companySymbol, String period, String format) {
        return mStockHistoryApi.getStockHistory(companySymbol, period, format);
    }

    /**
     *  We have 2 base URLs, so we create 2 API interfaces
     */

    private interface StockQuoteAPI {

        /* Send a GET stock details request for a particular stock */
        @GET("v1/public/yql")
        Observable<StockResultWrapper> getStockDetails(
                @Query("q") String query,
                @Query("format") String format,
                @Query("env") String env
        );
    }

    private interface StockHistoryAPI {
        /* Send a GET stock (user-selected) his tory request for a particular stock */
        @GET("instrument/1.0/{companySymbol}/chartdata;type=close;range={period}/{format}")
        Observable<Series.Response> getStockHistory(
                @Path("companySymbol") String companySymbol,
                @Path("period") String period,
                @Path("format") String format

        );
    }

}

