package com.sam_chordas.android.stockhawk.data.repository;

import android.content.ContentResolver;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.sam_chordas.android.stockhawk.data.model.stockHistory.Series;
import com.sam_chordas.android.stockhawk.data.model.stockQuote.Quote;
import com.sam_chordas.android.stockhawk.data.networkingAPI.StockAPI;
import com.sam_chordas.android.stockhawk.data.provider.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.provider.QuoteProvider;
import com.sam_chordas.android.stockhawk.utilities.Utils;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 */
public class StockRepositoryImpl implements StockRepository {

    private final StockAPI mStockAPI;
    private final BriteContentResolver mBriteContentResolver;
    private final ContentResolver mContentResolver;

    public StockRepositoryImpl(StockAPI stockAPI, BriteContentResolver briteContentResolver,
                               ContentResolver contentResolver) {
        this.mStockAPI = stockAPI;
        this.mBriteContentResolver = briteContentResolver;
        this.mContentResolver = contentResolver;

    }

    @RxLogObservable
    public Observable<Quote> getStockDetails(String query, String format, String env) {
        return mStockAPI.getStockDetails(query, format, env)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
//                .onErrorResumeNext(Observable.empty())
                .map(stockResultWrapper -> stockResultWrapper.getQuery().getResults().getQuote())
                .map(quote -> {
                    quote.setVolume(Utils.format(Long.parseLong(quote.getVolume())));
                    quote.setAverageDailyVolume(Utils.format(Long.parseLong(quote.getAverageDailyVolume())));
                    return quote;
                })
                .subscribeOn(Schedulers.io());
    }

    @RxLogObservable
    public Observable<List<Series>> getStockHistory(String companySymbol, String period, String format) {
        return mStockAPI.getStockHistory(companySymbol, period, format)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
//                .onErrorResumeNext(Observable.empty())
                .map(seriesCollection -> seriesCollection.seriesList)
                .subscribeOn(Schedulers.io());

    }

    @RxLogObservable
    public Observable<Map<String, String>> getSavedCompanies() {
        return mBriteContentResolver.createQuery(QuoteProvider.Quotes.CONTENT_URI, Quote.STOCK_PROJECTION_COMPANY_INFO,
                QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null, true)
                .map(Quote.COMPANY_INFO_PROJECTION_MAP);

    }

    /** We don't need to observe the query for changes, since we are already sending a broadcast
     * from our stockTaskService. So, no need to use BriteContentResolver here.
     */
    @RxLogObservable
    public Observable<List<Quote>> getStockListForWidget() {
        return Observable.just(mContentResolver.query(QuoteProvider.Quotes.CONTENT_URI, Quote.STOCK_PROJECTION_WIDGET_INFO,
                QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null))
                .map(Quote.WIDGET_PROJECTION_MAP);

    }


}
