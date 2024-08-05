package com.tkiani.plugins.browser;

/**
 * EventGroup class is used to handle indeterminate sequence of events.
 * It is used to track the completion of a group of events.
 */
class EventGroup {

    /**
     * Callback interface for event group completion.
     */
    interface CompletionCallback {
        /**
         * Called when the event group is completed.
         */
        void onCompletion();
    }

    // Number of events in the event group
    private int eventCount;

    // Flag to indicate if the event group is completed
    private boolean isCompleted;

    // Callback for event group completion
    private CompletionCallback completionCallback;

    /**
     * Constructs an EventGroup object.
     *
     * @param completionCallback Callback for event group completion.
     */
    EventGroup(CompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
    }

    /**
     * Enters the event group.
     * Increments the event count.
     */
    void enter() {
        eventCount++;
    }

    /**
     * Leaves the event group.
     * Decrements the event count and checks for completion.
     */
    void leave() {
        eventCount--;
        checkForCompletion();
    }

    /**
     * Resets the event group.
     * Resets the event count and completion flag.
     */
    void reset() {
        eventCount = 0;
        isCompleted = false;
    }

    /**
     * Checks if the event group is completed.
     * If the event count is zero, the event group is completed,
     * the completion flag is not set and the completion callback is not null,
     * then the completion callback is called and the completion flag is set.
     */
    private void checkForCompletion() {
        if (eventCount <= 0 && !isCompleted && completionCallback != null) {
            completionCallback.onCompletion();
            isCompleted = true;
        }
    }
}