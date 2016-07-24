package com.sam_chordas.android.stockhawk.data.model.stockQuote;

import android.support.annotation.Nullable;

import com.example.autoparcel.AutoParcel;
import com.google.gson.annotations.SerializedName;
import com.sam_chordas.android.stockhawk.data.provider.meta.QuoteMeta;


@AutoParcel
public class Quote implements QuoteMeta {

    /* Each of these values can be null as data of a stock may not be available except for name
    * which is always present (even when stock symbol does not exists, the name is filled in from
    * user input into the JSON response from YQL) */
    @SerializedName("symbol") private String mStockSymbol;
    @Nullable @SerializedName("Name") private String mCompanyName;
    @Nullable @SerializedName("Open") private String mOpeningPrice;
    @Nullable @SerializedName("MarketCapitalization") private String mMarketCap;
    @Nullable @SerializedName("BookValue") private String mBookValue;
    @Nullable @SerializedName("EBITDA") private String mEBITDA;
    @Nullable @SerializedName("DaysHigh") private String mDaysHigh;
    @Nullable @SerializedName("YearHigh") private String mYearHigh;
    @Nullable @SerializedName("DaysLow") private String mDaysLow;
    @Nullable @SerializedName("YearLow") private String mYearLow;
    @Nullable @SerializedName("Volume") private String mVolume;
    @Nullable @SerializedName("AverageDailyVolume") private String mAverageDailyVolume;
    @Nullable @SerializedName("PreviousClose") private String mPreviousClose;
    @Nullable @SerializedName("PERatio") private String mPERatio;
    @Nullable @SerializedName("EPSEstimateCurrentYear") private String mEPS;
    @Nullable @SerializedName("DividendYield") private String mDividendYield;
    @Nullable @SerializedName("Bid") private String mStockPrice;
    @Nullable @SerializedName("ChangeinPercent") private String mChangeInPercent;
    @Nullable @SerializedName("Change") private String mChange;


    public Quote() {}

    private boolean mIsUp = false;

    public boolean IsUp() {
        return this.mIsUp;
    }

    public Quote setIsUp(boolean isUp) {
        this.mIsUp = isUp;
        return this;
    }

    public String getStockSymbol() {
        return this.mStockSymbol;
    }

    public Quote setStockSymbol(String stockSymbol) {
        this.mStockSymbol = stockSymbol;
        return this;
    }

    public String getName() {
        return this.mCompanyName;
    }

    public Quote setName(String companyName) {
        this.mCompanyName = companyName;
        return this;
    }

    public String getOpen() {
        return this.mOpeningPrice;
    }

    public Quote setOpen(String openingPrice) {
        this.mOpeningPrice = openingPrice;
        return this;
    }

    public String getMarketCap() {
        return this.mMarketCap;
    }

    public Quote setMarketCap(String marketCap) {
        this.mMarketCap = marketCap;
        return this;
    }

    public String getBookValue() {
        return this.mBookValue;
    }

    public Quote setBookValue(String bookValue) {
        this.mBookValue = bookValue;
        return this;
    }

    public String getEBITDA() {
        return this.mEBITDA;
    }

    public Quote setEBITDA(String ebitda) {
        this.mEBITDA = ebitda;
        return this;
    }

    public String getDaysHigh() {
        return this.mDaysHigh;
    }

    public Quote setDaysHigh(String daysHigh) {
        this.mDaysHigh = daysHigh;
        return this;
    }

    public String getYearHigh() {
        return this.mYearHigh;
    }

    public Quote setYearHigh(String yearHigh) {
        this.mYearHigh = yearHigh;
        return this;
    }

    public String getDaysLow() {
        return this.mDaysLow;
    }

    public Quote setDaysLow(String daysLow) {
        this.mDaysLow = daysLow;
        return this;
    }

    public String getYearLow() {
        return this.mYearLow;
    }

    public Quote setYearLow(String yearLow) {
        this.mYearLow = yearLow;
        return this;
    }

    public String getVolume() {
        return this.mVolume;
    }

    public Quote setVolume(String volume) {
        this.mVolume = volume;
        return this;
    }

    public String getAverageDailyVolume() {
        return this.mAverageDailyVolume;
    }

    public Quote setAverageDailyVolume(String averageDailyVolume) {
        this.mAverageDailyVolume = averageDailyVolume;
        return this;
    }

    public String getPreviousClose() {
        return this.mPreviousClose;
    }

    public Quote setPreviousClose(String previousClose) {
        this.mPreviousClose = previousClose;
        return this;
    }

    public String getPERatio() {
        return this.mPERatio;
    }

    public Quote setPERatio(String peRatio) {
        this.mPERatio = peRatio;
        return this;
    }

    public String getEPS() {
        return this.mEPS;
    }

    public Quote setEPS(String eps) {
        this.mEPS = eps;
        return this;
    }

    public String getDividendYield() {
        return this.mDividendYield;
    }

    public Quote setDividendYield(String dividendYield) {
        this.mDividendYield = dividendYield;
        return this;
    }

    public String getStockPrice() {
        return this.mStockPrice;
    }

    public Quote setStockPrice(String stockPrice) {
        this.mStockPrice = stockPrice;
        return this;
    }

    public String getChangeInPercent() {
        return this.mChangeInPercent;
    }

    public Quote setChangeInPercent(String changeInPercent) {
        this.mChangeInPercent = changeInPercent;
        return this;
    }

    public String getChange() {
        return this.mChange;
    }

    public Quote setChange(String change) {
        this.mChange = change;
        return this;
    }

    @Override
    public String toString() {
        return "Stock of {" + this.mCompanyName + "}";
    }


    /** Implemented our custom annotation processor for implementing Parcelable, hence no need
     * of the boiler plate code below
     * */
//    // ---------------------------------Parcelable Implementation-----------------------------------
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.mStockSymbol);
//        dest.writeString(this.mBidPrice);
//        dest.writeString(this.mCompanyName);
//        dest.writeString(this.mOpeningPrice);
//        dest.writeString(this.mMarketCap);
//        dest.writeString(this.mBookValue);
//        dest.writeString(this.mEBITDA);
//        dest.writeString(this.mDaysHigh);
//        dest.writeString(this.mYearHigh);
//        dest.writeString(this.mDaysLow);
//        dest.writeString(this.mYearLow);
//        dest.writeString(this.mVolume);
//        dest.writeString(this.mAverageDailyVolume);
//        dest.writeString(this.mPreviousClose);
//        dest.writeString(this.mPERatio);
//        dest.writeString(this.mEPS);
//        dest.writeString(this.mDividendYield);
//        dest.writeString(this.mStockPrice);
//        dest.writeString(this.mChangeInPercent);
//    }
//
//    protected Quote(Parcel in) {
//        this.mStockSymbol = in.readString();
//        this.mBidPrice = in.readString();
//        this.mCompanyName = in.readString();
//        this.mOpeningPrice = in.readString();
//        this.mMarketCap = in.readString();
//        this.mBookValue = in.readString();
//        this.mEBITDA = in.readString();
//        this.mDaysHigh = in.readString();
//        this.mYearHigh = in.readString();
//        this.mDaysLow = in.readString();
//        this.mYearLow = in.readString();
//        this.mVolume = in.readString();
//        this.mAverageDailyVolume = in.readString();
//        this.mPreviousClose = in.readString();
//        this.mPERatio = in.readString();
//        this.mEPS = in.readString();
//        this.mDividendYield = in.readString();
//        this.mStockPrice = in.readString();
//        this.mChangeInPercent = in.readString();
//    }
//
//    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
//        @Override
//        public Quote createFromParcel(Parcel in) {
//            return new Quote(in);
//        }
//
//        @Override
//        public Quote[] newArray(int size) {
//            return new Quote[size];
//        }
//    };

}
