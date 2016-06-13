package com.flybits.samples.pushnotifications;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestGeneralCallback;
import com.flybits.samples.pushnotifications.adapters.PushPreferenceAdapter;
import com.flybits.samples.pushnotifications.objects.PushPreferenceItem;

import java.util.ArrayList;

public class PushPreferencesActivity extends AppCompatActivity implements PushPreferenceAdapter.ICheckboxChanged {

    private ArrayList<PushPreferenceItem> listOfPushPreferenceItems;
    private PushPreferenceAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_preferences);

        progressDialog          = new ProgressDialog(this);

        ListView listView           = (ListView) findViewById(R.id.listOfPushPreferences);
        listOfPushPreferenceItems   = new ArrayList<>();
        adapter                     = new PushPreferenceAdapter(PushPreferencesActivity.this, R.layout.item_push_preference, listOfPushPreferenceItems, this);
        listView.setAdapter(adapter);

        setProgressBar("Getting Your Push Preferences", true);
        setupListOfPreferences();

        Flybits.include(PushPreferencesActivity.this).getPushPreferences(new IRequestCallback<String[]>() {
            @Override
            public void onSuccess(String[] listOfPushPreferences) {
                for (String item : listOfPushPreferences){

                    for (PushPreferenceItem itemOfItem : listOfPushPreferenceItems){

                        if (itemOfItem.getHeader().equals(item)){
                            itemOfItem.setSelected(false);
                            break;
                        }

                    }
                }
                Log.d("Testing", "I");
            }

            @Override
            public void onException(Exception e) {
                Toast.makeText(PushPreferencesActivity.this, "Something went wrong! " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed(String s) {}

            @Override
            public void onCompleted() {

                adapter.notifyDataSetChanged();
                stopProgressBar();
            }
        });
    }

    private void setupListOfPreferences() {
        PushPreferenceItem item1    = new PushPreferenceItem("Sports", "Interested in notifications about sports in your area");
        listOfPushPreferenceItems.add(item1);
        PushPreferenceItem item2    = new PushPreferenceItem("Restaurants", "Interested in notifications about restaurants in your area");
        listOfPushPreferenceItems.add(item2);
        PushPreferenceItem item3    = new PushPreferenceItem("Arts", "Interested in notifications about arts and crafts in your area");
        listOfPushPreferenceItems.add(item3);
        PushPreferenceItem item4    = new PushPreferenceItem("Emergency", "Interested in notifications about emergencies in your area");
        listOfPushPreferenceItems.add(item4);
        PushPreferenceItem item5    = new PushPreferenceItem("Parks", "Interested in notifications about parks in your area");
        listOfPushPreferenceItems.add(item5);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_push_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            saveMenuOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveMenuOptions() {

        setProgressBar("Saving Your Push Preferences", true);
        Flybits.include(PushPreferencesActivity.this).deletePushPreferenceOption(null, new IRequestGeneralCallback() {
            @Override
            public void onSuccess() {

                ArrayList<String> listOfSavedPreferences = new ArrayList<>();
                for (PushPreferenceItem item : listOfPushPreferenceItems){
                    if (!item.isSelected()){
                        listOfSavedPreferences.add(item.getHeader());
                    }
                }
                String[] stringArray = listOfSavedPreferences.toArray(new String[0]);

                if (stringArray.length > 0) {
                    Flybits.include(PushPreferencesActivity.this).addPushPreferenceOptions(stringArray, new IRequestGeneralCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(PushPreferencesActivity.this, "Saved Push Preferences Successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(Exception e) {
                            Toast.makeText(PushPreferencesActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onException(Exception e) {
                Toast.makeText(PushPreferencesActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                stopProgressBar();
            }

            @Override
            public void onFailed(String s) {}

            @Override
            public void onCompleted() {}
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
    public void onChange(int item) {

        boolean currentValue = listOfPushPreferenceItems.get(item).isSelected();
        listOfPushPreferenceItems.get(item).setSelected(!currentValue);
    }
}
