package com.flybits.samples.pushnotifications.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FlybitsFirebaseReceiver extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message){

        Log.d("Testing", "FlybitsFirebaseReceiver->Received Message");
        String from = message.getFrom();
        Map data = message.getData();
    }
}
