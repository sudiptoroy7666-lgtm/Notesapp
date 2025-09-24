package com.example.notesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("note_channel", "Note Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create PendingIntent with FLAG_IMMUTABLE for security (recommended if no need to modify PendingIntent)
        Intent notificationIntent = new Intent(context, MainActivity.class); // Replace with the activity you want to open
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE here

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "note_channel")
                .setSmallIcon(R.drawable.nf)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Notify the user (using noteId to create a unique notification ID)
        int notificationId = intent.getStringExtra("noteId").hashCode(); // Ensure noteId is unique
        notificationManager.notify(notificationId, builder.build());
    }
}
