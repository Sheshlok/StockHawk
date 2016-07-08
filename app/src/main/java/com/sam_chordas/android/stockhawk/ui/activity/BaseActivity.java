package com.sam_chordas.android.stockhawk.ui.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

import butterknife.ButterKnife;

/**
 * Created by sheshloksamal on 08/07/16.
 * * Base class for all activities. Binds views, sets up toolbar. Memory leaks in activity are
 * watched by application (via LeakCanary)
 */
public abstract class BaseActivity extends AppCompatActivity {

    @CallSuper @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Todo: Remove later as this causes memory leaks. Only for DB/network related debugging
        Stetho.initializeWithDefaults(this);
    }

    @CallSuper @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        // Get a reference to all views in the contentView
        ButterKnife.bind(this);
    }
}
