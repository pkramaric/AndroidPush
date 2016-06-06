# Push Notification Sample
This is a sample application that is designed to demonstrate the push notifications featured inside the Flybits SDK. Through this sample, a developer will gain an understanding on how best to integration their application with the Flybits SDK.

# Features
This sample demonstrates numerous Flybits push notifications features that allow application developers to fully understand the push capabilities provided by the SDK. Currently this sample is setup for receiving push notification, in the near future additional push functionality will be included such as preference setting. Additionally, comments have been included in the code itself with further explanation.

###Project Setup
Before writing any code it is important to make sure that your Flybits instance have been fully set up with GCM. Through the following steps application developers will be able to set up their GCM API key and sure sure that their application are configured correctly.

####1. Step 1: Log in to your developer account
The first step to integrating with Flybits' push notification system to access your account through Flybits' [Developer Portal](https://developer.flybits.com/signin.html). If you do not have an access make sure you sign up. Below is the screenshot representing this step.
![Step1](https://github.com/flybits/AndroidPush/blob/master/screenshots/step1.png)

####2. Step 2: Choose your desired project.
The second step to integrating is to choose the project that you want enable push notifications for. This can be done by selected the "View Details" links as seen below.
![Step2](https://github.com/flybits/AndroidPush/blob/master/screenshots/step2.png)

####3. Step 3: Choose your API Key.
The third step to integration is choose the appropriate API Key that corresponds to your Android Application. In most cases, when you select your project in Step 2, you will be 2 API Keys, one is for your Experience Studio and one is for your Mobile application. In order to enable push notification you will need to select the Mobile API Key and NOT the Experience Studio one. As seen in the example below make sure you select the "Setting" link to continue.
![Step3](https://github.com/flybits/AndroidPush/blob/master/screenshots/step3.png)

####4. Step 4: Add your Google GCM Server Key.
The final step to configuring push notifications within the SDK is to upload your Google GCM Server Key which can be obtained from Google GCM Configuration page as seen [here](https://developers.google.com/cloud-messaging/android/client#get-config). Once you have registered your application with Google and obtained your Server Key you can add to your Flybits application by selecting the "Edit" button under "GCM Settings". Once you have entered your GCM Server Key, you can save it as seen below.
![Step4](https://github.com/flybits/AndroidPush/blob/master/screenshots/step4.png)

###Mobile SDK Setup
The rest of this guide will be based on the Android Mobile SDK. It assumes that you have already setup your application with the SDK and have entered the proper API Key that corresponds to the Project from the previous section. If you have not done this it is highly recommended that you take a look at our [Developer Portal](https://developer.flybits.com/android-getting-started.html#setup) to gain a better understand on the initial steps of setting up the Flybits SDK.

####1. Step 1: Enable Push Notifications in the SDK.

Assuming that you have successfully set up the Flybits SDK, let us add the necessary features for Push Notifications. The first step is to enable push notifications when initializing your Flybits SDK. An example of this can be seen in the [PushApplication](../master/app/src/main/java/com/flybits/samples/pushnotifications/PushApplication.java) class, or below:

```
FlybitsOptions builder = new FlybitsOptions.Builder(this)
    //Additional Options Can Be Added Here
    .setDebug(true)
    .enablePushNotifications(FlybitsOptions.GCMType.WITH_GOOGLE_SERVICES_JSON, "1059783408302")
    .build();

//Initialize the FlybitsOptions
Flybits.include(this).initialize(builder);
```

Notice "1059783408302" which represents the application id of the application you registered with in Step 4 of the previous section.

It is important to note that if you correctly followed the steps described in Google's GCM Notification [Guide](https://developers.google.com/cloud-messaging/android/client#get-config) that you have also added the google-services.json file to your Flybits enabled project.

####2. Step 2: Adding Flybits AndroidManifest.xml components.
In order to fully enable your application with GCM push notification there are a few [AndroidManifest](../master/app/src/main/AndroidManifest.xml) components that need to be added. This components are very similar to the ones described in Google's [Guide](https://developers.google.com/cloud-messaging/android/client#get-config), however there are a few changes that need to be made as seen below. It is also recommended that you check out the [AndroidManifest](../master/app/src/main/AndroidManifest.xml) for better insight.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.flybits.samples.context" >

    <!-- Permissions -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="{{yourPackageName}}.permission.C2D_MESSAGE" />
    <permission android:name="{{yourPackageName}}.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    ...
    <application... >
        ...
       <service
            android:name="com.flybits.core.api.services.gcm.FlybitsGCMTokenListenerService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.android.gms.iid.InstanceID" />
            </intent-filter>
        </service>
        <service android:name="com.flybits.core.api.services.gcm.FlybitsGCMRegistrationIntentService"/>

        <!-- This would your own GCM Listener class -->
        <service
            android:name=".services.FlybitsGCMListener"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            </intent-filter>
        </service>

        <receiver
            android:name="com.google.android.gms.gcm.GcmReceiver"
            android:exported="true"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="{{yourPackageName}}" />
            </intent-filter>
        </receiver>

        <service android:name="com.flybits.core.api.services.PushService" />
        ...
    </application>

</manifest>
```

####3. Step 3: Setting up the GCM Listener.
The final step to this guide is setting up your GCM listener to be able to receive push notification. A full example of this can be seen in the [FlybitsGCMListener](../master/app/src/main/java/com/flybits/samples/pushnotifications/services/FlybitsGCMListener.java) class. Below you can find a sample:

```
public class FlybitsGCMListener extends GcmListenerService {
   ...
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "Received GCMBroadcast: " + data);

        Gson myGson = new Gson();
        PushBody push  = myGson.fromJson(data.getString("body"), PushBody.class);

        Intent broadcastIntent = new Intent(MSG_RECEIVED);
        broadcastIntent.putExtra(EXTRA_MSG, push.message);
        sendBroadcast(broadcastIntent);

        //setNotification(getApplicationContext(), "Header Text", push.message);
    }

    public class PushBody{
        public String message;
    }
    ...
}
```