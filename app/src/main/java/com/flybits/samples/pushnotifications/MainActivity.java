package com.flybits.samples.pushnotifications;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog          = new ProgressDialog(this);

        Button btnLogin         = (Button) findViewById(R.id.btnLogin);
        Button btnLocation1     = (Button) findViewById(R.id.btnEnglandLocation);
        Button btnLocation2     = (Button) findViewById(R.id.btnTorontoLocation);
        ListView listView       = (ListView) findViewById(R.id.listOfNotification);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        
        btnLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void login(final View v){

        setProgressBar("Logging In...", false);
        Flybits.include(MainActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
            @Override
            public void onLoggedIn(User user) {
                Snackbar.make(v, "You are already logged in!", Snackbar.LENGTH_LONG).show();
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

}
