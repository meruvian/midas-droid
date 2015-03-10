package org.meruvian.midas.core.job;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.meruvian.midas.core.event.Event;

import de.greenrobot.event.EventBus;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public abstract class DefaultJob extends Job {
    public DefaultJob(int priority) {
        super(new Params(priority).requireNetwork().persist());
    }
}
