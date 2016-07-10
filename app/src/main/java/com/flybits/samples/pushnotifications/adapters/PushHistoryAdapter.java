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

    public PushHistoryAdapter(Context context, int resource, ArrayList<Push> objects) {
        super(context, resource, objects);

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

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtContents.setText(item.bodyAsString);
        holder.txtTimestamp.setText(format.format(item.timestamp));
        return convertView;
    }

    public static class ViewHolder{
        public TextView txtContents;
        public TextView txtTimestamp;
    }
}
