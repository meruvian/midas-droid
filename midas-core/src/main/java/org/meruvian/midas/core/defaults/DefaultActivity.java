package org.meruvian.midas.core.defaults;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.meruvian.midas.core.R;

import butterknife.ButterKnife;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public abstract class DefaultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());

        ButterKnife.inject(this);

        onViewCreated();

//        setTheme(getThemes());
    }

    protected abstract int layout();

    public abstract void onViewCreated();

//    private int getThemes() {
//        SharedPreferences sessionPreference = PreferenceManager.getDefaultSharedPreferences(this);
//
//        return sessionPreference.getInt("themes", sessionPreference.getInt("themes", R.style.DarkTheme_Midas));
//    }
}
