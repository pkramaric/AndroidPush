package com.flybits.samples.pushnotifications;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

public class PushApplication extends Application{

    @Override
    public void onCreate() {
        FlybitsOptions builder = new FlybitsOptions.Builder(this)
                //Additional Options Can Be Added Here
                .setDebug(true)
                .enablePushNotifications(FlybitsOptions.GCMType.WITH_GOOGLE_SERVICES_JSON, "1059783408302")
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(builder);
    }

}
