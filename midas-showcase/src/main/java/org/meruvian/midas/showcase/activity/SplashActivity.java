package org.meruvian.midas.showcase.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.activity.social.SocialLoginActivity;
import org.meruvian.midas.showcase.fragment.news.NewsViewFragment;
import org.meruvian.midas.social.SocialVariable;
import org.meruvian.midas.social.task.facebook.RefreshTokenFacebook;
import org.meruvian.midas.social.task.facebook.RequestAccessFacebook;
import org.meruvian.midas.social.task.google.RefreshTokenGoogle;
import org.meruvian.midas.social.task.google.RequestAccessGoogle;
import org.meruvian.midas.social.task.mervid.RefreshTokenMervID;
import org.meruvian.midas.social.task.mervid.RequestAccessMervID;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ludviantoovandi on 15/10/14.
 */
public class SplashActivity extends DefaultActivity implements TaskService {
    private SharedPreferences preferences;

    private ProgressDialog progressDialog;

    @InjectView(R.id.button_refresh)
    Button refresh;

    @Override
    public int layout() {
        return R.layout.splash_activity;
    }

    @Override
    public void onViewCreated() {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.splash_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (preferences.getBoolean("mervid", false)) {
                    new RefreshTokenMervID(SplashActivity.this, SplashActivity.this).execute(preferences.getString("mervid_refresh_token", ""));
                } else {
                    Intent mainIntent;
                    if (preferences.contains("manual") || preferences.contains("mervid")) {
                        mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        mainIntent = new Intent(SplashActivity.this, SocialLoginActivity.class);
                    }
                    SplashActivity.this.startActivity(mainIntent);
//                    startActivity(new Intent(SplashActivity.this, NewsViewFragment.class));
//                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, NewsViewFragment.class));
                    SplashActivity.this.finish();
                }
            }
        }, 2000);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RefreshTokenMervID(SplashActivity.this, SplashActivity.this).execute(preferences.getString("mervid_token", ""));
            }
        });
    }

    @Override
    public void onExecute(int code) {
        refresh.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);
        if (code == SocialVariable.MERVID_REFRESH_TOKEN_TASK) {
            progressDialog.setMessage(getString(R.string.update_merv_id));
        }
        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, Object result) {
        progressDialog.dismiss();

        if (result != null) {
            if (code == SocialVariable.MERVID_REFRESH_TOKEN_TASK) {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);

                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }
    }

    @Override
    public void onCancel(int code, String message) {
        progressDialog.dismiss();
    }

    @Override
    public void onError(int code, String message) {
        progressDialog.dismiss();

        refresh.setVisibility(View.VISIBLE);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
