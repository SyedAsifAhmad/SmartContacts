package com.asifahmad.smartcontacts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class DeleteContactReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int contactId = intent.getIntExtra("contact_id", -1);
        if (contactId != -1) {
            Log.d("DeleteContactReceiver", "Received alarm to delete contact: " + contactId);

            // ✅ Perform the delete operation on a background thread using AsyncTask
            new DeleteContactTask(context, contactId).execute();
        }
    }

    private static class DeleteContactTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final int contactId;

        DeleteContactTask(Context context, int contactId) {
            this.context = context;
            this.contactId = contactId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContactDatabase contactDatabase = ContactDatabase.getInstance(context);
            ContactDao contactDao = contactDatabase.contactDao();
            contactDao.deleteById(contactId);
            Log.d("DeleteContactReceiver", "Deleted contact with ID: " + contactId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent updateIntent = new Intent("CONTACT_DELETED");
            context.sendBroadcast(updateIntent);

            // ✅  Send notification
            sendDeleteNotification(context, contactId);
        }

        private void sendDeleteNotification(Context context, int contactId) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("delete_channel", "Deleted Contacts", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "delete_channel")
                    .setSmallIcon(R.drawable.notification).setContentTitle("Contact Deleted")
                    .setContentText("Your scheduled contact has been deleted.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent).setAutoCancel(true);
            notificationManager.notify(contactId, builder.build());


        }
    }
}
