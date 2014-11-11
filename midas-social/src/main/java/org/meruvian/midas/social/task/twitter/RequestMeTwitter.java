package org.meruvian.midas.social.task.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ludviantoovandi on 03/10/14.
 */
public class RequestMeTwitter extends AsyncTask<Void, Void, User> {
    private TaskService service;
    private Context context;

    private SharedPreferences preferences;

    public RequestMeTwitter(TaskService service, Context context) {
        this.service = service;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(SocialVariable.TWITTER_REQUEST_ME_TASK);
    }

    @Override
    protected User doInBackground(Void... params) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(SocialVariable.TWITTER_API_KEY);
        builder.setOAuthConsumerSecret(SocialVariable.TWITTER_SECRET_KEY);

        Twitter twitter = new TwitterFactory(builder.build()).getInstance();

        AccessToken accessToken = new AccessToken(preferences.getString("twitter_token", ""), preferences.getString("twitter_secret", ""));
        try {
            User user = twitter.showUser(accessToken.getUserId());

            return user;
        } catch (TwitterException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        if (user != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("twitter_user_name", user.getName());
            editor.putString("twitter_user_screenname", user.getScreenName());
            editor.putLong("twitter_user_id", user.getId());
            editor.commit();

            service.onSuccess(SocialVariable.TWITTER_REQUEST_ME_TASK, true);
        } else {
            service.onError(SocialVariable.TWITTER_REQUEST_ME_TASK, context.getString(R.string.failed_recieve));
        }
    }
}
