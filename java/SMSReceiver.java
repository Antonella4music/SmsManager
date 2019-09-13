package com.example.antonellab.sms_app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by AntonellaB on 09-Nov-16.
 */

public class SMSReceiver extends BroadcastReceiver
{
    String PHONE = "phone";
    int RECEIVE_SMS = 2;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdusObj.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                saveMessage(messages[i].getOriginatingAddress(), messages[i].getMessageBody(), RECEIVE_SMS, messages[i].getTimestampMillis(), context);
                fireNotification(context, messages[i]);
            }
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void fireNotification(Context context, SmsMessage messages) {
        int notifyId = 2;
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentTitle(messages.getOriginatingAddress())
                        .setContentText(messages.getMessageBody());
        mBuilder.setAutoCancel(true);

        int numMessages = 0;

        mBuilder.setContentText(messages.getMessageBody())
                .setNumber(++numMessages);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(messages.getOriginatingAddress());
        int chunksize = 25;
        String[] arrayMessage = splitMessage(messages.getMessageBody(), chunksize, messages.getMessageBody().length() + 1);

        for (String line: arrayMessage) {
            inboxStyle.addLine(line);
        }

        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, Conversation.class);
        resultIntent.putExtra(PHONE, messages.getOriginatingAddress());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Conversation.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    //split message in chunks
    public static String[] splitMessage(String message, int chunkSize, int maxLength) {
        int line = 0;
        char[] data = message.toCharArray();
        int length = Math.min(data.length,maxLength);
        String[] myMessage = new String[(length+chunkSize-1)/chunkSize];

        for (int i=0; i < Math.min(data.length,maxLength); i+=chunkSize) {
            myMessage[line] = new String(data, i, Math.min(chunkSize,length-i));
            line++;
        }
        return myMessage;
    }

    public static void saveMessage(String phone, String message, Integer type, long timestamp, Context context){
        MessagesDBHelper dbHelper = new MessagesDBHelper(context);
        dbHelper.insertSms(message,phone, type, timestamp);
    }

}