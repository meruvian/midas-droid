package org.meruvian.midas.showcase.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.social.SocialVariable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ludviantoovandi on 19/11/14.
 */
public class LoginTask extends AsyncTask<String, Void, JSONObject> {
    private TaskService service;
    private Context context;

    public LoginTask(TaskService service, Context context) {
        this.service = service;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(GlobalVariable.LOGIN_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String encodeSecret = URLEncoder.encode(SocialVariable.MERVID_API_SECRET, "UTF-8");

            String url = "http://merv.id/oauth/token?" +
                    "grant_type=password" +
                    "&username=" + params[0] +
                    "&password=" + params[1] +
                    "&client_id=" + SocialVariable.MERVID_APP_ID +
                    "&client_secret=" + encodeSecret;
            return ConnectionUtil.post(url, new ArrayList<NameValuePair>());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                Log.e("json", jsonObject.toString());
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean("manual", true);
                editor.putString("manual_token", jsonObject.getString("access_token"));
                editor.putString("manual_token_type", jsonObject.getString("token_type"));
                editor.putLong("manual_expires_in", jsonObject.getLong("expires_in"));
                editor.putString("manual_scope", jsonObject.getString("scope"));
                editor.putString("manual_jti", jsonObject.getString("jti"));
                editor.commit();

                service.onSuccess(GlobalVariable.LOGIN_TASK, jsonObject.getString("access_token"));
            } catch (JSONException e) {
                e.printStackTrace();
                service.onError(GlobalVariable.LOGIN_TASK, context.getString(org.meruvian.midas.social.R.string.failed_recieve));
            }
        } else {
            service.onError(GlobalVariable.LOGIN_TASK, context.getString(org.meruvian.midas.social.R.string.failed_recieve));
        }
    }
}
