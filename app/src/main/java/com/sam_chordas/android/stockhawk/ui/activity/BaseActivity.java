package com.sam_chordas.android.stockhawk.ui.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.StockHawkApplication;
import com.sam_chordas.android.stockhawk.data.repository.StockRepository;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 08/07/16.
 * * Base class for all activities. Binds views, sets up toolbar, subscriptions.
 * Memory leaks in activity are watched by application (via LeakCanary)
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.toolbar) Toolbar mToolbar;
    protected CompositeSubscription mCompositeSubscriptions;

    @Inject StockRepository mStockRepository;

    @CallSuper @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeSubscriptions = new CompositeSubscription();

        ((StockHawkApplication) getApplication()).getAppComponent().inject(this);
    }

    @CallSuper @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        // Get a reference to all views in the contentView
        ButterKnife.bind(this);
        setUpToolbar();
    }

    @CallSuper @Override
    protected void onDestroy() {
        mCompositeSubscriptions.unsubscribe();
        super.onDestroy();
    }

    private void setUpToolbar() {
        if (mToolbar == null) {
            Timber.w("Didn't find toolbar");
            return;
        }

        // Set elevation of 4dp as per material design
        ViewCompat.setElevation(mToolbar, R.dimen.toolbar_elevation);

        setSupportActionBar(mToolbar);

        // Get a supportActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }
}
