package org.meruvian.midas.showcase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.News;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsAdapter extends ArrayAdapter<News> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public NewsAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(getContext(), R.layout.adapter_news_view, null);

            holder.content = (TextView) convertView.findViewById(R.id.text_content);
            holder.date = (TextView) convertView.findViewById(R.id.text_date);
            holder.title = (TextView) convertView.findViewById(R.id.text_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        News news = getItem(position);

        holder.title.setText(news.getTitle());
        holder.date.setText(dateFormat.format(news.getLogInformation().getCreateDate()));

        if (news.getContent().length() > 100) {
            holder.content.setText(news.getContent().substring(0, 100) + " ...");
        } else {
            holder.content.setText(news.getContent());
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView title;
        public TextView date;
        public TextView content;
    }
}
