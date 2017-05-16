package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Created by Joachim on 15/05/2017.
 */

public class StockWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.stock_widgets);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);
            views.setRemoteAdapter(R.id.widget_list, new Intent(context, StockWidgetService.class));

            Intent clickedIntent = new Intent(context, StockDetailedActivity.class);
            PendingIntent pIndent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickedIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, pIndent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
