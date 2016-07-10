package com.flybits.samples.pushnotifications;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.samples.pushnotifications.fragments.HomeFragment;
import com.flybits.samples.pushnotifications.fragments.PushHistoryFragment;
import com.flybits.samples.pushnotifications.fragments.PushPreferenceFragment;
import com.flybits.samples.pushnotifications.interfaces.IProgressDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IProgressDialog {

    private ProgressDialog mProgressDialog;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog     = new ProgressDialog(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loginToFlybits();

        Fragment fragment = HomeFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit();
    }

    private void loginToFlybits() {

        Flybits.include(MainActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
            @Override
            public void onLoggedIn(User user) {
                isLoggedIn = true;
            }

            @Override
            public void onNotLoggedIn() {
                LoginOptions options = new LoginOptions.Builder(MainActivity.this)
                        .loginAnonymously()
                        .setDeviceOSVersion()
                        .build();

                Flybits.include(MainActivity.this).login(options, new IRequestCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        isLoggedIn = true;
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_push:
                fragmentClass = PushPreferenceFragment.class;
                break;
            case R.id.nav_history:
                fragmentClass = PushHistoryFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (isLoggedIn || id == R.id.nav_home) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit();
        }else{
            Snackbar.make(drawer, "You Must Be Logged In To Access This Content", Snackbar.LENGTH_LONG).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProgressStart(String text, boolean isCancelable) {
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
        mProgressDialog.show();
        mProgressDialog.setMessage(text);
    }

    @Override
    public void onProgressEnd() {
        try {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        } catch (Exception e) {}
    }
}
