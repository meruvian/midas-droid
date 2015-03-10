package org.meruvian.midas.core.defaults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.meruvian.midas.core.service.AdapterService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludviantoovandi on 09/09/14.
 */
public abstract class DefaultAdapter<L, H> extends BaseAdapter implements AdapterService<L, H> {
    private LayoutInflater inflater;
    private int layout;
    private int position;

    private List<L> contents = new ArrayList<L>();

    private Context context;

    public DefaultAdapter(Context context, int layout, List<L> contents) {
        this.contents = contents;
        this.layout = layout;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getPosition() {
        return position;
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public L getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            holder = ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }

//        createdView(convertView, holder, getItem(position));

        setPosition(position);

        return createdView(convertView, holder, getItem(position));
    }

    @Override
    public void add(L object) {
        contents.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<L> objects) {
        contents.addAll(objects);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        contents.clear();
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public abstract H ViewHolder(View view);

    public abstract View createdView(View view, H holder, L object);
}
