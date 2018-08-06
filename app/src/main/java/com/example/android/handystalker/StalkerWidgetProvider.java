package com.example.android.handystalker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.ui.MainActivity;
import com.example.android.handystalker.ui.NewRuleActivity;
import com.example.android.handystalker.ui.StalkerService;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class StalkerWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String arrivalPlace, List<Contact> mContacts) {

        // Construct the RemoteViews object
        String title;
        if (arrivalPlace != null) {
            title = context.getString(R.string.reached) + " " + arrivalPlace;} else
        { title = context.getString(R.string.add_new_rule);}
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stalker_widget);
        views.setTextViewText(R.id.appwidget_text, title);

        if (mContacts != null) {
            views.removeAllViews (R.id.stalker_actions_widget_linear_layout);
            Log.d("contact not null", "false");
            int a = 1;
            for (final Contact contact : mContacts) {
                if (contact != null) {
                    Log.d("contact not null", "true");

                    String name = contact.getName();
                    RemoteViews mEventsItem = new RemoteViews(context.getPackageName(), R.layout.widget_item);
                    String messageUser = "Let " + name + " know you are safe.";
                    mEventsItem.setTextViewText(R.id.widget_item_textView, messageUser);

                    //Create an Intent to launch WhatsAppActivity when clicked
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    String text = "Hi, I arrived safely!";
                    String toNumber = contact.getPhone();
                    intent2.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, a, intent2, 0);
                    a++;

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(contact.getPhone()));
                    PendingIntent callPendingIntent = PendingIntent.getActivity(context, a, callIntent, 0);
                    a++;

                    Intent smsIntent = new Intent(Intent.ACTION_SEND);
                    smsIntent.setData(Uri.parse("smsto:" + contact.getPhone())); // This ensures only SMS apps respond
                    smsIntent.putExtra("sms_body", text);
                    PendingIntent smsPendingIntent = PendingIntent.getActivity(context, a, smsIntent, 0);


                    //Widgets allow click handlers to only launch pending intents
                    views.setOnClickPendingIntent(R.id.wapp_widget_button, pendingIntent2);
                    views.setOnClickPendingIntent(R.id.call_widget_button, callPendingIntent);
                    views.setOnClickPendingIntent(R.id.sms_widget_button, smsPendingIntent);
                    a++;
                }
            }
        } else {
            Log.d("widget", "This fragment has a null list of events");
        }

        //Create an Intent to launch NewRuleActivity when clicked
        Intent intent = new Intent(context, NewRuleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateStalkersWidgets(Context context, AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds, String arrivalPlace, List<Contact> mContacts) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, arrivalPlace, mContacts);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        StalkerService.startActionUpdateWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

