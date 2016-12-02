package com.flybits.samples.pushnotifications.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.exceptions.FlybitsPushException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.models.Push;
import com.flybits.samples.pushnotifications.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.flybits.samples.pushnotifications.services.FlybitsGCMListener.EXTRA_MSG;
import static com.flybits.samples.pushnotifications.services.FlybitsGCMListener.MSG_RECEIVED;

public class FlybitsFirebaseReceiver extends FirebaseMessagingService {

    private final String _TAG = "FlybitsFirebaseReceiver";

    @Override
    public void onMessageReceived(RemoteMessage message){

        Log.d(_TAG, "FlybitsFirebaseReceiver->Received Message: " + message.getData());
        String from = message.getFrom();
        Map data = message.getData();

        try {
            Flybits.include(getBaseContext()).parseFCMPushNotification(data, null, new IRequestCallback<Push>() {
                @Override
                public void onSuccess(Push push) {

                    Intent broadcastIntent = new Intent(MSG_RECEIVED);
                    broadcastIntent.putExtra(EXTRA_MSG, push.alert);
                    String title    = (push.title != null)? push.title : "SOME_TITLE";
                    String message  = (push.alert != null)? push.alert: "SOME_ALERT";
                    setNotification(getApplicationContext(), title, message);
                    sendBroadcast(broadcastIntent);
                }

                @Override
                public void onException(Exception e) {

                }

                @Override
                public void onFailed(String s) {

                }

                @Override
                public void onCompleted() {

                }
            });
        }catch (FlybitsPushException e){
            Log.d("Testing", e.getLocalizedMessage());
        }

    }


    private void setNotification(Context context, String heading, String message) {

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        int iconDrawable = useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;

        //Incase null to avoid crash
        if (message == null)
            message = "";

        long[] vibration = new long[1];
        vibration[0] = 100L;

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(heading);
        bigText.setSummaryText("Notification Received");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(heading)
                        .setContentText(message)
                        .setSmallIcon(iconDrawable)
                        .setVibrate(vibration)
                        .setStyle(bigText)
                        .setAutoCancel(true);


        mNotificationManager.notify(0, mBuilder.build());
    }
}
