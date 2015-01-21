package org.meruvian.midas.core.defaults;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.meruvian.midas.core.R;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public class DefaultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setTheme(getThemes());
    }

//    private int getThemes() {
//        SharedPreferences sessionPreference = PreferenceManager.getDefaultSharedPreferences(this);
//
//        return sessionPreference.getInt("themes", sessionPreference.getInt("themes", R.style.DarkTheme_Midas));
//    }
}
