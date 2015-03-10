package org.meruvian.midas.core.event;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public interface OnEventMainThread<Execute, Success, Cancel, Failed> {
    void onEventMainThread(Event.OnExecuteEvent<Execute> event);

    void onEventMainThread(Event.OnSuccessEvent<Success> event);

    void onEventMainThread(Event.OnCancelEvent<Cancel> event);

    void onEventMainThread(Event.OnFailedEvent<Failed> event);
}
