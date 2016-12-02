package com.flybits.samples.pushnotifications;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestGeneralCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.samples.pushnotifications.fragments.HomeFragment;
import com.flybits.samples.pushnotifications.fragments.PushHistoryFragment;
import com.flybits.samples.pushnotifications.fragments.PushPreferenceFragment;
import com.flybits.samples.pushnotifications.interfaces.IProgressDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IProgressDialog,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private ProgressDialog mProgressDialog;
    private boolean isLoggedIn;
    private Fragment fragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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

    public void setActionBarTitle(String title){
        toolbar.setTitle(title);
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
                    public void onException(Exception e) {}

                    @Override
                    public void onFailed(String s) {}

                    @Override
                    public void onCompleted() {}
                });
            }

            @Override
            public void onException(Exception e) {}
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

        Class fragmentClass = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
            case R.id.nav_refresh_jwt:
                onProgressStart("Refreshing JWT...", true);
                Flybits.include(MainActivity.this).refreshJWT(new IRequestGeneralCallback() {
                    @Override
                    public void onSuccess() {
                        Snackbar.make(drawer, "JWT Token Succesfully Refreshed", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Exception e) {
                        Snackbar.make(drawer, "Something Went Wrong! JWT Was not refreshed.", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed(String s) {

                    }

                    @Override
                    public void onCompleted() {
                        onProgressEnd();
                    }
                });
                break;
            default:
                fragmentClass = HomeFragment.class;
        }
        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (isLoggedIn || id == R.id.nav_home) {
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit();
            } else {
                Snackbar.make(drawer, "You Must Be Logged In To Access This Content", Snackbar.LENGTH_LONG).show();
            }
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

    private Calendar calendar;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (fragment instanceof PushHistoryFragment && fragment.isAdded()){
            calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            DialogFragment newFragment = new com.flybits.samples.pushnotifications.dialogs.TimePicker();
            newFragment.show(getSupportFragmentManager(), "timePicker");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (fragment instanceof PushHistoryFragment && fragment.isAdded()){

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            ((PushHistoryFragment) fragment).onTimeSelected(calendar);

        }
    }
}
