package org.meruvian.midas.showcase.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import org.meruvian.midas.showcase.R;

/**
 * Created by ludviantoovandi on 15/10/14.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent =  new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }
}
