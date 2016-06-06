package com.flybits.samples.pushnotifications;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestGeneralCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.samples.pushnotifications.services.FlybitsGCMListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ArrayList<String> listOfPushNotifications;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog          = new ProgressDialog(this);

        Button btnLogin         = (Button) findViewById(R.id.btnLogin);
        Button btnLogout        = (Button) findViewById(R.id.btnLogout);
        ListView listView       = (ListView) findViewById(R.id.listOfNotification);

        listOfPushNotifications = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfPushNotifications);
        listView.setAdapter(adapter);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setProgressBar("Logging Out ...", false);

                Flybits.include(MainActivity.this).logout(new IRequestGeneralCallback() {
                    @Override
                    public void onSuccess() {
                        Snackbar.make(v, "Successfully Logged Out!", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Exception e) {
                        Snackbar.make(v, "Something went wrong! ", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed(String s) {

                    }

                    @Override
                    public void onCompleted() {
                        stopProgressBar();
                    }
                });
            }
        });

        IntentFilter filter = new IntentFilter(FlybitsGCMListener.MSG_RECEIVED);
        registerReceiver(gcmReceiver, filter);
    }

    BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null){
                String msg = extras.getString(FlybitsGCMListener.EXTRA_MSG);
                listOfPushNotifications.add(0, msg);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public void login(final View v){

        setProgressBar("Logging In...", false);
        Flybits.include(MainActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
            @Override
            public void onLoggedIn(User user) {
                Snackbar.make(v, "You are already logged in!", Snackbar.LENGTH_LONG).show();
                stopProgressBar();
            }

            @Override
            public void onNotLoggedIn() {
                LoginOptions options = new LoginOptions.Builder(MainActivity.this)
                        .loginAnonymously()
                        .setRememberMeToken()
                        .setDeviceOSVersion()
                        .build();

                Flybits.include(MainActivity.this).login(options, new IRequestCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Snackbar.make(v, "You have successfully logged in!", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Exception e) {

                    }

                    @Override
                    public void onFailed(String s) {
                        Snackbar.make(v, "Something went wrong! " + s, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCompleted() {
                        stopProgressBar();
                    }
                });
            }
        });
    }

    private void setProgressBar(String text, boolean isCancelable) {
        progressDialog.setCancelable(isCancelable);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
        progressDialog.show();
        progressDialog.setMessage(text);
    }

    private void stopProgressBar() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {}
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(gcmReceiver);
        super.onDestroy();
    }
}
