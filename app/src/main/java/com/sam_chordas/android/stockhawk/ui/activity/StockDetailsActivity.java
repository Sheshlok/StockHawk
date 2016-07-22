package com.sam_chordas.android.stockhawk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.fragment.StockDetailsFragment;
import com.sam_chordas.android.stockhawk.utilities.Constants;
import com.sam_chordas.android.stockhawk.widget.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 09/07/16.
 * Shows stock details
 */
public class StockDetailsActivity extends BaseActivity {

    // For preserving state during rotation or any event when the app gets killed by the system
    private static final String SELECTED_STOCK_POSITION_STATE = "selected_stock_position_state";
    private static final int DEFAULT_STOCK_POSITION_STATE = 0;

    @BindView(R.id.tabbar_stock_symbol) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.indicator) InkPageIndicator mInkPageIndicator;

//    private String mSelectedStockSymbol;
//    private String mSelectedCompany;
    private Map<String, String> mCompanyInfoMap;
    private int mSelectedStockPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        mSelectedStockPosition = (savedInstanceState == null) ?
                getIntent().getExtras().getInt(Constants.SELECTED_STOCK_POSITION, DEFAULT_STOCK_POSITION_STATE) :
                savedInstanceState.getInt(SELECTED_STOCK_POSITION_STATE, DEFAULT_STOCK_POSITION_STATE);
        Timber.e("mSelectedStockPosition %s", mSelectedStockPosition);

        initializeScreen();

    }

    /* Provides up navigation to the same instance of the Parent Activity, with its state intact */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // Respond to action bar's Up/Home Button
            case android.R.id.home:
                NavUtils.navigateUpTo(this,
                        NavUtils.getParentActivityIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.e("Selected Stock Position: %s", mSelectedStockPosition);
        outState.putInt(SELECTED_STOCK_POSITION_STATE, mSelectedStockPosition);
        super.onSaveInstanceState(outState);
    }


    /**
     * Set up ViewPager, TabLayout, and Toolbar. Layout elements from XML are already linked
     * from 'setContentView' call to BaseActivity
     */
    private void initializeScreen() {

        /**
         * Create SectionPagerAdapter, set it as adapter to viewPager with setOffscreenPageLimit(2)
         **/
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());


        if (mViewPager != null) {
            /*
             * Set the number of pages that should be retained to either side of the
             * current page in the view hierarchy in an idle state. Pages beyond this
             * limit will be recreated from the adapter when needed. Set it to
             * 0 here since API does not respond positively to all of the (simultaneous) queries.
             */
            mViewPager.setOffscreenPageLimit(1);
            mViewPager.setAdapter(adapter);

            //-------- Attach the page change listener to keep the selectedStockPosition updated-------//

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                /**
                 * This method will be invoked when the current page is scrolled. Do nothing
                 */
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                /**
                 * This method will be invoked when a new page is selected. This helps us keep
                 * STATE across rotation by updating the selected Stock Position
                 */
                @Override
                public void onPageSelected(int position) {
//                Toast.makeText(StockDetailsActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                    mSelectedStockPosition = position;
                }

                /**
                 * Called when the scroll state changes: SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING,
                 * SCROLL_STATE_SETTLING. Do nothing
                 */
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //-------- Attach the page change listener to keep the selectedStockPosition updated-------//

            //--------------------Customizing page animations using PageTransformer support library --//

            mViewPager.setPageTransformer(true, new CubeOutTransformer());

            //--------------------Customizing page animations using PageTransformer support library --//
        }

        /**
         * Set up the tab layout with the view pager
         */
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
        }

        /**
         * Set up toolbar scrolling, title options etc.
         */
        ActionBar actionBar = getSupportActionBar();

        if (mToolbar != null) {
            AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
            layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);

            if (actionBar != null) {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        /** Set up TabLayout clickListeners to handle subsequent selections. Initial selection is
         * handled separately */

        final String[] selectedStockSymbol = new String[1];

        if (mTabLayout != null) {

            mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab != null) {
                        selectedStockSymbol[0] = tab.getText() != null ? tab.getText().toString() : "";
                        if (actionBar != null) {
                            actionBar.setSubtitle(selectedStockSymbol[0]);
                            actionBar.setTitle(mCompanyInfoMap.get(selectedStockSymbol[0]));
                        }
                        tab.select();
                        mViewPager.setCurrentItem(tab.getPosition(), true);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

    }

    /**
     * SectionPagerAdapter class that extends FragmentStatePagerAdapter to save fragments state
     */
    public class SectionPagerAdapter extends SmartFragmentStatePagerAdapter<StockDetailsFragment> {
        ArrayList<String> mStockSymbolsList = new ArrayList<>();

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            mCompositeSubscriptions.add(mStockRepository.getSavedCompanies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(companyInfoMap -> {
                        mCompanyInfoMap = companyInfoMap;
                        Iterator entries = mCompanyInfoMap.entrySet().iterator();
                        while (entries.hasNext()) {
                            Map.Entry entry = (Map.Entry) entries.next();
                            mStockSymbolsList.add((String) entry.getKey());
                        }
                        notifyDataSetChanged();
                        setUpInitialSelection();
                    }, throwable -> {
                        Timber.e("CompanyInfo Loading Failed");
                    }, () -> {
                        Timber.i("CompanyInfo Loading succeded");
                    }));
        }

        @Override
        public Fragment getItem(int position) {
            return StockDetailsFragment.newInstance(mStockSymbolsList.get(position).toLowerCase());
        }

        @Override
        public int getCount() {
            return mStockSymbolsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStockSymbolsList.get(position);
        }

    }

    /** Set up initial selection after the adapter has data*/
    private void setUpInitialSelection() {
        // Set up ink page indicators
        mInkPageIndicator.setViewPager(mViewPager);

        if (mTabLayout != null) {

            ActionBar actionBar = getSupportActionBar();
            final String[] selectedStockSymbol = new String[1];
            TabLayout.Tab initialTab = mTabLayout.getTabAt(mSelectedStockPosition);
            if (initialTab != null) {
                selectedStockSymbol[0] = initialTab.getText() != null ? initialTab.getText().toString() : "";

                if (actionBar != null) {
                    actionBar.setSubtitle(selectedStockSymbol[0]);
                    actionBar.setTitle(mCompanyInfoMap.get(selectedStockSymbol[0]));
                }
                initialTab.select();
            }
        }
    }
}