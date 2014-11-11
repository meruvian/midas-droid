package org.meruvian.midas.showcase.task.news;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.News;

import java.util.Date;

/**
 * Created by ludviantoovandi on 25/10/14.
 */
public class NewsDetailGet extends AsyncTask<String, Void, JSONObject> {
    private TaskService<News> service;
    private Context context;

    public NewsDetailGet(TaskService<News> service, Context context) {
        this.service = service;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(GlobalVariable.NEWS_DETAIL_GET_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return ConnectionUtil.get(GlobalVariable.SERVER_URL + "/news/" + params[0]);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                News news = new News();
                news.setId(jsonObject.getString("id"));
                news.setContent(jsonObject.getString("content"));
                news.setTitle(jsonObject.getString("title"));
                news.getLogInformation().setCreateDate(new Date(jsonObject.getJSONObject("logInformation").getLong("createDate")));

                service.onSuccess(GlobalVariable.NEWS_DETAIL_GET_TASK, news);
            } catch (JSONException e) {
                service.onError(GlobalVariable.NEWS_DETAIL_GET_TASK, context.getString(R.string.failed_recieve_news));
            }
        } else {
            service.onError(GlobalVariable.NEWS_DETAIL_GET_TASK, context.getString(R.string.failed_recieve_news));
        }
    }
}
