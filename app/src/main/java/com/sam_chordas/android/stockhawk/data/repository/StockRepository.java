package com.sam_chordas.android.stockhawk.data.repository;

import com.sam_chordas.android.stockhawk.data.model.stockHistory.Series;
import com.sam_chordas.android.stockhawk.data.model.stockQuote.Quote;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by sheshloksamal on 20/07/16.
 * An interface which activities and fragments can use to get/store data as needed without
 * understanding how the data is retrieved/stored.
 *
 * @see StockRepositoryImpl
 */
public interface StockRepository {

    Observable<Quote> getStockDetails(String query, String format, String env);

    Observable<List<Series>> getStockHistory(String companySymbol, String period, String format);

    Observable<Map<String, String>> getSavedCompanies();

    Observable<List<Quote>> getStockListForWidget();

}
