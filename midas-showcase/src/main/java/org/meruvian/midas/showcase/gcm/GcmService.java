package org.meruvian.midas.showcase.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.meruvian.midas.core.notification.DefaultNotification;
import org.meruvian.midas.showcase.R;

/**
 * Created by ludviantoovandi on 13/08/14.
 */
public class GcmService extends IntentService {
    public static final int NOTIF_ID = 1;

    public GcmService() {
        super("GcmIntentService");
    }

    public void notify(Bundle bundle) {
        new DefaultNotification(getApplicationContext(), NOTIF_ID) {

            @Override
            public String title() {
                return "Notifikasi";
            }

            @Override
            public String contentText() {
                return "Hasil tes notifikasi";
            }

            @Override
            public int smallIcon() {
                return R.drawable.ic_launcher;
            }
        }.show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging.getInstance(this);

        if (!bundle.isEmpty()) {
            notify(bundle);
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
