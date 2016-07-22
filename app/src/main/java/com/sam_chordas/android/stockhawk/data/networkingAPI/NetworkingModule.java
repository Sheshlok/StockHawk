package com.sam_chordas.android.stockhawk.data.networkingAPI;

import android.app.Application;

import com.sam_chordas.android.stockhawk.data.networkingAPI.jsonpGsonCustomConverter.JsonpParser;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 */

@Module
public class NetworkingModule {

    // Writing custom interceptor since Jsonp custom converter is not working for some reason.
    private final Interceptor REMOVE_WRAPPING_IN_2nd_API_INTERCEPTOR = chain -> {
        Response originalResponse = chain.proceed(chain.request());
        String responseBody = originalResponse.body().string();

        // Since the 2nd API response is wrapped within finance_charts_json_callback(...)"
        if (responseBody.startsWith("finance_charts_json_callback( ")) {
            responseBody = JsonpParser.jsonpToJson(responseBody);
        }

        return originalResponse.newBuilder()
                .body(ResponseBody.create(MediaType.parse("text/javascript; charset=UTF-8"), responseBody))
                .build();
    };


    //Setting up Logging interceptors for Http
    private final HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS);


    private final int DISK_CACHE_SIZE = 20 * 1024 * 1024; // 20MB


    @Provides @Singleton
    Cache provideOkHttpCache(Application app) {
        // Install an HTTP Cache in the application cache directory
        File cacheDir = new File(app.getCacheDir(), "http");
        return new Cache(cacheDir, DISK_CACHE_SIZE);

    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(REMOVE_WRAPPING_IN_2nd_API_INTERCEPTOR)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .cache(cache)
                .build();

    }

    @Provides @Singleton
    Retrofit.Builder provideRetrofitBuilder(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient);
    }

    @Provides @Singleton
    StockAPI provideStockAPI(Retrofit.Builder retrofitBuilder) {
        return new StockAPI(retrofitBuilder);
    }
}
