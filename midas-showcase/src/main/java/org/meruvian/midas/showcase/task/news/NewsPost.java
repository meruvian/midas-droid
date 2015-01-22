package org.meruvian.midas.showcase.task.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsPost extends AsyncTask<String, Void, JSONObject> {
    private TaskService<News> taskService;
    private Context context;

    private SharedPreferences preferences;

    public NewsPost(TaskService taskService, Context context) {
        this.taskService = taskService;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        taskService.onExecute(GlobalVariable.NEWS_POST_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject json = new JSONObject();
        JSONObject jsonCategory = new JSONObject();

        try {
            json.put("title", params[0]);
            json.put("content", params[2]);

            if (params[1] != null && !"".equalsIgnoreCase(params[1])) {
                jsonCategory.put("id", params[1]);
                json.put("category", jsonCategory);
            }

            HttpClient httpClient = new DefaultHttpClient(ConnectionUtil.getHttpParams(15000, 15000));
            HttpPost httpPost = new HttpPost(GlobalVariable.SERVER_URL + "/news/-");

            httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
            httpPost.addHeader(new BasicHeader("Authorization", "Bearer " + preferences.getString("mervid_token", "")));
            httpPost.setEntity(new StringEntity(json.toString()));

            HttpResponse response = httpClient.execute(httpPost);
            json = new JSONObject(ConnectionUtil.convertEntityToString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
            json = null;
        } catch (JSONException e) {
            e.printStackTrace();
            json = null;
        } catch (Exception e) {
            e.printStackTrace();
            json = null;
        }

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                News news = new News();
                news.setId(jsonObject.getString("id"));
                news.setContent(jsonObject.getString("content"));
                news.setTitle(jsonObject.getString("title"));

                taskService.onSuccess(GlobalVariable.NEWS_POST_TASK, news);
            } else {
                taskService.onError(GlobalVariable.NEWS_POST_TASK, context.getString(R.string.failed_post_news));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            taskService.onError(GlobalVariable.NEWS_POST_TASK, context.getString(R.string.failed_post_news));
        }
    }
}
