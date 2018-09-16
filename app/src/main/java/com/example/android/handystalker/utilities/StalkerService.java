package com.example.android.handystalker.utilities;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.handystalker.R;
import com.example.android.handystalker.model.Contact;

import java.util.ArrayList;

public class StalkerService extends IntentService {

    private static final String NAME = "com.example.android.handystalker.ui.name";
    private static final String EVENTS = "com.example.android.handystalker.events";

    public StalkerService() {
        super("StalkerService");
    }


    public static void startActionAddEvents(Context context, String name, ArrayList<Contact> contacts) {
        Log.d("Start Widget", "widget started");
        Intent intent = new Intent(context, StalkerService.class);
        intent.putExtra(NAME, name);
        intent.putParcelableArrayListExtra(EVENTS, contacts);
        context.startService(intent);
    }

    /**
     * Starts this service to perform UpdateWidgets action with the given parameters.
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, StalkerService.class);
        Log.d("Start Widget", "Update");
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ArrayList<Contact> contacts;
            contacts = intent.getParcelableArrayListExtra(EVENTS);
            String name = intent.getStringExtra(NAME);
            handleActionUpdateWidgets(name, contacts);
        }
    }

    /**
     * Handle action UpdateWidgets in the provided background thread
     */
    private void handleActionUpdateWidgets(String arrivalPlace, ArrayList<Contact> contacts) {

        Log.d("Start Widget", "handleActionUpdate");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StalkerWidgetProvider.class));
        //Trigger data update
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stalker_actions_widget_linear_layout);
        //Now update all widgets
        StalkerWidgetProvider.updateStalkersWidgets(this, appWidgetManager, appWidgetIds, arrivalPlace, contacts);
    }
}