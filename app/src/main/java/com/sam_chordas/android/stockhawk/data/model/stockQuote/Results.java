package com.sam_chordas.android.stockhawk.data.model.stockQuote;

import com.google.gson.annotations.SerializedName;

public class Results {
    @SerializedName("quote") private Quote quote;

    public Quote getQuote() {
        return this.quote;
    }

    public Results setQuote(Quote quote){
        this.quote = quote;
        return this;
    }
}
