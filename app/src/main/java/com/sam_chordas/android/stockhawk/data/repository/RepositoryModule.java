package com.sam_chordas.android.stockhawk.data.repository;

import android.content.ContentResolver;

import com.sam_chordas.android.stockhawk.data.networkingAPI.StockAPI;
import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 */
@Module
public class RepositoryModule {

    @Provides @Singleton
    public StockRepository provideStockRepository(StockAPI stockAPI, BriteContentResolver briteContentResolver,
                                                  ContentResolver contentResolver) {
        return new StockRepositoryImpl(stockAPI, briteContentResolver, contentResolver);
    }
}
