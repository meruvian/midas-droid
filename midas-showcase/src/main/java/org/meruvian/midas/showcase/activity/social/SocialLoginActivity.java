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
import org.meruvian.midas.showcase.activity.LoginActivity;
import org.meruvian.midas.showcase.activity.MainActivity;
import org.meruvian.midas.showcase.fragment.news.NewsViewFragment;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.activity.WebViewActivity;
import org.meruvian.midas.social.task.facebook.RefreshTokenFacebook;
import org.meruvian.midas.social.task.facebook.RequestAccessFacebook;
import org.meruvian.midas.social.task.facebook.RequestTokenFacebook;
import org.meruvian.midas.social.task.google.RefreshTokenGoogle;
import org.meruvian.midas.social.task.google.RequestAccessGoogle;
import org.meruvian.midas.social.task.google.RequestTokenGoogle;
import org.meruvian.midas.social.task.mervid.RefreshTokenMervID;
import org.meruvian.midas.social.task.mervid.RequestAccessMervID;
import org.meruvian.midas.social.task.mervid.RequestTokenMervID;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class SocialLoginActivity extends DefaultActivity implements TaskService {
    @InjectViews({R.id.button_facebook_login, R.id.button_google_login, R.id.button_mervid_login})
    List<Button> logins;
    @InjectView(R.id.text_skip)
    TextView skip;

    private ProgressDialog progressDialog;

    private SharedPreferences preferences;
    // Twitter
//    private RequestToken requestToken;


    @Override
    public int layout() {
        return R.layout.activity_social_login;
    }

    @Override
//    protected void onCreate(Bundle savedInstanceState) {
    public void onViewCreated() {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_social_login);
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayShowHomeEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setTitle(R.string.social_login);
        setTitle(R.string.social_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

//        facebookLogin = (Button) findViewById(R.id.button_facebook_login);
//        twitterLogin = (Button) findViewById(R.id.button_twitter_login);
//        googleLogin = (Button) findViewById(R.id.button_google_login);
//        mervIDlogin = (Button) findViewById(R.id.button_mervid_login);
//        skip = (TextView) findViewById(R.id.text_skip);



//        twitterLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickTwitter();
//            }
//        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SocialLoginActivity.this, LoginActivity.class));
                finish();
            }
        });

        updateViewFacebook();
//        updateViewTwitter(getIntent());
        updateViewMervID();
        updateViewGoogle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
        } else if (code == SocialVariable.MERVID_REQUEST_ACCESS) {
            progressDialog.setMessage(getString(R.string.access_merv_id));
        } else if (code == SocialVariable.GOOGLE_REQUEST_ACCESS) {
            progressDialog.setMessage(getString(R.string.access_google));
        } else if (code == SocialVariable.FACEBOOK_REQUEST_ACCESS) {
            progressDialog.setMessage(getString(R.string.access_facebook));
        } else if (code == SocialVariable.MERVID_REFRESH_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.update_merv_id));
        } else if (code == SocialVariable.FACEBOOK_REFRESH_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.update_facebook));
        } else if (code == SocialVariable.GOOGLE_REFRESH_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.update_google));
        }

        progressDialog.show();
    }

    @OnClick({R.id.button_facebook_login, R.id.button_google_login, R.id.button_mervid_login})
    public void setOnClickListener(Button button) {
        if (button.getId() == R.id.button_facebook_login) {
            onClickFacebook();
        } else if (button.getId() == R.id.button_google_login) {
            onClickGoogle();
        } else if (button.getId() == R.id.button_mervid_login) {
            onClickMervID();
        }
    }

    @OnLongClick({R.id.button_facebook_login, R.id.button_google_login, R.id.button_mervid_login})
    public boolean setOnLongClickListener(Button button) {
        if (button.getId() == R.id.button_facebook_login) {
            onLongClickFacebook();
        } else if (button.getId() == R.id.button_google_login) {
            onLongClickGoogle();
        } else if (button.getId() == R.id.button_mervid_login) {
            onLongClickMervID();
        }

        return true;
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
                    logins.get(2).setText(getString(R.string.logout_merv_id));
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else if (code == SocialVariable.FACEBOOK_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    logins.get(0).setText(getString(R.string.logout_facebook));
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else if (code == SocialVariable.GOOGLE_REQUEST_TOKEN_TASK) {
                String result = (String) object;
                if (result != null && !"".equalsIgnoreCase(result)) {
                    logins.get(1).setText(getString(R.string.logout_google));
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else if (code == SocialVariable.MERVID_REQUEST_ACCESS) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", object.toString());
                intent.setData(Uri.parse(object.toString()));
                startActivityForResult(intent, SocialVariable.MERVID_REQUEST_ACCESS);
            } else if (code == SocialVariable.FACEBOOK_REQUEST_ACCESS) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", object.toString());
                intent.setData(Uri.parse(object.toString()));
                startActivityForResult(intent, SocialVariable.FACEBOOK_REQUEST_ACCESS);
            } else if (code == SocialVariable.GOOGLE_REQUEST_ACCESS) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", object.toString());
                intent.setData(Uri.parse(object.toString()));
                startActivityForResult(intent, SocialVariable.GOOGLE_REQUEST_ACCESS);
            } else if (code == SocialVariable.MERVID_REFRESH_TOKEN_TASK) {
                Toast.makeText(this, getString(R.string.finish_update_merv_id), Toast.LENGTH_LONG).show();
            } else if (code == SocialVariable.FACEBOOK_REFRESH_TOKEN_TASK) {
                Toast.makeText(this, getString(R.string.finish_update_facebook), Toast.LENGTH_LONG).show();
            } else if (code == SocialVariable.GOOGLE_REFRESH_TOKEN_TASK) {
                Toast.makeText(this, getString(R.string.finish_update_google), Toast.LENGTH_LONG).show();
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

            logins.get(0).setText(getString(R.string.login_facebook));
        } else {
            new RequestAccessFacebook(this, this).execute();
        }
    }

    private void onLongClickFacebook() {
        if (preferences.getBoolean("facebook", false)) {
            new RefreshTokenFacebook(this, this).execute();
        } else {
            Toast.makeText(this, getString(R.string.login_facebook), Toast.LENGTH_SHORT).show();
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
        if (preferences.getBoolean("google", false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("google");
            editor.remove("google_token");
            editor.remove("google_token_type");
            editor.remove("google_expires_in");
            editor.remove("google_scope");
            editor.remove("google_jti");
            editor.commit();

            logins.get(1).setText(getString(R.string.login_google));
        } else {
            new RequestAccessGoogle(this, this).execute();
        }
    }

    private void onLongClickGoogle() {
        if (preferences.getBoolean("google", false)) {
            new RefreshTokenGoogle(this, this).execute();
        } else {
            Toast.makeText(this, getString(R.string.login_google), Toast.LENGTH_SHORT).show();
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

            logins.get(2).setText(getString(R.string.login_merv_id));
        } else {
            new RequestAccessMervID(this, this).execute();
        }
    }

    private void onLongClickMervID() {
        if (preferences.getBoolean("mervid", false)) {
            new RefreshTokenMervID(this, this).execute(preferences.getString("mervid_token", ""));
        } else {
            Toast.makeText(this, getString(R.string.login_merv_id), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViewGoogle() {
        if (preferences.getBoolean("google", false)) {
            logins.get(1).setText(getString(R.string.refresh_google));
        } else {
            logins.get(1).setText(getString(R.string.login_google));
        }
    }

    private void updateViewMervID() {
        if (preferences.getBoolean("mervid", false)) {
            logins.get(2).setText(getString(R.string.refresh_merv_id));
        } else {
            logins.get(2).setText(getString(R.string.login_merv_id));
        }
    }

    private void updateViewFacebook() {
        if (preferences.getBoolean("facebook", false)) {
            logins.get(0).setText(getString(R.string.refresh_facebook));
        } else {
            logins.get(0).setText(getString(R.string.login_facebook));
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