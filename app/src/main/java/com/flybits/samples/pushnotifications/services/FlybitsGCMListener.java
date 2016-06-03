package com.flybits.samples.pushnotifications.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.exceptions.FlybitsDisabledException;
import com.flybits.core.api.exceptions.FlybitsPushException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.models.Push;
import com.flybits.core.api.models.v1_5.internal.Result;
import com.flybits.core.api.utils.Utilities;
import com.flybits.core.api.utils.http.GetRequest;
import com.flybits.samples.pushnotifications.MainActivity;
import com.flybits.samples.pushnotifications.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import java.io.IOException;

public class FlybitsGCMListener extends GcmListenerService {

    public static final String NOTIFICATION_MSG     = "Action.Notification.Msg";
    public static final String NOTIFICATION_HEADER  = "Action.Notification.Header";
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "Received GCMBroadcast: " + data);
        try {
            Flybits.include(getBaseContext()).parseGCMPushNotification(data, MomentMessage.class, new IRequestCallback<Push>() {
                @Override
                public void onSuccess(Push pushReceived) {

                    if (pushReceived != null && pushReceived.body != null && pushReceived.body instanceof MomentMessage) {
                        MomentMessage momentMessage = (MomentMessage) pushReceived.body;
                        new MyTask(getBaseContext()).execute(momentMessage.url);
                    }
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
            Flybits.include(getBaseContext())._LOG_EXCEPTION(e);
        }
    }

    static class MomentMessage{
        public String url;
        public String zoneId;
        public String id;
    }

    private class MyTask extends AsyncTask<String, Integer, MomentPayload> {

        private Context context;

        public MyTask(Context context){
            this.context = context;
        }

        // This is run in a background thread
        @Override
        protected MomentPayload doInBackground(String... params) {

            try {
                Result result = new GetRequest(context, params[0], null).getResponse();
                if (result.status >= 200 && result.status < 300) {

                    String resultAsString = result.response;
                    return new Gson().fromJson(resultAsString, MomentPayload.class);
                }
            }catch (FlybitsDisabledException | IOException e){
                return null;
            }

            return null;
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(MomentPayload result) {
            super.onPostExecute(result);

            if (result != null && result.localizations != null ){
                if (result.localizations.en != null ) {

                    try {
                        String heading = Utilities.hmtlDecodeString(result.localizations.en.name.replace("\n", "<br />"));
                        String message = Utilities.hmtlDecodeString(result.localizations.en.message.replace("\n", "<br />"));
                        sendNotification(context, heading, message);
                    }catch (NullPointerException e){}
                }

                else if(result.localizations.fr != null){

                    try {
                        String heading = Utilities.hmtlDecodeString(result.localizations.fr.name.replace("\n", "<br />"));
                        String message = Utilities.hmtlDecodeString(result.localizations.fr.message.replace("\n", "<br />"));
                        sendNotification(context, heading, message);
                    }catch (NullPointerException e){}
                }
            }
        }
    }

    private void sendNotification(Context context, String heading, String message) {

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        int iconDrawable = useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;

        //Incase null to avoid crash
        if (message == null)
            message = "";

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_MSG, message);
        intent.putExtra(NOTIFICATION_HEADER, heading);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibration = new long[1];
        vibration[0] = 100L;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(heading)
                        .setContentText(message)
                        .setSmallIcon(iconDrawable)
                        .setVibrate(vibration)
                        .setAutoCancel(true);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    static class MomentPayload{
        public Localizations localizations;
    }

    static class Localizations{
        public Localization en;
        public Localization fr;

    }

    static class Localization{
        public String name;
        public String message;
    }
}