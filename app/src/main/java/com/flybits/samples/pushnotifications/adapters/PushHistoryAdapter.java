package com.flybits.samples.pushnotifications.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flybits.core.api.models.Push;
import com.flybits.samples.pushnotifications.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PushHistoryAdapter extends ArrayAdapter<Push> {

    private ArrayList<Push> listOfPushes;
    private LayoutInflater li;
    private DateFormat format;
    private Context context;

    public PushHistoryAdapter(Context context, int resource, ArrayList<Push> objects) {
        super(context, resource, objects);

        this.context        = context;
        listOfPushes        = objects;
        li                  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        format              = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Push item = listOfPushes.get(position);
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView             = li.inflate(R.layout.item_push_history, parent, false);
            holder.txtContents      = (TextView) convertView.findViewById(R.id.txtContents);
            holder.txtTimestamp     = (TextView) convertView.findViewById(R.id.txtTimestamp);
            holder.txtTitle         = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.txtAlert         = (TextView) convertView.findViewById(R.id.txtAlert);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtContents.setText(context.getString(R.string.txtHistoryCustom, item.bodyAsString));

        int visibilityAlert = (item.alert != null) ? View.VISIBLE : View.GONE;
        holder.txtAlert.setVisibility(visibilityAlert);
        holder.txtAlert.setText(context.getString(R.string.txtHistoryAlert, item.alert));

        int visibilityTitle = (item.title != null) ? View.VISIBLE : View.GONE;
        holder.txtTitle.setVisibility(visibilityTitle);
        holder.txtTitle.setText(context.getString(R.string.txtHistoryTitle, item.title));

        holder.txtTimestamp.setText(format.format(item.timestamp * 1000));
        return convertView;
    }

    public static class ViewHolder{
        public TextView txtContents;
        public TextView txtTitle;
        public TextView txtAlert;
        public TextView txtTimestamp;
    }
}
