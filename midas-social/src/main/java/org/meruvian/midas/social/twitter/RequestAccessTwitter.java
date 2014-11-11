package org.meruvian.midas.social.twitter;

import android.content.Context;
import android.os.AsyncTask;

import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ludviantoovandi on 18/07/14.
 */
public class RequestAccessTwitter extends AsyncTask<Void, Void, RequestToken> {
    private TaskService taskService;

    public RequestAccessTwitter(TaskService taskService, Context context) {
        this.taskService = taskService;
    }

    @Override
    protected void onPreExecute() {
        taskService.onExecute(SocialVariable.TWITTER_REQUEST_ACCESS_TASK);
    }

    @Override
    protected RequestToken doInBackground(Void... params) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(SocialVariable.TWITTER_API_KEY);
        builder.setOAuthConsumerSecret(SocialVariable.TWITTER_SECRET_KEY);

        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        try {
            return twitter.getOAuthRequestToken(SocialVariable.TWITTER_CALLBACK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(RequestToken result) {
        taskService.onSuccess(SocialVariable.TWITTER_REQUEST_ACCESS_TASK, result);
    }

}
