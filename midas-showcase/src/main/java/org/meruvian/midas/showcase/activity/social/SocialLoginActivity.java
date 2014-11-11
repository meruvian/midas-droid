package org.meruvian.midas.showcase.activity.social;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.activity.WebViewActivity;
import org.meruvian.midas.social.task.facebook.RequestMeFacebook;
import org.meruvian.midas.social.task.mervid.RequestMeMervID;
import org.meruvian.midas.social.task.mervid.RequestTokenMervID;
import org.meruvian.midas.social.task.twitter.RequestAccessTwitter;
import org.meruvian.midas.social.task.twitter.RequestTokenTwitter;

import twitter4j.auth.RequestToken;

public class SocialLoginActivity extends DefaultActivity implements TaskService, PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener,
        PlusClient.OnAccessRevokedListener {
    private Button facebookLogin, googleLogin, /*twitterLogin,*/ mervIDlogin;
    private ProgressDialog progressDialog;
    private TextView usersText;
    private WebView webOauth;

    private SharedPreferences preferences;
    private StringBuilder users = new StringBuilder("");

    // Facebook
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    // Google
    private PlusClient plusClient;
    private ConnectionResult connectionResult;

    // Twitter
//    private RequestToken requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.social_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        facebookLogin = (Button) findViewById(R.id.button_facebook_login);
//        twitterLogin = (Button) findViewById(R.id.button_twitter_login);
        googleLogin = (Button) findViewById(R.id.button_google_login);
        mervIDlogin = (Button) findViewById(R.id.button_mervid_login);
        usersText = (TextView) findViewById(R.id.text_users);
        webOauth = (WebView) findViewById(R.id.webview);

        plusClient = new PlusClient.Builder(this, this, this)
                .setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .setScopes(Scopes.PLUS_LOGIN)
                .build();

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFacebook();
            }
        });

//        twitterLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickTwitter();
//            }
//        });

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoogle();
            }
        });

        mervIDlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMervID();
            }
        });

        updateViewFacebook();
//        updateViewTwitter(getIntent());
        updateViewMervID(getIntent());
        updateViewGoogle();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
        plusClient.connect();
    }

    @Override
    public void onStop() {
        plusClient.disconnect();
        Session.getActiveSession().removeCallback(statusCallback);

        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == SocialVariable.GOOGLE_REQUEST_ACCESS_TASK
                || requestCode == SocialVariable.GOOGLE_REQUEST_PLAY_TASK) {
            if (resultCode == RESULT_OK && !plusClient.isConnected()
                    && !plusClient.isConnecting()) {
                plusClient.connect();
            }
        } else if (requestCode == SocialVariable.MERVID_REQUEST_ACCESS) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null && uri.toString().startsWith(SocialVariable.MERVID_CALLBACK)) {
                    String code = uri.getQueryParameter("code");

                    if (code != null && !"".equalsIgnoreCase(code)) {
                        new RequestTokenMervID(this, this).execute(code);
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onExecute(int code) {
        progressDialog = new ProgressDialog(this);

        if (code == SocialVariable.TWITTER_REQUEST_ACCESS_TASK || code == SocialVariable.TWITTER_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_twitter));
        } else if (code == SocialVariable.MERVID_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_merv_id));
        } else if (code == SocialVariable.MERVID_REQUEST_ME_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_merv_id));
        }

        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, Object object) {
        progressDialog.dismiss();

        if (object != null) {
            /*if (code == SocialVariable.TWITTER_REQUEST_ACCESS_TASK) {
                requestToken = (RequestToken) object;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL()));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                startActivity(intent);
            } else if (code == SocialVariable.TWITTER_REQUEST_TOKEN_TASK) {
                boolean result = (Boolean) object;
                if (result) {
                    users.append("Twitter : \n");
                    users.append(preferences.getString("twitter_user_screenname", "") + "\n");

                    twitterLogin.setText("Logout from Twitter");
                }
            } else*/ if (code == SocialVariable.MERVID_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    mervIDlogin.setText(getString(R.string.logout_merv_id));

//                    new RequestMeMervID(this, this).execute(result);
                }
            } else if (code == SocialVariable.MERVID_REQUEST_ME_TASK) {
                boolean result = (Boolean) object;
                if (result) {
                    users.append("Merv ID : \n");
                    users.append(preferences.getString("mervid_firstName", "") + " " + preferences.getString("mervid_middlename", "") + " " + preferences.getString("mervid_lastname", "") + "\n");
                }
            } else if (code == SocialVariable.FACEBOOK_REQUEST_ME_TASK) {
                boolean result = (Boolean) object;
                if (result) {
                    users.append("Facebook : \n");
                    users.append(preferences.getString("facebook_firstName", "") + " " + preferences.getString("facebook_middlename", "") + " " + preferences.getString("facebook_lastname", "") + "\n");
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.failed_recieve), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCancel(int code, String message) {

    }

    @Override
    public void onError(int code, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        progressDialog.dismiss();
    }

    @Override
    public void onConnected(Bundle bundle) {
        googleLogin.setText(getString(R.string.logout_google));

        updateViewGoogle();
    }

    @Override
    public void onDisconnected() {
        googleLogin.setText(R.string.login_google);
        plusClient.connect();

        updateViewGoogle();
    }

    @Override
    public void onAccessRevoked(ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            plusClient.disconnect();
        }
        plusClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
        updateViewGoogle();
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateViewFacebook();
        }
    }

    private void onClickFacebook() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            session.closeAndClearTokenInformation();
            facebookLogin.setText(getString(R.string.login_facebook));
        } else {
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(this).setPermissions("public_profile").setCallback(statusCallback));
            } else {
                Session.openActiveSession(this, true, statusCallback);

                Request request = Request.newMeRequest(session, new RequestMeFacebook(this, this));
                request.executeAsync();
            }
        }
    }

//    private void onClickTwitter() {
//        if (preferences.getBoolean("twitter", false)) {
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.remove("twitter_token");
//            editor.remove("twitter_secret");
//            editor.remove("twitter");
//            editor.commit();
//
//            twitterLogin.setText("Login with Twitter");
//        } else {
//            new RequestAccessTwitter(this, this).execute();
//        }
//    }

    private void onClickGoogle() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
            return;
        }

        if (plusClient.isConnected() && plusClient.getCurrentPerson() != null) {
            plusClient.clearDefaultAccount();
            plusClient.disconnect();
            plusClient.connect();
        } else {
            try {
                connectionResult.startResolutionForResult(this, SocialVariable.GOOGLE_REQUEST_ACCESS_TASK);
            } catch (IntentSender.SendIntentException e) {
                plusClient.connect();
            }
        }
    }

    private void onClickMervID() {
        if (preferences.getBoolean("mervid", false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("mervid");
            editor.remove("mervid_token");
            editor.remove("mervid_token_type");
            editor.remove("mervid_expires_in");
            editor.remove("mervid_scope");
            editor.remove("mervid_jti");
            editor.commit();

            mervIDlogin.setText(getString(R.string.login_merv_id));
        } else {
            try {
                Log.e("clicked", "merv id");
                OAuthClientRequest request = OAuthClientRequest.authorizationLocation(SocialVariable.MERVID_AUTH_URL)
                        .setClientId(SocialVariable.MERVID_APP_ID)
                        .setRedirectURI(SocialVariable.MERVID_CALLBACK)
                        .setScope("read write")
                        .setParameter("response_type", "code")
                        .buildQueryMessage();

                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", request.getLocationUri());
                intent.setData(Uri.parse(request.getLocationUri()));
                startActivityForResult(intent, SocialVariable.MERVID_REQUEST_ACCESS);
            } catch (OAuthSystemException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateViewGoogle() {
        if (plusClient != null && plusClient.isConnected() && plusClient.getCurrentPerson() != null) {
            googleLogin.setText(getString(R.string.logout_google));
        } else {
            googleLogin.setText(getString(R.string.login_google));
        }
    }

    private void updateViewMervID(Intent intent) {
        if (preferences.getBoolean("mervid", false)) {
            mervIDlogin.setText(getString(R.string.logout_merv_id));
        } else {
            mervIDlogin.setText(getString(R.string.login_merv_id));
        }
    }

    private void updateViewFacebook() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            facebookLogin.setText(getString(R.string.logout_facebook));
        } else {
            facebookLogin.setText(getString(R.string.login_facebook));
        }
    }

//    private void updateViewTwitter(Intent intent) {
//        if (preferences.getBoolean("twitter", false)) {
//            twitterLogin.setText(getString(R.string.logout_twitter));
//        } else {
//            twitterLogin.setText(getString(R.string.login_twitter));
//
//            Uri uri = intent.getData();
//            if (uri != null && uri.toString().startsWith(SocialVariable.TWITTER_CALLBACK)) {
//                String verifier = uri.getQueryParameter("oauth_verifier");
//
//                if (verifier != null && !"".equalsIgnoreCase(verifier)) {
//                    new RequestTokenTwitter(this, this).execute(uri, requestToken);
//                }
//            }
//        }
//    }
}