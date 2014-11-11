package org.meruvian.midas.social.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ludviantoovandi on 23/07/14.
 */
public class RequestTokenTwitter extends AsyncTask<Object, Void, AccessToken> {
    private TaskService service;

    private Context context;
    private SharedPreferences preferences;

    public RequestTokenTwitter(TaskService service, Context context) {
        this.service = service;
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        service.onExecute(SocialVariable.TWITTER_REQUEST_TOKEN_TASK);
    }

    @Override
    protected AccessToken doInBackground(Object... params) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(SocialVariable.TWITTER_API_KEY);
        builder.setOAuthConsumerSecret(SocialVariable.TWITTER_SECRET_KEY);

        Twitter twitter = new TwitterFactory(builder.build()).getInstance();

        Uri uri = (Uri) params[0];
        RequestToken requestToken = (RequestToken) params[1];

        String verifier = uri.getQueryParameter("oauth_verifier");

        try {
            return twitter.getOAuthAccessToken(requestToken, verifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        if (accessToken != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("twitter_token", accessToken.getToken());
            editor.putString("twitter_token_secret", accessToken.getTokenSecret());
            editor.putBoolean("twitter", true);
            editor.commit();

            service.onSuccess(SocialVariable.TWITTER_REQUEST_TOKEN_TASK, true);
        } else {
            service.onError(SocialVariable.TWITTER_REQUEST_TOKEN_TASK, context.getString(R.string.failed_recieve));
        }

    }
}
