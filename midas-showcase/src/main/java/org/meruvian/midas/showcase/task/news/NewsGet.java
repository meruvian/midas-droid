package org.meruvian.midas.showcase.task.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.Category;
import org.meruvian.midas.showcase.entity.LogInformation;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.fragment.PreferencesFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsGet extends AsyncTask<String, Void, JSONObject> {
    private TaskService taskService;
    private Context context;

    private SharedPreferences preferences;

    public NewsGet(Context context, TaskService taskService) {
        this.taskService = taskService;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        JSONObject json = null;

        try {
            String query = "?q=" + params[0] + "&max=" + params[1] + "&page=" + params[2];

            HttpClient httpClient = new DefaultHttpClient(ConnectionUtil.getHttpParams(15000, 15000));
            HttpGet httpGet = new HttpGet(GlobalVariable.SERVER_URL + "/news" + query);

            httpGet.addHeader(new BasicHeader("Content-Type", "application/json"));
            httpGet.addHeader(new BasicHeader("Authorization", "Bearer " + preferences.getString("mervid_token", "")));
            HttpResponse response = httpClient.execute(httpGet);

            Log.e("response", response.getStatusLine().getReasonPhrase() + "  " + response.getStatusLine().getStatusCode());
            json = new JSONObject(ConnectionUtil.convertEntityToString(response.getEntity()));
        } catch (IOException e) {
            json = null;
            e.printStackTrace();
        } catch (JSONException e) {
            json = null;
            e.printStackTrace();
        } catch (Exception e) {
            json = null;
            e.printStackTrace();
        }
//        return ConnectionUtil.get(GlobalVariable.SERVER_URL + "/news", preferences.getString("mervid_token", ""));
        return json;
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

                    Category category = new Category();
                    if (!json.isNull("category")) {
                        JSONObject jsonCategory = json.getJSONObject("category");

                        category.setId(jsonCategory.getString("id"));
                        category.setName(jsonCategory.getString("name"));
                        category.setDescription(jsonCategory.getString("description"));
                    }

                    news.setCategory(category);

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
