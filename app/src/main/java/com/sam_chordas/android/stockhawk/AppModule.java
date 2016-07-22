package com.sam_chordas.android.stockhawk;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sheshloksamal on 20/07/16.
 */

@Module
public class AppModule {

    private final StockHawkApplication application;

    public AppModule(StockHawkApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    Application provideApplication() {
        return this.application;
    }
}
