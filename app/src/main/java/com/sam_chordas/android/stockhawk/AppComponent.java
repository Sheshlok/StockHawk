package com.sam_chordas.android.stockhawk;

import com.sam_chordas.android.stockhawk.data.DataModule;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.activity.BaseActivity;
import com.sam_chordas.android.stockhawk.ui.fragment.BaseFragment;
import com.sam_chordas.android.stockhawk.widget.StockListWidgetRemoteViewsService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sheshloksamal on 20/07/16.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class
})

public interface AppComponent {
    void inject(BaseFragment baseFragment);
    void inject(BaseActivity baseActivity);
    void inject(StockListWidgetRemoteViewsService stockListWidgetRemoteViewsService);
    void inject(StockTaskService stockTaskService);
}
