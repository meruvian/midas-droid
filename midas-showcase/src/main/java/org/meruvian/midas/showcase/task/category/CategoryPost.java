package org.meruvian.midas.showcase.task.category;

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
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.Category;

import java.io.IOException;

/**
 * Created by ludviantoovandi on 19/01/15.
 */
public class CategoryPost extends AsyncTask<String, Void, JSONObject> {
    private Context context;
    private TaskService<Category> service;

    private SharedPreferences preferences;

    public CategoryPost(Context context, TaskService service) {
        this.service = service;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(GlobalVariable.CATEGORY_POST_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject json = new JSONObject();

        try {
            json.put("name", params[0]);
            json.put("description", params[1]);

            HttpClient httpClient = new DefaultHttpClient(ConnectionUtil.getHttpParams(15000, 15000));
            HttpPost httpPost = new HttpPost(GlobalVariable.SERVER_URL + "/categories/-");

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
                Category category = new Category();
                category.setId(jsonObject.getString("id"));
                category.setDescription(jsonObject.getString("description"));
                category.setName(jsonObject.getString("name"));
                category.setParentCategory(jsonObject.getString("parentCategory"));

                service.onSuccess(GlobalVariable.CATEGORY_POST_TASK, category);
            } else {
                service.onError(GlobalVariable.CATEGORY_POST_TASK, context.getString(R.string.failed_post_category));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            service.onError(GlobalVariable.CATEGORY_POST_TASK, context.getString(R.string.failed_post_category));
        }
    }
}
