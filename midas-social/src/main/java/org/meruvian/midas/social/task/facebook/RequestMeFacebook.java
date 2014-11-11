package org.meruvian.midas.social.task.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;

/**
 * Created by ludviantoovandi on 03/10/14.
 */
public class RequestMeFacebook implements Request.GraphUserCallback {
    private Context context;
    private SharedPreferences preferences;

    private TaskService service;

    public RequestMeFacebook(Context context, TaskService service) {
        this.context = context;
        this.service = service;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCompleted(GraphUser user, Response response) {
        if (user != null) {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean("facebook", true);
            editor.putString("facebook_firstName", user.getFirstName());
            editor.putString("facebook_middleName", user.getMiddleName());
            editor.putString("facebook_lastname", user.getLastName());
            editor.putString("facebook_username", user.getUsername());

            if (user.asMap().containsKey("email")  && (user.asMap().get("email").toString() != null && !"".equalsIgnoreCase(user.asMap().get("email").toString()))) {
                editor.putString("facebook_email", user.asMap().get("email").toString());
            }

            editor.commit();

            service.onSuccess(SocialVariable.FACEBOOK_REQUEST_ME_TASK, true);
        }
    }
}
