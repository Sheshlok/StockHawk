package com.sam_chordas.android.stockhawk.data.model.stockQuote;

import com.google.gson.annotations.SerializedName;

public class Query {
    @SerializedName("results") private Results results;

    public Results getResults() {
        return this.results;
    }

    public Query setResults(Results results) {
        this.results = results;
        return this;
    }
}
