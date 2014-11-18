package org.meruvian.midas.social.task.google;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.R;
import org.meruvian.midas.social.SocialVariable;

/**
 * Created by ludviantoovandi on 03/10/14.
 */
public class RequestMeGoogle extends AsyncTask<String, Void, OAuthResourceResponse> {
    private TaskService service;
    private Context context;

    public RequestMeGoogle(TaskService service, Context context) {
        this.context = context;
        this.service = service;
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(SocialVariable.MERVID_REQUEST_ME_TASK);
    }

    @Override
    protected OAuthResourceResponse doInBackground(String... params) {
        try {
            OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(SocialVariable.MERVID_REQUEST_ME)
                    .setAccessToken(params[0])
                    .buildHeaderMessage();

            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthResourceResponse response = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);

            return response;
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(OAuthResourceResponse oAuthResourceResponse) {
        try {
            if (oAuthResourceResponse.getResponseCode() == 200) {
                JSONObject json = new JSONObject(oAuthResourceResponse.getBody());

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("mervid_user_id", json.getString("id"));
                editor.putString("mervid_user_username", json.getString("username"));
                editor.putString("mervid_user_email", json.getString("email"));
                editor.putString("mervid_user_firstname", json.getJSONObject("name").optString("first", ""));
                editor.putString("mervid_user_middlename", json.getJSONObject("name").optString("middle", ""));
                editor.putString("mervid_user_lastname", json.getJSONObject("name").optString("last", ""));
                editor.putString("mervid_user_address", json.optString("address", ""));

                editor.commit();

                service.onSuccess(SocialVariable.MERVID_REQUEST_ME_TASK, true);
            } else {
                service.onError(SocialVariable.MERVID_REQUEST_ME_TASK, context.getString(R.string.failed_recieve));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            service.onError(SocialVariable.MERVID_REQUEST_ME_TASK, context.getString(R.string.failed_recieve));
        } catch (Exception e) {
            e.printStackTrace();
            service.onError(SocialVariable.MERVID_REQUEST_ME_TASK, context.getString(R.string.failed_recieve));
        }
    }
}
