package com.sam_chordas.android.stockhawk.ui.touch_helper;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 * This class enables swipe to delete in RecyclerView.
 *
 * ItemTouchHelper is a utility class that adds 1. Swipe to dismiss, 2. Drag & Drop support to
 * RecyclerView. Helps manipulate UI as per user's whims and desires.
 *
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter mAdapter;
    public static final float ALPHA_FULL = 1.0f;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    //---------------------Main Helpers for drag/drop and swipe-to-dismiss--------------------------
    /**
     *
     * Alternatives for these Helpers:
     * 1. isLongPressDragEnabled: ItemTouchHelper.startDrag(RecyclerView.ViewHolder)
     * can be called to start a drag from a “handle.”
     *
     * 2. isItemViewSwipeEnabled: ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) can be
     * called to start a drag manually.
     *
     */

    // Enables drag/drop on long press
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // Enables swipe to dismiss
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    //---------------------Main Helpers for drag/drop and swipe-to-dismiss--------------------------

    //----------Main Callbacks we must override to enable basic drag/drop/swipe-to-dismiss---------

    /**
     * Gets a composite flag which defines the enabled move directions for each state, i.e. idle/drag/swipe.
     * We are enabling dragging and swiping in both directions here
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    /** Called when the itemTouchHelper wants to move the dragged items from old position to new position.
     * If it returns true, the itemTouchHelper assumes the viewHolder has been moved from the source
     * to the target position
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder sourceViewHolder,
                          RecyclerView.ViewHolder targetViewHolder) {
        if (sourceViewHolder.getItemViewType() != targetViewHolder.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(sourceViewHolder.getAdapterPosition(), targetViewHolder.getAdapterPosition());
        return true;
    }



    /** When swiped, dismiss items.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

        // Notify the adapter of the dismissal
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //----------Main Callbacks we must override to enable basic drag/drop/swipe-to-dismiss---------



    //-------------------------------------Other helpers--------------------------------------------
    /** Called when the selected VH - one that is swiped/dragged - by the itemTouchHelper is changed.
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {

                // Lets the viewHolder know that this item is being moved/dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * Called by the itemTouchHelper when the user interaction with the element is over.
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {

            // Tell the viewHolder that its time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    //-------------------------------------Other helpers--------------------------------------------

    //---------------------overriding the default swipe animation to show a linear fade------------
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

}
