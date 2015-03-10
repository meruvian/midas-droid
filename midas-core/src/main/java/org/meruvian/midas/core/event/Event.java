package org.meruvian.midas.core.event;

/**
 * Created by ludviantoovandi on 06/03/15.
 */
public class Event {
//    private OnExecuteEvent executeEvent;
//    private OnSuccessEvent successEvent;
//    private OnCancelEvent cancelEvent;
//    private OnFailedEvent failedEvent;
//
//    public Event(OnExecuteEvent executeEvent) {
//        this(executeEvent, null, null, null);
//    }
//
//    public Event(OnSuccessEvent successEvent) {
//        this(null, successEvent, null, null);
//    }
//
//    public Event(OnCancelEvent cancelEvent) {
//        this(null, null, cancelEvent, null);
//    }
//
//    public Event(OnFailedEvent failedEvent) {
//        this(null, null, null, failedEvent);
//    }
//
//    public OnCancelEvent getCancelEvent() {
//        return cancelEvent;
//    }
//
//    public OnExecuteEvent getExecuteEvent() {
//        return executeEvent;
//    }
//
//    public OnFailedEvent getFailedEvent() {
//        return failedEvent;
//    }
//
//    public OnSuccessEvent getSuccessEvent() {
//        return successEvent;
//    }

//    public Event(OnExecuteEvent executeEvent, OnSuccessEvent successEvent, OnCancelEvent cancelEvent, OnFailedEvent failedEvent) {
//        this.executeEvent = executeEvent;
//        this.successEvent = successEvent;
//        this.cancelEvent = cancelEvent;
//        this.failedEvent = failedEvent;
//    }

    public static class OnExecuteEvent<R> {
        private int processId;
        private R message;

        public OnExecuteEvent(int processId, R message) {
            this.processId = processId;
            this.message = message;
        }

        public int getProcessId() {
            return this.processId;
        }

        public R getResult() {
            return this.message;
        }
    }

    public static class OnSuccessEvent<R> {
        private int processId;
        private R result;

        public OnSuccessEvent(int processId, R result) {
            this.processId = processId;
            this.result = result;
        }

        public int getProcessId() {
            return this.processId;
        }

        public R getResult() {
            return this.result;
        }
    }

    public static class OnCancelEvent<R> {
        private int processId;
        private R result;

        public OnCancelEvent(int processId, R result) {
            this.processId = processId;
            this.result = result;
        }

        public int getProcessId() {
            return this.processId;
        }

        public R getResult() {
            return this.result;
        }
    }

    public static class OnFailedEvent<R> {
        private int processId;
        private R message;

        public OnFailedEvent(int processId, R message) {
            this.processId = processId;
            this.message = message;
        }

        public int getProcessId() {
            return this.processId;
        }

        public R getResult() {
            return this.message;
        }
    }
}
