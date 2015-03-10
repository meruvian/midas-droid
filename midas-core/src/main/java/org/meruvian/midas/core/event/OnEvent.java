package org.meruvian.midas.core.event;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public interface OnEvent<E> {
    void onEvent(Event event);
}
