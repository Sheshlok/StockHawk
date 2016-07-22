package com.sam_chordas.android.stockhawk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.StockHawkApplication;
import com.sam_chordas.android.stockhawk.data.repository.StockRepository;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 * Base class for all fragments
 * Binds views, performs dependency injections, and watches for memory leaks
 *
 */
public abstract class BaseFragment extends Fragment {

    private Toast mToast;
    private Unbinder mUnbinder;
    protected CompositeSubscription mCompositeSubscriptions;

    @Inject StockRepository mStockRepository;

    @CallSuper @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Perform Dependency Injection when activity is attached to the fragment and is available
        ((StockHawkApplication)getActivity().getApplication()).getAppComponent().inject(this);
    }

    @CallSuper @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Get a reference to all the views in the rootView
        mUnbinder = ButterKnife.bind(this, rootView);
        mCompositeSubscriptions = new CompositeSubscription();
    }

    @CallSuper @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @CallSuper @Override
    public void onDestroyView() {
        // Release reference to all the views
        mUnbinder.unbind();
        mCompositeSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @CallSuper @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = StockHawkApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    protected void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
