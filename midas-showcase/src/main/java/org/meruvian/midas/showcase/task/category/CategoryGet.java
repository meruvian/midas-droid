package org.meruvian.midas.showcase.task.category;

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
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludviantoovandi on 19/01/15.
 */
public class CategoryGet extends AsyncTask<String, Void, JSONObject> {
    private Context context;
    private TaskService service;

    private SharedPreferences preferences;

    public CategoryGet(Context context, TaskService service) {
        this.context = context;
        this.service = service;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(GlobalVariable.CATEGORY_GET_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject json = null;

        try {
            String query = "?name=" + params[0];

            HttpClient httpClient = new DefaultHttpClient(ConnectionUtil.getHttpParams(15000, 15000));
            HttpGet httpGet = new HttpGet(GlobalVariable.SERVER_URL + "/categories" + query);

            httpGet.addHeader(new BasicHeader("Content-Type", "application/json"));
            httpGet.addHeader(new BasicHeader("Authorization", "Bearer " + preferences.getString("mervid_token", "")));
            HttpResponse response = httpClient.execute(httpGet);

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
//        return ConnectionUtil.get(GlobalVariable.SERVER_URL + "/categories");

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            List<Category> categories = new ArrayList<Category>();
            if (jsonObject != null) {
                JSONArray array = jsonObject.getJSONArray("content");
                for (int index = 0; index < array.length(); index++) {
                    JSONObject json = array.getJSONObject(index);

                    Category category = new Category();
                    category.setId(json.getString("id"));
                    category.setName(json.getString("name"));
                    category.setDescription(json.getString("description"));

                    categories.add(category);
                }

                service.onSuccess(GlobalVariable.CATEGORY_GET_TASK, categories);
            } else {
                service.onError(GlobalVariable.CATEGORY_GET_TASK, context.getString(R.string.failed_recieve_news));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            service.onError(GlobalVariable.CATEGORY_GET_TASK, context.getString(R.string.failed_recieve_news));
        }
    }
}
