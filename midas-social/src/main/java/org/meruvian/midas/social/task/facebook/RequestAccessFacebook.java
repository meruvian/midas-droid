package org.meruvian.midas.social.task.facebook;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.R;
import org.meruvian.midas.social.SocialVariable;

/**
 * Created by ludviantoovandi on 14/01/15.
 */
public class RequestAccessFacebook extends AsyncTask<Void, Void, String> {
    private Context context;
    private TaskService service;

    public RequestAccessFacebook(Context context, TaskService service) {
        this.context = context;
        this.service = service;
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(SocialVariable.FACEBOOK_REQUEST_ACCESS);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            OAuthClientRequest request = OAuthClientRequest.authorizationLocation(SocialVariable.MERVID_AUTH_URL)
                    .setClientId(SocialVariable.MERVID_APP_ID)
                    .setRedirectURI(SocialVariable.MERVID_CALLBACK)
                    .setResponseType(ResponseType.CODE.toString())
                    .setScope("read write")
                    .setParameter("social", "facebook")
                    .buildQueryMessage();

            return request.getLocationUri();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        if (string != null) {
            service.onSuccess(SocialVariable.FACEBOOK_REQUEST_ACCESS, string);
        } else {
            service.onError(SocialVariable.FACEBOOK_REQUEST_ACCESS, context.getString(R.string.failed_recieve));
        }
    }
}
