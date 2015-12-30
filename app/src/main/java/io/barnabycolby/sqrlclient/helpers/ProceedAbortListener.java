package io.barnabycolby.sqrlclient.helpers;

/**
 * A listener interface used for decision tasks with two outcomes, proceed or abort.
 */
public interface ProceedAbortListener {
    /**
     * Called if the task should proceed.
     */
    public void proceed();

    /**
     * Called if the task should be aborted.
     */
    public void abort();
}
