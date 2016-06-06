package com.flybits.samples.pushnotifications.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flybits.samples.pushnotifications.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

public class FlybitsGCMListener extends GcmListenerService {

    public final static String MSG_RECEIVED = "PUSH_RECEIVED";
    public final static String EXTRA_MSG    = "MSG_EXTRA";

    public static final String NOTIFICATION_MSG     = "Action.Notification.Msg";
    public static final String NOTIFICATION_HEADER  = "Action.Notification.Header";
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "Received GCMBroadcast: " + data);

        Gson myGson = new Gson();
        PushBody push  = myGson.fromJson(data.getString("body"), PushBody.class);

        Intent broadcastIntent = new Intent(MSG_RECEIVED);
        broadcastIntent.putExtra(EXTRA_MSG, push.message);
        sendBroadcast(broadcastIntent);

        setNotification(getApplicationContext(), "Header Text", push.message);
    }

    public class PushBody{
        public String message;
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

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(heading)
                        .setContentText(message)
                        .setSmallIcon(iconDrawable)
                        .setVibrate(vibration)
                        .setAutoCancel(true);


        mNotificationManager.notify(0, mBuilder.build());
    }

}