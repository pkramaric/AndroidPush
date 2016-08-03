package com.flybits.samples.pushnotifications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Push;
import com.flybits.core.api.utils.filters.PushHistoryOptions;
import com.flybits.samples.pushnotifications.MainActivity;
import com.flybits.samples.pushnotifications.R;
import com.flybits.samples.pushnotifications.adapters.PushHistoryAdapter;
import com.flybits.samples.pushnotifications.dialogs.DatePicker;
import com.flybits.samples.pushnotifications.interfaces.IProgressDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class PushHistoryFragment extends Fragment {

    private ArrayList<Push> listOfPushNotifications;
    private PushHistoryAdapter adapter;
    private IProgressDialog callbackProgress;

    private ExecutorService taskGetPushHistory;
    private TextView txtEmptyList;
    private boolean isEndTimeSelected;

    private Calendar startTime, endTime;
    private Button btnChangeStart, btnChangeEnd;
    private RadioButton radAsc, radDesc;
    private MainActivity mainActivity;

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

        startTime                   = Calendar.getInstance();
        endTime                     = Calendar.getInstance();

        btnChangeStart      = (Button) view.findViewById(R.id.btnChangeStartTime);
        btnChangeEnd        = (Button) view.findViewById(R.id.btnChangeEndTime);
        radAsc              = (RadioButton) view.findViewById(R.id.rdbAsc);
        radDesc             = (RadioButton) view.findViewById(R.id.rdbDesc);

        btnChangeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEndTimeSelected = false;
                DialogFragment newFragment = new DatePicker();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        btnChangeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEndTimeSelected = true;
                DialogFragment newFragment = new DatePicker();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        radAsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getPushHistory(0);
            }
        });

        radDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getPushHistory(0);
            }
        });

        if (mainActivity != null) {
            mainActivity.setActionBarTitle("Push History");
        }

        getPushHistory(0);
        return view;
    }

    private void getPushHistory(final int page) {

        callbackProgress.onProgressStart("Getting Your Push History...", true);

        PushHistoryOptions.Builder options = new PushHistoryOptions.Builder()
                .addPaging(10, page);

        if (radAsc.isChecked()){
            options.setSortOrder(PushHistoryOptions.SortOrder.ASCENDING);
        }else{
            options.setSortOrder(PushHistoryOptions.SortOrder.DESCENDING);
        }

        if (!checkIfTimeSelected(btnChangeEnd, "End Time")  &&
                !checkIfTimeSelected(btnChangeStart, "Start Time")){

            options.addTimeRange((startTime.getTimeInMillis() / 1000), (endTime.getTimeInMillis() / 1000));
        }

        taskGetPushHistory  = Flybits.include(getActivity()).getPushHistory(options.build(), new IRequestPaginationCallback<ArrayList<Push>>() {

            @Override
            public void onSuccess(ArrayList<Push> pushes, Pagination pagination) {

                if (page == 0){
                    listOfPushNotifications.clear();
                }

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
            getPushHistory(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {

        if (mainActivity != null) {
            mainActivity.setActionBarTitle("");
        }

        if (taskGetPushHistory != null && !taskGetPushHistory.isShutdown()){
            taskGetPushHistory.shutdownNow();
        }

        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity){
            mainActivity =(MainActivity) context;
        }

        if (context instanceof IProgressDialog) {
            callbackProgress = (IProgressDialog) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IProgressDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
        callbackProgress = null;
    }

    public void onTimeSelected(Calendar calendar){

        DateFormat format              = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        if (isEndTimeSelected){
            endTime =   calendar;
            btnChangeEnd.setText(getString(R.string.toTime, format.format(calendar.getTime())));

            if (!checkIfTimeSelected(btnChangeStart, "Start Time")){
                getPushHistory(0);
            }
        }else{
            startTime   =   calendar;
            btnChangeStart.setText(getString(R.string.fromTime, format.format(calendar.getTime())));

            if (!checkIfTimeSelected(btnChangeEnd, "End Time")){
                getPushHistory(0);
            }
        }
    }

    public boolean checkIfTimeSelected(Button txt, String text){
        return txt.getText().toString().equals(text);

    }
}
