package org.meruvian.midas.core;

import android.app.Application;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public abstract class MidasApplication extends Application {
    private static MidasApplication instance;
    private JobManager jobManager;

    public MidasApplication() {
        this.instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        jobManager = new JobManager(getApplicationContext(), configJobManager());

        onCreated();
    }

    public abstract void onCreated();

    public Configuration configJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this).customLogger(
                new CustomLogger() {
                    private static final String TAG = "JOB";

                    @Override
                    public boolean isDebugEnabled() {
                        return false;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }
                }
        );
        builder.minConsumerCount(1);
        builder.maxConsumerCount(3);
        builder.loadFactor(3);
        builder.consumerKeepAlive(120); // count by minute

        return builder.build();
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public static MidasApplication getInstance() {
        return instance;
    }
}
