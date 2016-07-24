package com.sam_chordas.android.stockhawk.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.provider.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.provider.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.touch_helper.ItemTouchHelperAdapter;
import com.sam_chordas.android.stockhawk.ui.touch_helper.ItemTouchHelperViewHolder;
import com.sam_chordas.android.stockhawk.utilities.DbUtils;
import com.sam_chordas.android.stockhawk.utilities.JsonCVUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */

public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    @NonNull  private final Context mContext;
    @NonNull  private final LayoutInflater mLayoutInflater;
    private static Typeface robotoLight;
    private boolean isPercent;

    public QuoteCursorAdapter(@NonNull Context context, Cursor cursor, View emptyView) {
        super(context, cursor, emptyView);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = mLayoutInflater.inflate(R.layout.list_item_quote, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        viewHolder.bind(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        @BindView(R.id.stock_symbol) TextView symbol;
        @BindView(R.id.company_name) TextView companyName;
        @BindView(R.id.bid_price) TextView bidPrice;
        @BindView(R.id.change) TextView change;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull Cursor cursor) {
            symbol.setTypeface(robotoLight);
            symbol.setText(cursor.getString(cursor.getColumnIndex("symbol")));
            companyName.setTypeface(robotoLight);
            companyName.setText(cursor.getString(cursor.getColumnIndex("company_name")));

            bidPrice.setText(cursor.getString(cursor.getColumnIndex("bid_price")));
            if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation for 'setBackgroundDrawable'
                    change.setBackgroundDrawable(
                            mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
                } else {
                    //noinspection deprecation for 'getDrawable'
                    change.setBackground(
                            mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation for 'setBackgroundDrawable'
                    change.setBackgroundDrawable(
                            mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
                } else {
                    //noinspection deprecation for 'getDrawable'
                    change.setBackground(
                            mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
                }
            }
            if (JsonCVUtils.showPercent) {
                change.setText(cursor.getString(cursor.getColumnIndex("percent_change")));
            } else {
                change.setText(cursor.getString(cursor.getColumnIndex("change")));
            }

        }

        //----------------- Implementation of ItemTouchHelperViewHolder --------------------------
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        //---------------------------------------------------------------------------------------

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    // Implementation of ItemTouchHelperAdapter
    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = DbUtils.getString(c, QuoteColumns.SYMBOL);
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
    }

}
