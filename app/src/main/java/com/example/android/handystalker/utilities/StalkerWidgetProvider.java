package com.example.android.handystalker.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.handystalker.R;
import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.ui.RulesActivity;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 *
 * Code for SMS used from
 * https://google-developer-training.gitbooks.io/android-developer-phone-sms-course/content/Lesson%202/2_p_sending_sms_messages.html
 */
public class StalkerWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String arrivalPlace, ArrayList<Contact> mContacts) {

        // Construct the RemoteViews object
        String title;
        if (arrivalPlace != null) {
            title = context.getString(R.string.reached) + " " + arrivalPlace;} else
        { title = context.getString(R.string.go_to_app);}
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
                    String messageUser = "Let " + name + " know that you are safe.";
                    mEventsItem.setTextViewText(R.id.widget_item_textView, messageUser);

                    //Create an Intent to launch WhatsAppActivity when clicked
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    String text = context.getString(R.string.text_message);
                    String contactPhone = contact.getPhone();
                    if (contactPhone.substring(0,1).contains("+")){
                        String contactPhoneWA = contactPhone.substring(1);
                        intent2.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + contactPhoneWA +"&text=" + text));

                            PendingIntent waPendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            mEventsItem.setViewVisibility(R.id.wapp_widget_button, View.VISIBLE);
                            mEventsItem.setOnClickPendingIntent(R.id.wapp_widget_button, waPendingIntent);

                    } else {
                        mEventsItem.setViewVisibility(R.id.wapp_widget_button, View.GONE);
                    }

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contactPhone));


                    // Create the intent.
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    // Set the data for the intent as the phone number.
                    String smsNumber = String.format("smsto: %s",
                            contactPhone);
                    smsIntent.setData(Uri.parse(smsNumber));
                    // Add the message (sms) with the key ("sms_body").
                    smsIntent.putExtra("sms_body", text);


                    //Widgets allow click handlers to only launch pending intents

                        PendingIntent callPendingIntent = PendingIntent.getActivity(context, (a*10+1), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent smsPendingIntent = PendingIntent.getActivity(context, (a*20+1), smsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        mEventsItem.setOnClickPendingIntent(R.id.call_widget_button, callPendingIntent);
                        mEventsItem.setOnClickPendingIntent(R.id.sms_widget_button, smsPendingIntent);
                        mEventsItem.setViewVisibility(R.id.call_widget_button, View.VISIBLE);
                        mEventsItem.setViewVisibility(R.id.sms_widget_button, View.VISIBLE);
                        views.addView(R.id.stalker_actions_widget_linear_layout, mEventsItem);

                    a++;
                }
            }
        } else {
            Log.d("widget", "This fragment has a null list of events");
        }

        //Create an Intent to launch the app when clicked
        Intent intent = new Intent(context, RulesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //Create pending intent - Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateStalkersWidgets(Context context, AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds, String arrivalPlace, ArrayList<Contact> mContacts) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, arrivalPlace, mContacts);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        StalkerWidgetService.startActionUpdateWidgets(context);
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

