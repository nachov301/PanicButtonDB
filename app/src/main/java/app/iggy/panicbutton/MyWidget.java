package app.iggy.panicbutton;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidget extends AppWidgetProvider {
    private static final String TAG = "MyWidget";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: starts");

// code for launching MainActivity
//        for(int j = 0; j < appWidgetIds.length; j++)
//        {
//            int appWidgetId = appWidgetIds[j];
//
//            try {
//                Intent intent = new Intent("android.intent.action.MAIN");
//                intent.addCategory("android.intent.category.LAUNCHER");
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.setComponent(new ComponentName(context.getPackageName(),
//                        MainActivity.class.getName()));
//                PendingIntent pendingIntent = PendingIntent.getActivity(
//                        context, 0, intent, 0);
//                RemoteViews views = new RemoteViews(context.getPackageName(),
//                        R.layout.my_widget);
//                views.setOnClickPendingIntent(R.id.btn_open_app, pendingIntent);
//                appWidgetManager.updateAppWidget(appWidgetId, views);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(context.getApplicationContext(),
//                        "There was a problem loading the application: ",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        }



        for(int j = 0; j < appWidgetIds.length; j++)
        {
            int appWidgetId = appWidgetIds[j];

            try {
                Intent intent = new Intent(context, WidgetActivity.class);
                intent.addCategory("android.intent.category.LAUNCHER");

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setComponent(new ComponentName(context.getPackageName(),
                        WidgetActivity.class.getName()));
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.my_widget);
                views.setOnClickPendingIntent(R.id.btn_open_app, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

}