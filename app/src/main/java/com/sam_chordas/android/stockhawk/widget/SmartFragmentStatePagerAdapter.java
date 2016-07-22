package com.sam_chordas.android.stockhawk.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by sheshloksamal on 20/07/16.
 * https://gist.github.com/nesquena/c715c9b22fb873b1d259
 *
 * Extension of FragmentStatePagerAdapter which intelligently cachesall active fragments and manages
 * the fragment life-cycles. Usage involves extending from SmartFragmentStatePagerAdapter as you would
 * any other PagerAdapter.
 * This is more useful when we may need a dynamic ViewPager, where we want to get access to fragment
 * instances or with pages being added or removed at runtime.
 *
 * Now with this adapter in place, we can also easily access any fragments within the
 * {@link android.support.v4.view.ViewPager} with:
 * (NOTE: With generic type version below, we don't need to cast the returned fragment)
 *
 * adapterViewPager.getRegisteredFragment(0); //returns 1st fragment item within the pager
 *
 * &
 *
 * we can "easily" access the current fragment/pager item with:
 *
 * adapterViewPager.getRegisteredFragment(vpPager.getCurrentItem()); // returns current fragment item displayed within the pager
 *
 * This pattern should save your app quite a deal of memory and allow for much easier management of
 * fragments within your pager for the right situation.
*/
public abstract class SmartFragmentStatePagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    // Sparse array to keep track of registered fragments in memory
    private SparseArray<T> registeredFragments = new SparseArray<T>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        T fragment = (T) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public T getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}