package org.meruvian.midas.core.defaults;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.meruvian.midas.core.R;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public class DefaultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getThemes());
    }

    private int getThemes() {
        SharedPreferences sessionPreference = PreferenceManager.getDefaultSharedPreferences(this);

        return sessionPreference.getInt("themes", sessionPreference.getInt("themes", R.style.DarkTheme_Midas));
    }
}
