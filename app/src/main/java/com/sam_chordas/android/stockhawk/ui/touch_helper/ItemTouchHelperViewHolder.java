package com.sam_chordas.android.stockhawk.ui.touch_helper;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 *
 * Notifies a View Holder of relevant callbacks from
 * {@link android.support.v7.widget.helper.ItemTouchHelper.Callback}.
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper}
     * first registers an item as being moved or swiped.
     * Implementations should update the itemView to indicate it's active state
     */
    void onItemSelected();


    /**
     * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper}
     * has completed the move or swipe, and the active item state should be cleared.
     */
    void onItemClear();
}
