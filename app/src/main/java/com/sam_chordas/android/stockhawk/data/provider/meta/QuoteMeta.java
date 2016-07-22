package com.sam_chordas.android.stockhawk.data.provider.meta;

import android.database.Cursor;

import com.sam_chordas.android.stockhawk.data.model.stockQuote.Quote;
import com.sam_chordas.android.stockhawk.data.provider.QuoteColumns;
import com.sam_chordas.android.stockhawk.utilities.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

/**
 * Created by sheshloksamal on 20/07/16.
 */
public interface QuoteMeta {

    /**
     * Projection for Quote
     */
    String[] STOCK_PROJECTION_COMPANY_INFO = {
            QuoteColumns.SYMBOL,
            QuoteColumns.COMPANYNAME
    };

    String[] STOCK_PROJECTION_WIDGET_INFO = {
            QuoteColumns.SYMBOL,
            QuoteColumns.COMPANYNAME,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP
    };

    Func1<SqlBrite.Query, Map<String, String>> COMPANY_INFO_PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            assert cursor != null;
            Map<String, String> stocksMap = new LinkedHashMap<>(cursor.getCount());

            while (cursor.moveToNext()) {
                String stockSymbol = DbUtils.getString(cursor, QuoteColumns.SYMBOL);
                String companyName = DbUtils.getString(cursor, QuoteColumns.COMPANYNAME);
                stocksMap.put(stockSymbol, companyName);
            }
            return stocksMap;

        } finally {
            assert cursor != null;
            cursor.close();
        }
    };

    Func1<Cursor, List<Quote>> WIDGET_PROJECTION_MAP = cursor -> {
        try {
            List<Quote> stockQuoteList = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                stockQuoteList.add(new Quote()
                        .setStockSymbol(DbUtils.getString(cursor, QuoteColumns.SYMBOL))
                        .setName(DbUtils.getString(cursor, QuoteColumns.COMPANYNAME))
                        .setStockPrice(DbUtils.getString(cursor, QuoteColumns.BIDPRICE))
                        .setChangeInPercent(DbUtils.getString(cursor, QuoteColumns.PERCENT_CHANGE))
                        .setIsUp(DbUtils.getBoolean(cursor, QuoteColumns.ISUP)));
            }
            return stockQuoteList;
        } finally {
            cursor.close();
        }
    };
}

