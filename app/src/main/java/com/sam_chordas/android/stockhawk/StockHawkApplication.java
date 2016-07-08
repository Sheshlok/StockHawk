package com.sam_chordas.android.stockhawk;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 08/07/16.
 */
public class StockHawkApplication extends Application {

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        StockHawkApplication stockHawkApplication = (StockHawkApplication) context.getApplicationContext();
        return  stockHawkApplication.mRefWatcher;
    }

}
