package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.StockHawkApplication;
import com.sam_chordas.android.stockhawk.data.model.stockQuote.Quote;
import com.sam_chordas.android.stockhawk.data.repository.StockRepository;
import com.sam_chordas.android.stockhawk.utilities.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 22/07/16.
 *
 * RemoteViewsService controls the data being shown in the scrollable weather detail widget.
 * From doc, RemoteViewsService is extended so that the appropriate RemoteViewsFactory method can be
 * used to promote remote collection view (ListView, GridView) etc.
 *
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB) // (V11)
public class StockListWidgetRemoteViewsService extends RemoteViewsService {

    @Inject StockRepository mStockRepository;

    @Override
    public void onCreate() {
        ((StockHawkApplication)getApplication()).getAppComponent().inject(StockListWidgetRemoteViewsService.this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        /**
         * RemoteViewsFactory is an interface for an adapter between a remote collection view
         * (ListView, GridView etc.) and the underlying data for that view. The implementor is
         * responsible for making a remote view for each item in the data set. This interface is
         * a thin wrapper around Adapter.
         */
        return new RemoteViewsFactory() {

            private List<Quote> data = null;
            private CompositeSubscription mCompositeSubscriptions;

            @Override
            public void onCreate() {
                // Inject dependencies and initialize instance variables.
                mCompositeSubscriptions = new CompositeSubscription();
            }

            @Override
            public void onDataSetChanged() {
                // This method is called by the app hosting the widget, i.e. launcherApp.
                // However, our ContentProvider is not exported so it doesn't have access to data.
                // Therefore, we need to clear (and finally restore) the calling identity so that
                // calls use our process and permission

                final long identityToken = Binder.clearCallingIdentity();

                mCompositeSubscriptions.add(mStockRepository.getStockListForWidget()
                        .observeOn(Schedulers.immediate())
                        .subscribe(stockQuotesList -> data = stockQuotesList,
                                throwable -> Timber.e("Widget Data loading Failed!: %s", throwable.toString()),
                                () -> Timber.i("Widget Data Loading Completed!")));

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                data = null;
                mCompositeSubscriptions.unsubscribe();
            }

            @Override
            public int getCount() {
                return data == null ? 0: data.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || data.get(position) == null)
                    return null;
                int priceChangeDrawableId;

                // Data checks OK //
                Timber.e("{ Data: %s, %s, %s, %s, %s }", position,
                        data.get(position).getStockSymbol() != null ? data.get(position).getStockSymbol(): "---",
                        data.get(position).getStockPrice() != null ? data.get(position).getStockPrice(): "---",
                        data.get(position).getChangeInPercent() != null ? data.get(position).getChangeInPercent(): "---",
                        data.get(position).IsUp());
                // Data checks OK //

                // Bind data to remoteViews
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                remoteViews.setTextViewText(R.id.widget_stock_symbol,
                        data.get(position).getStockSymbol() != null ? data.get(position).getStockSymbol(): "---");
                remoteViews.setTextViewText(R.id.widget_bid_price,
                        data.get(position).getStockPrice() != null ? data.get(position).getStockPrice(): "---");
                remoteViews.setTextViewText(R.id.widget_percent_change,
                        data.get(position).getChangeInPercent() != null ? data.get(position).getChangeInPercent(): "---");


                    if (data.get(position).IsUp()) {
                            priceChangeDrawableId = R.drawable.percent_change_pill_green;
                        } else {
                            priceChangeDrawableId = R.drawable.percent_change_pill_red;
                        }

                    remoteViews.setInt(R.id.widget_percent_change, "setBackgroundResource", priceChangeDrawableId);

                /*
                    Now handle the unique part for each element, calling 'setOnClickFillInIntent' to
                    fill in pendingTemplate appropriately.
                 */
                Bundle extras = new Bundle();
                extras.putInt(Constants.SELECTED_STOCK_POSITION, position);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                return data.get(position) != null ? data.get(position).getStockSymbol().hashCode(): position;
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
