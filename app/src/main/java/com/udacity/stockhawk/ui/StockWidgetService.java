package com.udacity.stockhawk.ui;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Joachim on 15/05/2017.
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetViewFactory(getApplicationContext());
    }

    private class StockWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context mAppContext;
        private final DecimalFormat dollarFormat, dollarFormatWithPlus, percentageFormat;
        private Cursor data;

        public StockWidgetViewFactory(Context appContext) {
            mAppContext = appContext;

            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");
        }

        @Override
        public void onCreate() {
            getData();
        }

        public void getData() {
            long identity = Binder.clearCallingIdentity();
            try {

                data = mAppContext.getContentResolver().query(
                        Contract.Quote.URI,
                        null,
                        null,
                        null,
                        null
                );


                Log.d("Service", DatabaseUtils.dumpCursorToString(data));


            } finally {
                Binder.restoreCallingIdentity(identity);
            }

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            data.close();
        }

        @Override
        public int getCount() {
            return data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            data.moveToPosition(position);
            Log.d("CURSORDUMP", DatabaseUtils.dumpCursorToString(data));

            RemoteViews views = new RemoteViews(mAppContext.getPackageName(), R.layout.list_item_quote);

            String symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
            float absChange = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            float perChange = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
            float price = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));


            views.setTextViewText(R.id.symbol, symbol);
            views.setTextViewText(R.id.price, dollarFormat.format(price));


            if (absChange > 0) {
                views.setInt(R.id.change, "setBackgroundresource", R.drawable.percent_change_pill_green);
            } else {
                views.setInt(R.id.change, "setBackgroundresource", R.drawable.percent_change_pill_red);
            }

            views.setTextViewText(R.id.change, percentageFormat.format(perChange / 100));

             /*views.setTextViewText(R.id.symbol, "Tester");
            views.setTextViewText(R.id.price, "55");
            views.setTextViewText(R.id.change, "20");*/

            Intent fillIntent = new Intent();

            fillIntent.putExtra("symbol", symbol);

            views.setOnClickFillInIntent(R.id.list_item_stock, fillIntent);

            Log.d("RemoteViews", views.toString());

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
