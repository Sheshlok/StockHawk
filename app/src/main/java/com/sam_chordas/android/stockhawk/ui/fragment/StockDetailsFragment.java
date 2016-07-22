package com.sam_chordas.android.stockhawk.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.model.stockHistory.Series;
import com.sam_chordas.android.stockhawk.ui.chartHelper.CustomMarkerView;
import com.sam_chordas.android.stockhawk.ui.chartHelper.CustomXAxisValueFormatter;
import com.sam_chordas.android.stockhawk.ui.chartHelper.CustomYAxisValueFormatter;
import com.sam_chordas.android.stockhawk.utilities.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 */
public class StockDetailsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @Override
    public void onRefresh() {
        loadStockDetails();
        drawChart();
    }

    private static final String LINE_CHART_ENTRIES = "line_chart_entries";

    // For preserving state during rotation or any event when the app gets killed by the system
    private static final String SELECTED_TAB_POSITION_STATE = "selected_tab_position";
    private static final int DEFAULT_TAB_POSITION = 0;

    private String mSelectedStockSymbol;
    private String mQuery;

    private String mPeriod;

    /** Tab position for history */
    private int mTabPosition;

    @BindView(R.id.stock_details_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.stock_price) TextView mStockPrice;
    @BindView(R.id.stock_percent_change) TextView mStockPercentChange;

    @BindView(R.id.opening_price) TextView mOpeningPrice;
    @BindView(R.id.market_cap) TextView mMarketCap;
    @BindView(R.id.book_value) TextView mBookValue;
    @BindView(R.id.ebitda) TextView mEBITDA;
    @BindView(R.id.day_high) TextView mDaysHigh;
    @BindView(R.id.year_high) TextView mYearHigh;
    @BindView(R.id.day_low) TextView mDaysLow;
    @BindView(R.id.year_low) TextView mYearLow;
    @BindView(R.id.volume) TextView mVolume;
    @BindView(R.id.avg_vol) TextView mAverageVolume;
    @BindView(R.id.previous_close) TextView mPrevClose;
    @BindView(R.id.peRatio) TextView mPERatio;
    @BindView(R.id.eps) TextView mEPS;
    @BindView(R.id.dividend_yield) TextView mDivYield;

    @BindView(R.id.tabbar_choose_time_span) TabLayout mTabStockHistory;

    @BindView(R.id.stock_history_line_chart) LineChart mLineChart;

    public StockDetailsFragment() {
        /* Required no arguments constructor */
    }

    /**
     * Create fragment and pass bundle with data as its args
     */
    public static StockDetailsFragment newInstance(String stockSymbol) {
        StockDetailsFragment stockDetailsFragment = new StockDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SELECTED_STOCK_SYMBOL, stockSymbol);
        stockDetailsFragment.setArguments(args);
        return stockDetailsFragment;
    }

    /** Initialize instance variables with arguments supplied when data was created.
     * Recover state instance variables from savedInstanceState bundle*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSelectedStockSymbol = getArguments().getString(Constants.SELECTED_STOCK_SYMBOL);
            Timber.e("Selected SS: %s", mSelectedStockSymbol);
        }

        mQuery = "select * from yahoo.finance.quotes where symbol in (\"" + mSelectedStockSymbol + "\")";

        if (savedInstanceState == null) {
            mTabPosition = DEFAULT_TAB_POSITION;
        } else {
            mTabPosition = savedInstanceState.getInt(SELECTED_TAB_POSITION_STATE, DEFAULT_TAB_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        mPeriod = mTabStockHistory.getTabAt(mTabPosition).getText().toString().toLowerCase();

        Timber.e("mPeriod: %s", mPeriod);

        TabLayout.Tab initialTab = mTabStockHistory.getTabAt(mTabPosition);
        if (initialTab != null) {
            mTabStockHistory.setSmoothScrollingEnabled(true);
            initialTab.select();
        }

        mTabStockHistory.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabPosition = tab.getPosition();
                mPeriod = ((String) tab.getText()).toLowerCase();
                Timber.e("Tab Selected. mPeriod: %s, stocksymbol: %s", mPeriod, mSelectedStockSymbol);
                mLineChart.getXAxis().setValueFormatter(new CustomXAxisValueFormatter(mPeriod));
                drawChart();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initializeChartSetup();
        initializeSwipeRefreshLayout();

        loadStockDetails();
        drawChart();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.e("Selected tab position: %s", mTabPosition);
        Timber.e("mPeriod: %s", mPeriod);
        Timber.e("Selected Stock Symbol: %s", mSelectedStockSymbol);
        outState.putInt(SELECTED_TAB_POSITION_STATE, mTabPosition);
        super.onSaveInstanceState(outState);
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    private void loadStockDetails() {

        String format = "json";
        String env = "store://datatables.org/alltableswithkeys";
        mCompositeSubscriptions.add(mStockRepository.getStockDetails(mQuery, format, env)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(quote -> {
                    if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
                    mStockPrice.setText(quote.getStockPrice() != null ? quote.getStockPrice() : "---");
                    mStockPercentChange.setText(quote.getChangeInPercent() != null ? quote.getChangeInPercent() : "---");
                    if (quote.getChangeInPercent().charAt(0) == '+') {
                        mStockPercentChange.setTextColor(getResources().getColor(R.color.stockGainBackground));
                    } else
                        mStockPercentChange.setTextColor(getResources().getColor(R.color.stockLossBackground));
                    mOpeningPrice.setText(quote.getOpen() != null ? quote.getOpen() : "---");
                    mMarketCap.setText(quote.getMarketCap() != null ? quote.getMarketCap() : "---");
                    mBookValue.setText(quote.getBookValue() != null ? quote.getBookValue() : "---");
                    mEBITDA.setText(quote.getEBITDA() != null ? quote.getEBITDA() : "---");
                    mDaysHigh.setText(quote.getDaysHigh() != null ? quote.getDaysHigh() : "---");
                    mYearHigh.setText(quote.getYearHigh() != null ? quote.getYearHigh() : "---");
                    mDaysLow.setText(quote.getDaysLow() != null ? quote.getDaysLow() : "---");
                    mYearLow.setText(quote.getYearLow() != null ? quote.getYearLow() : "---");
                    mVolume.setText(quote.getVolume() != null ? quote.getVolume() : "---");
                    mAverageVolume.setText(quote.getAverageDailyVolume() != null ? quote.getAverageDailyVolume() : "---");
                    mPrevClose.setText(quote.getPreviousClose() != null ? quote.getPreviousClose() : "---");
                    mPERatio.setText(quote.getPERatio() != null ? quote.getPERatio() : "---");
                    mEPS.setText(quote.getEPS() != null ? quote.getEPS() : "---");
                    mDivYield.setText(quote.getDividendYield() != null ? quote.getDividendYield() : "---");
                    // Set SwipeRefreshLayout.setRefreshing(false);
                }, throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Timber.e("Stock Details Loading Failed!");
                    showToast("Stock Details Loading Failed. Swipe Up to Refresh!");
                }, () -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Timber.i("Stock Loading Completed");
                    // Stop the spinner
                }));

    }

    private void drawChart() {
        mCompositeSubscriptions.add(mStockRepository.getStockHistory(mSelectedStockSymbol, mPeriod, "json")
                .map(this::getLineDataForChart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lineData -> {
                    if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
                    setData(lineData);
                }, throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Timber.e("Data set NA. Error: ", throwable);
                }, () -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Timber.i("Chart Done!");
                }));
    }

    private LineData getLineDataForChart(List<Series> seriesList) {

        ArrayList<Entry> chartEntries = new ArrayList<>();
        for (Series seriesItem : seriesList) {
            if (mPeriod.equals("1d")) {
//                Timber.e("{ Timestamp: %s, close: %s }", seriesItem.getTimeStamp(), seriesItem.getClose());
                chartEntries.add(new Entry(Float.parseFloat(seriesItem.getTimeStamp()) * 1000,
                        Float.parseFloat(seriesItem.getClose())));
            } else {
                DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                try {
                    Date date = formatter.parse(seriesItem.getDate());
//                    Timber.e("{ Date: %s, %s, close: %s }", seriesItem.getDate(), date.getTime()
//                            , seriesItem.getClose());
                    chartEntries.add(new Entry((float) date.getTime(), Float.parseFloat(seriesItem.getClose())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        LineDataSet lineDataSet;

        if (mLineChart.getData() != null &&
                Arrays.asList(mLineChart.getData().getDataSetLabels()).contains(LINE_CHART_ENTRIES)) {
            lineDataSet = (LineDataSet) mLineChart.getData().getDataSetByLabel(LINE_CHART_ENTRIES, false);
            lineDataSet.setValues(chartEntries);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(chartEntries, LINE_CHART_ENTRIES);

            //----------------------- Style line data set here-------------------------------------
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setColor(getResources().getColor(R.color.chart_line_color));
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawFilled(true);

            if (Build.VERSION.SDK_INT >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_chart_color);
                lineDataSet.setFillDrawable(drawable);
            } else {
                lineDataSet.setFillColor(R.color.colorPrimary);
            }
            //----------------------- Style line data set here-------------------------------------
        }

        return new LineData(lineDataSet);
    }

    private void initializeChartSetup() {
        // Sets the background color
        mLineChart.setBackgroundColor(getResources().getColor(R.color.chart_background_color));
        mLineChart.setDrawGridBackground(false);

        // Further a11y features: Set whether this view should have sound effects enabled for events
        // such as clicking and touching.
        mLineChart.setSoundEffectsEnabled(true);
        mLineChart.setHapticFeedbackEnabled(true);

        // Sets the description details
        mLineChart.setDescription("");
        mLineChart.setNoDataText(getString(R.string.chart_no_data_text));

        // Disable legends
        mLineChart.getLegend().setEnabled(false);

        // Remove right side markings
        mLineChart.getAxisRight().setEnabled(false);

        // In order: enable touch gestures, zooming (on both X & Y simultaneously), dragging and scaling
        mLineChart.setTouchEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        //Create a custom marker view and specify the layout to use for it
        MarkerView markerView = new CustomMarkerView(getActivity(), R.layout.custom_marker_view);

        // Set the marker to the chart
        mLineChart.setMarkerView(markerView);


        // Styling/modifying the X-axis
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(getResources().getColor(R.color.chart_axis_line_color));
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(mPeriod));
        xAxis.setGridColor(getResources().getColor(R.color.chart_grid_lines_color));
        xAxis.setTextColor(getResources().getColor(R.color.chart_axis_text_color));
        xAxis.setTextSize(11f);
        xAxis.enableGridDashedLine(10f, 10f, 10f);
        xAxis.setLabelCount(6, true);
//        xAxis.setGranularityEnabled(true);

        // Styling/modifying the left Y-axis.
        YAxis leftYAxis = mLineChart.getAxisLeft();
        leftYAxis.setDrawLabels(true);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.chart_axis_line_color));
        leftYAxis.setGridColor(getResources().getColor(R.color.chart_grid_lines_color));
        leftYAxis.setTextColor(getResources().getColor(R.color.chart_axis_text_color));
        leftYAxis.setValueFormatter(new CustomYAxisValueFormatter());
        leftYAxis.setTextSize(11f);
        leftYAxis.enableGridDashedLine(10f, 10f, 10f);
//        leftYAxis.setGranularityEnabled(true);

    }

    private void setData(LineData lineData) {
        Timber.e("in setData: %s", mPeriod);
        mLineChart.setData(lineData); //Calls notifyDataSetChanged in method
        mLineChart.animateXY(2500, 2500); // Animate
        mLineChart.invalidate(); //Re-draw
    }


}
