package org.meruvian.midas.showcase.task.news;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.LogInformation;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsGet extends AsyncTask<String, Void, JSONObject> {
    private TaskService taskService;
    private Context context;

    public NewsGet(TaskService taskService, Context context) {
        this.taskService = taskService;
        this.context = context;
    }

    @Override
    protected void onCancelled() {
        taskService.onCancel(GlobalVariable.NEWS_GET_TASK, context.getString(R.string.cancle));
    }

    @Override
    protected void onPreExecute() {
        taskService.onExecute(GlobalVariable.NEWS_GET_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return ConnectionUtil.get(GlobalVariable.SERVER_URL + "/news");
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                List<News> newses = new ArrayList<News>();

                JSONArray array = jsonObject.getJSONArray("content");
                for (int index = 0; index < array.length(); index++) {
                    JSONObject json = array.getJSONObject(index);
                    JSONObject jsonLog = json.getJSONObject("logInformation");

                    News news = new News();
                    news.setId(json.getString("id"));
                    news.setContent(json.getString("content"));
                    news.setTitle(json.getString("title"));

                    LogInformation logInformation = news.getLogInformation();
                    logInformation.setCreateDate(new Date(jsonLog.getLong("createDate")));

                    newses.add(news);
                }

                taskService.onSuccess(GlobalVariable.NEWS_GET_TASK, newses);
            } else {
                taskService.onError(GlobalVariable.NEWS_GET_TASK, context.getString(R.string.failed_recieve_news));
            }
        } catch (JSONException e) {
            e.printStackTrace();

            taskService.onError(GlobalVariable.NEWS_GET_TASK, context.getString(R.string.failed_recieve_news));
        }
    }
}
