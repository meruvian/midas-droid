package org.meruvian.midas.showcase.activity.social;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.activity.WebViewActivity;
import org.meruvian.midas.social.task.facebook.RequestTokenFacebook;
import org.meruvian.midas.social.task.google.RequestTokenGoogle;
import org.meruvian.midas.social.task.mervid.RequestTokenMervID;

public class SocialLoginActivity extends DefaultActivity implements TaskService {
    private Button facebookLogin, googleLogin, /*twitterLogin,*/ mervIDlogin;
    private ProgressDialog progressDialog;
    private TextView usersText;
    private WebView webOauth;

    private SharedPreferences preferences;
    private StringBuilder users = new StringBuilder("");

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

        updateViewFacebook(getIntent());
//        updateViewTwitter(getIntent());
        updateViewMervID(getIntent());
        updateViewGoogle(getIntent());
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SocialVariable.MERVID_REQUEST_ACCESS) {
                new RequestTokenMervID(this, this).execute(parseCode(data));
            } else if (requestCode == SocialVariable.FACEBOOK_REQUEST_ACCESS) {
                new RequestTokenFacebook(this, this).execute(parseCode(data));
            } else if (requestCode == SocialVariable.GOOGLE_REQUEST_ACCESS) {
                new RequestTokenGoogle(this, this).execute(parseCode(data));
            }
        }
    }

    @Override
    public void onExecute(int code) {
        progressDialog = new ProgressDialog(this);

        if (code == SocialVariable.TWITTER_REQUEST_ACCESS_TASK || code == SocialVariable.TWITTER_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_twitter));
        } else if (code == SocialVariable.MERVID_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_merv_id));
        } else if (code == SocialVariable.FACEBOOK_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_facebook));
        } else if (code == SocialVariable.GOOGLE_REQUEST_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.signing_in_google));
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
            } else*/
            if (code == SocialVariable.MERVID_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    mervIDlogin.setText(getString(R.string.logout_merv_id));
                }
            } else if (code == SocialVariable.FACEBOOK_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    facebookLogin.setText(getString(R.string.logout_facebook));
                }
            } else if (code == SocialVariable.GOOGLE_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    googleLogin.setText(getString(R.string.logout_google));
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

    private void onClickFacebook() {
        if (preferences.getBoolean("facebook", false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("facebook");
            editor.remove("facebook_token");
            editor.remove("facebook_token_type");
            editor.remove("facebook_expires_in");
            editor.remove("facebook_scope");
            editor.remove("facebook_jti");
            editor.commit();

            facebookLogin.setText(getString(R.string.login_facebook));
        } else {
            try {
                OAuthClientRequest request = OAuthClientRequest.authorizationLocation(SocialVariable.MERVID_AUTH_URL)
                        .setClientId(SocialVariable.MERVID_APP_ID)
                        .setRedirectURI(SocialVariable.MERVID_CALLBACK)
                        .setScope("read write")
                        .setParameter("response_type", "code")
                        .setParameter("social", "facebook")
                        .buildQueryMessage();

                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", request.getLocationUri());
                intent.setData(Uri.parse(request.getLocationUri()));
                startActivityForResult(intent, SocialVariable.FACEBOOK_REQUEST_ACCESS);
            } catch (OAuthSystemException e) {
                e.printStackTrace();
            }
        }

//        Session session = Session.getActiveSession();
//        if (session.isOpened()) {
//            session.closeAndClearTokenInformation();
//            facebookLogin.setText(getString(R.string.login_facebook));
//        } else {
//            if (!session.isOpened() && !session.isClosed()) {
//                session.openForRead(new Session.OpenRequest(this).setPermissions("public_profile").setCallback(statusCallback));
//            } else {
//                Session.openActiveSession(this, true, statusCallback);
//
//                Request request = Request.newMeRequest(session, new RequestMeFacebook(this, this));
//                request.executeAsync();
//            }
//        }
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
        if (preferences.getBoolean("google", false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("google");
            editor.remove("google_token");
            editor.remove("google_token_type");
            editor.remove("google_expires_in");
            editor.remove("google_scope");
            editor.remove("google_jti");
            editor.commit();

            googleLogin.setText(getString(R.string.login_google));
        } else {
            try {
                OAuthClientRequest request = OAuthClientRequest.authorizationLocation(SocialVariable.MERVID_AUTH_URL)
                        .setClientId(SocialVariable.MERVID_APP_ID)
                        .setRedirectURI(SocialVariable.MERVID_CALLBACK)
                        .setScope("read write")
                        .setParameter("response_type", "code")
                        .setParameter("social", "google")
                        .buildQueryMessage();

                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", request.getLocationUri());
                intent.setData(Uri.parse(request.getLocationUri()));
                startActivityForResult(intent, SocialVariable.GOOGLE_REQUEST_ACCESS);
            } catch (OAuthSystemException e) {
                e.printStackTrace();
            }
        }

//        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (code != ConnectionResult.SUCCESS) {
//            GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
//            return;
//        }
//
//        if (plusClient.isConnected() && plusClient.getCurrentPerson() != null) {
//            plusClient.clearDefaultAccount();
//            plusClient.disconnect();
//            plusClient.connect();
//        } else {
//            try {
//                connectionResult.startResolutionForResult(this, SocialVariable.GOOGLE_REQUEST_ACCESS_TASK);
//            } catch (IntentSender.SendIntentException e) {
//                plusClient.connect();
//            }
//        }
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

    private void updateViewGoogle(Intent intent) {
//        if (plusClient != null && plusClient.isConnected() && plusClient.getCurrentPerson() != null) {
//            googleLogin.setText(getString(R.string.logout_google));
//        } else {
//            googleLogin.setText(getString(R.string.login_google));
//        }

        if (preferences.getBoolean("google", false)) {
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

    private void updateViewFacebook(Intent intent) {
//        Session session = Session.getActiveSession();
//        if (session.isOpened()) {
//            facebookLogin.setText(getString(R.string.logout_facebook));
//        } else {
//            facebookLogin.setText(getString(R.string.login_facebook));
//        }

        if (preferences.getBoolean("facebook", false)) {
            facebookLogin.setText(getString(R.string.logout_facebook));
        } else {
            facebookLogin.setText(getString(R.string.login_facebook));
        }
    }

    private String parseCode(Intent data) {
        Uri uri = data.getData();
        if (uri != null && uri.toString().startsWith(SocialVariable.MERVID_CALLBACK)) {
            String code = uri.getQueryParameter("code");

            if (code != null && !"".equalsIgnoreCase(code)) {
                return code;
            }
        }

        return "null";
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