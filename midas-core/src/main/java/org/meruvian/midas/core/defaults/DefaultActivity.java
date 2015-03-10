package org.meruvian.midas.core.defaults;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.path.android.jobqueue.JobManager;

import org.meruvian.midas.core.MidasApplication;
import org.meruvian.midas.core.R;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public abstract class DefaultActivity extends ActionBarActivity {

    protected JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());

        ButterKnife.inject(this);

        onViewCreated();

//        setTheme(getThemes());
    }

    public void registerJob() {
        jobManager = MidasApplication.getInstance().getJobManager();
    }

    public void registerEvent() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void postEvent(Object object) {
        EventBus.getDefault().post(object);
    }

    protected abstract int layout();

    public abstract void onViewCreated();

//    private int getThemes() {
//        SharedPreferences sessionPreference = PreferenceManager.getDefaultSharedPreferences(this);
//
//        return sessionPreference.getInt("themes", sessionPreference.getInt("themes", R.style.DarkTheme_Midas));
//    }
}
