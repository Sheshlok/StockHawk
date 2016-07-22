package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.activity.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.activity.StockDetailsActivity;

/**
 * Created by sheshloksamal on 22/07/16.
 *
 * Provides for a scrollable stocks detail widget
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockListWidgetProvider extends AppWidgetProvider {

    /** TargetApi(Build.VERSION_CODES.HONEYCOMB) // For AppWidgetManager.notifyAppWidgetViewDataChanged */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            /*
                This will take us to RemoteViewsFactory onDataSetChanged method
             */
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }

    /** TargetApi(Build.VERSION_CODES.HONEYCOMB) // For remoteViews.setPendingIntentTemplate */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this procedure for each appWidget that belongs to this provider
        for (int appWidgetId: appWidgetIds) {
            // RemoteViews describe view hierarchy in another process
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);

            // Create an intent to launch MyStocksActivity. This makes sure that clicking on the
            // header takes us to MyStocksActivity
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

            // Open Detail Activity on List Item Click. Needs to be changed for tablet (2-pane) support
            Intent clickIntentTemplate = new Intent(context, StockDetailsActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Set up the collection. This takes us to 'onCreate' in RemoteViewsFactory in the
            // StockListWidgetRemoteViewsService, and then through the RemoteViewsFactory life-cycle
            // events
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, remoteViews);
            } else {
                setRemoteAdapterV11(context, remoteViews);
            }

            // Finished setting up remoteViews except looking up and binding data (background thread operation)
            // Do that in DetailWidgetRemoteViewsService. We can even do it here with observables.
            // This invariably calls the 'onDataSetChanged' method in RemoteViewsFactory

            // Now, tell the AppWidgetManager to perform an update on the current widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    /**
     * Sets the remote adapter to fill in the list items for build version codes >= ICS(14).
     * This takes us to 'onCreate' method in the RemoteAdapter
     *
     * @param remoteViews RemoteViews to set the RemoteAdapter.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews remoteViews) {
        remoteViews.setRemoteAdapter(R.id.widget_list, new Intent(context, StockListWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter to fill in the list items for build versions >=11.
     * This takes us to 'onCreate' method to set the RemoteAdapter.
     *
     * @param remoteViews RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews remoteViews) {
        remoteViews.setRemoteAdapter(0, R.id.widget_list, new Intent(context, StockListWidgetRemoteViewsService.class));
    }



}
