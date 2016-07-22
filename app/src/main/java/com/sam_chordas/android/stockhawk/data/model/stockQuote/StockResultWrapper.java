package com.sam_chordas.android.stockhawk.data.model.stockQuote;

/**
 * Created by sheshloksamal on 12/07/16.
 */

public class StockResultWrapper {
    Query query;

    public Query getQuery() {
        return this.query;
    }

    public StockResultWrapper setQuery(Query query) {
        this.query = query;
        return this;
    }
}
