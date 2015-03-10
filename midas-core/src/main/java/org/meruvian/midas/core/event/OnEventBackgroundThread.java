package org.meruvian.midas.core.event;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public interface OnEventBackgroundThread<Execute, Success, Cancel, Failed> {
    void onEventBackgroundThread(Event.OnExecuteEvent<Execute> event);

    void onEventBackgroundThread(Event.OnSuccessEvent<Success> event);

    void onEventBackgroundThread(Event.OnCancelEvent<Cancel> event);

    void onEventBackgroundThread(Event.OnFailedEvent<Failed> event);
}
