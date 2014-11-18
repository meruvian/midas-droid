package org.meruvian.midas.social.task.google;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.social.R;
import org.meruvian.midas.social.SocialVariable;

/**
 * Created by ludviantoovandi on 01/10/14.
 */
public class RequestTokenGoogle extends AsyncTask<String, Void, JSONObject> {
    private TaskService service;
    private Context context;

    public RequestTokenGoogle(TaskService service, Context context) {
        this.service = service;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(SocialVariable.GOOGLE_REQUEST_TOKEN_TASK);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(SocialVariable.MERVID_REQUEST_TOKEN)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(SocialVariable.MERVID_APP_ID)
                    .setClientSecret(SocialVariable.MERVID_API_SECRET)
                    .setRedirectURI(SocialVariable.MERVID_CALLBACK)
                    .setCode(params[0])
                    .setParameter("social", "google")
                    .buildQueryMessage();

            return ConnectionUtil.get(request.getLocationUri());

        } catch (OAuthSystemException e) {
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
                editor.putBoolean("google", true);
                editor.putString("google_token", jsonObject.getString("access_token"));
                editor.putString("google_token_type", jsonObject.getString("token_type"));
                editor.putLong("google_expires_in", jsonObject.getLong("expires_in"));
                editor.putString("google_scope", jsonObject.getString("scope"));
                editor.putString("google_jti", jsonObject.getString("jti"));
                editor.commit();

                service.onSuccess(SocialVariable.GOOGLE_REQUEST_TOKEN_TASK, jsonObject.getString("access_token"));
            } catch (JSONException e) {
                e.printStackTrace();
                service.onError(SocialVariable.GOOGLE_REQUEST_TOKEN_TASK, context.getString(R.string.failed_recieve));
            }
        } else {
            service.onError(SocialVariable.GOOGLE_REQUEST_TOKEN_TASK, context.getString(R.string.failed_recieve));
        }
    }
}
