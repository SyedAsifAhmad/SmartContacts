package com.asifahmad.smartcontacts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ContactScheduler {
    public static void scheduleContactDeletion(Context context, int contactId, long triggerTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DeleteContactReceiver.class);
        intent.putExtra("contact_id", contactId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, contactId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );


        Log.d("AlarmManager", "Scheduling for " + contactId + " at " + new java.util.Date(triggerTime));


        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

}

