package com.flybits.samples.pushnotifications.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flybits.samples.pushnotifications.R;
import com.flybits.samples.pushnotifications.objects.PushPreferenceItem;

import java.util.ArrayList;

public class PushPreferenceAdapter extends ArrayAdapter<PushPreferenceItem> {

    public interface ICheckboxChanged{
        void onChange(int item);
    }

    private ArrayList<PushPreferenceItem> listOfPreferences;
    private LayoutInflater li;
    private ICheckboxChanged callback;

    public PushPreferenceAdapter(Context context, int resource, ArrayList<PushPreferenceItem> objects, ICheckboxChanged callback) {
        super(context, resource, objects);
        listOfPreferences   = objects;
        this.callback       = callback;
        li                  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PushPreferenceItem item = listOfPreferences.get(position);
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView             = li.inflate(R.layout.item_push_preference, parent, false);
            holder.txtTitle         = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.txtDescription   = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.ckbIsEnabled     = (CheckBox) convertView.findViewById(R.id.cbxIsEnabled);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(item.getHeader());
        holder.txtDescription.setText(item.getDescription());
        holder.ckbIsEnabled.setChecked(item.isSelected());
        holder.ckbIsEnabled.setTag(position);
        holder.ckbIsEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onChange((Integer) v.getTag());
            }
        });
        return convertView;
    }

    public static class ViewHolder{
        public TextView txtTitle;
        public TextView txtDescription;
        public CheckBox ckbIsEnabled;
    }
}
