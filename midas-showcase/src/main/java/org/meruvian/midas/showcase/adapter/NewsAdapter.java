package org.meruvian.midas.showcase.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.meruvian.midas.core.defaults.DefaultAdapter;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.showcase.holder.NewsHolder;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ludviantoovandi on 17/11/14.
 */
public class NewsAdapter extends DefaultAdapter<News, NewsHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public NewsAdapter(Context context, int layout, List<News> contents) {
        super(context, layout, contents);
    }

    @Override
    public NewsHolder ViewHolder() {
        return new NewsHolder();
    }

    @Override
    public void findView(NewsHolder holder, View convertView) {
        holder.content = (TextView) convertView.findViewById(R.id.text_content);
        holder.date = (TextView) convertView.findViewById(R.id.text_date);
        holder.title = (TextView) convertView.findViewById(R.id.text_title);
    }

    @Override
    public void createdView(NewsHolder holder, News news) {
        holder.title.setText(news.getTitle());
        holder.date.setText(dateFormat.format(news.getLogInformation().getCreateDate()));

        if (news.getContent().length() > 100) {
            holder.content.setText(news.getContent().substring(0, 100) + " ...");
        } else {
            holder.content.setText(news.getContent());
        }
    }
}
