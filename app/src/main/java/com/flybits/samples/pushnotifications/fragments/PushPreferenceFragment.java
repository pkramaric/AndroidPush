package com.flybits.samples.pushnotifications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestGeneralCallback;
import com.flybits.samples.pushnotifications.R;
import com.flybits.samples.pushnotifications.adapters.PushPreferenceAdapter;
import com.flybits.samples.pushnotifications.interfaces.IProgressDialog;
import com.flybits.samples.pushnotifications.objects.PushPreferenceItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class PushPreferenceFragment extends Fragment implements PushPreferenceAdapter.ICheckboxChanged{

    private ArrayList<PushPreferenceItem> listOfPushPreferenceItems;
    private PushPreferenceAdapter adapter;
    private IProgressDialog callbackProgress;

    private ExecutorService taskGetPushPreferences;
    private ExecutorService taskSavePushPreferences;

    public PushPreferenceFragment() {}

    public static PushPreferenceFragment newInstance() {
        PushPreferenceFragment fragment = new PushPreferenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_push_preference, container, false);

        ListView listView           = (ListView) view.findViewById(R.id.listOfPushPreferences);

        listOfPushPreferenceItems   = new ArrayList<>();
        adapter                     = new PushPreferenceAdapter(getActivity(), R.layout.item_push_preference, listOfPushPreferenceItems, this);
        listView.setAdapter(adapter);

        setupListOfPreferences();
        getPushPreferences();

        return view;
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

    private void getPushPreferences() {

        callbackProgress.onProgressStart("Getting Your Push Preferences...", true);

        /**
         * Get all of the logged in user's push preferences
         */
        taskGetPushPreferences = Flybits.include(getActivity()).getPushPreferences(new IRequestCallback<String[]>() {
            @Override
            public void onSuccess(String[] listOfPushPreferences) {
                if (isAdded()) {
                    for (String item : listOfPushPreferences) {

                        for (PushPreferenceItem itemOfItem : listOfPushPreferenceItems) {

                            if (itemOfItem.getHeader().equals(item)) {
                                itemOfItem.setSelected(false);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Something went wrong! " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String s) {}

            @Override
            public void onCompleted() {
                if (isAdded()) {
                    adapter.notifyDataSetChanged();
                    callbackProgress.onProgressEnd();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_push_preferences, menu);  // Use filter.xml from step 1
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {

            saveMenuOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the Push Preferences that have been selected by the user.
     */
    private void saveMenuOptions() {

        callbackProgress.onProgressStart("Saving Your Push Preferences", true);
        ArrayList<String> listOfSavedPreferences = new ArrayList<>();
        for (PushPreferenceItem item : listOfPushPreferenceItems){
            if (!item.isSelected()){
                listOfSavedPreferences.add(item.getHeader());
            }
        }

        taskSavePushPreferences = Flybits.include(getActivity()).overridePushPreferences(listOfSavedPreferences, new IRequestGeneralCallback() {
            @Override
            public void onSuccess() {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Saved Push Preferences Successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String s) {

            }

            @Override
            public void onCompleted() {
                if (isAdded()) {
                    callbackProgress.onProgressEnd();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IProgressDialog) {
            callbackProgress = (IProgressDialog) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IProgressDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbackProgress = null;
    }

    @Override
    public void onDestroyView() {

        if (taskGetPushPreferences != null && !taskGetPushPreferences.isShutdown()){
            taskGetPushPreferences.shutdownNow();
        }

        if (taskSavePushPreferences != null && !taskSavePushPreferences.isShutdown()){
            taskSavePushPreferences.shutdownNow();
        }

        super.onDestroyView();
    }

    @Override
    public void onChange(int item) {
        boolean currentValue = listOfPushPreferenceItems.get(item).isSelected();
        listOfPushPreferenceItems.get(item).setSelected(!currentValue);
    }
}
