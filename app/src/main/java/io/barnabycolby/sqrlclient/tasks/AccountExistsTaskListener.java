package io.barnabycolby.sqrlclient.tasks;

/**
 * A listener used to handle the result of the AccountExistsTask.
 */
public interface AccountExistsTaskListener {
    public void onAccountAlreadyExists();
    public void onAccountDoesNotAlreadyExist();
}
