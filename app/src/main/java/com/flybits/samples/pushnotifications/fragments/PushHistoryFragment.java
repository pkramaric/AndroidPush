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
import android.widget.TextView;
import android.widget.Toast;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Push;
import com.flybits.core.api.utils.filters.PushHistoryOptions;
import com.flybits.samples.pushnotifications.R;
import com.flybits.samples.pushnotifications.adapters.PushHistoryAdapter;
import com.flybits.samples.pushnotifications.interfaces.IProgressDialog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class PushHistoryFragment extends Fragment {

    private ArrayList<Push> listOfPushNotifications;
    private PushHistoryAdapter adapter;
    private IProgressDialog callbackProgress;

    private ExecutorService taskGetPushHistory;
    private TextView txtEmptyList;

    public PushHistoryFragment() {}

    public static PushHistoryFragment newInstance() {
        return new PushHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_push_history, container, false);

        txtEmptyList                = (TextView) view.findViewById(R.id.txtNoEntries);
        ListView lsvPushHistory     = (ListView) view.findViewById(R.id.listOfPushHistory);
        listOfPushNotifications     = new ArrayList<>();
        adapter                     = new PushHistoryAdapter(getActivity(), R.layout.item_push_history,listOfPushNotifications);
        lsvPushHistory.setAdapter(adapter);

        getPushHistory(0);
        return view;
    }

    private void getPushHistory(int page) {

        callbackProgress.onProgressStart("Getting Your Push History...", true);

        PushHistoryOptions options = new PushHistoryOptions.Builder()
                .addPaging(10, page)
//                .addTimeRange()
                .build();

        taskGetPushHistory  = Flybits.include(getActivity()).getPushHistory(options, new IRequestPaginationCallback<ArrayList<Push>>() {

            @Override
            public void onSuccess(ArrayList<Push> pushes, Pagination pagination) {
                if (isAdded()) {
                    listOfPushNotifications.addAll(pushes);
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

                    int visibility = (listOfPushNotifications.size() == 0)? View.VISIBLE : View.GONE;
                    txtEmptyList.setVisibility(visibility);
                    callbackProgress.onProgressEnd();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_push_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            listOfPushNotifications.clear();
            getPushHistory(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {

        if (taskGetPushHistory != null && !taskGetPushHistory.isShutdown()){
            taskGetPushHistory.shutdownNow();
        }

        super.onDestroyView();
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
}
