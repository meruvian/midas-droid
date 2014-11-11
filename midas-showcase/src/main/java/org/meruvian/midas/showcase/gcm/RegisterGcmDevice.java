package org.meruvian.midas.showcase.gcm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.meruvian.midas.core.util.ConnectionUtil;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.social.SocialVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludviantoovandi on 13/08/14.
 */
public abstract class RegisterGcmDevice {
    private Activity activity;
    private GoogleCloudMessaging gcm;
    private String registrationId;

    public void register(Activity activity) {
        this.activity = activity;

        if (checkPlayServices()) {
            if (!hasRegister()) {
                registerDevice();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 9000).show();
            }

            return false;
        }
        return true;
    }

    private void registerDevice() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }

                    registrationId = gcm.register(GlobalVariable.GOOGLE_APP_ID);
                    Log.e("gcm", registrationId + " asdasd");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(String id) {
                if (id != null && !"".equalsIgnoreCase(id)) {
                    storeRegistrationId(id);
                    storeInServer(id);
                }
            }
        }.execute();
    }

    protected abstract void storeRegistrationId(String id);

    public abstract boolean hasRegister();

    public abstract void storeInServer(String id);

//    private void storeRegistrationId(String id) {
//        SharedPreferences.Editor editor = defaultPreference.edit();
//        editor.putString("gcm_id", id);
//
//        editor.commit();
//    }
//
//    private boolean hasRegister() {
//        return defaultPreference.contains("gcm_id");
//    }
}
