package com.example.modernsms.broadcasts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.modernsms.R;

import java.util.Random;

public class MySmsBroadCastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        int nid = new Random().nextInt();
        Log.d("nid",""+nid);
        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        //int rand=0;
        if(msgs.length!=0){
            String strMessage="";
            String strAddress ="";
            for (SmsMessage msg : msgs) {
                strMessage += msg.getDisplayMessageBody();
                strAddress = msg.getOriginatingAddress();
            }
               showingNotification(strMessage,strAddress,context,nid);
        }
    }
    public static void showingNotification(String strMessage,String strAddress,Context context,int nid){
        final String CHANNEL_ID = "smsReceivedChannel";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HasanChannel";
            String description = "just for testing notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.cat)
                .setContentTitle(strAddress)
                .setContentText(strMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        //showing notification now
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(nid, builder.build());

    }
}
