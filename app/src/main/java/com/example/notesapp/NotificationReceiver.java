package com.example.notesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "onReceive called");

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String noteId = intent.getStringExtra("noteId");

        Log.d("NotificationReceiver", "Title: " + title + ", Content: " + content);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "note_channel",
                    "Note Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for note reminders");
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                noteId.hashCode(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "note_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title != null ? title : "Note Reminder")
                .setContentText(content != null ? content : "You have a note reminder")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            notificationManager.notify(noteId.hashCode(), builder.build());
            Log.d("NotificationReceiver", "Notification sent successfully");
        } catch (SecurityException e) {
            Log.e("NotificationReceiver", "SecurityException: " + e.getMessage());
        }
    }
}

