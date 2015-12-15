package io.barnabycolby.sqrlclient.helpers;

import android.os.AsyncTask;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper around AsyncTask that makes it more testable.
 *
 * Using this class only requires one slight difference to the use of AsyncTask. If you override onPostExecute then it must call
 * executionFinished() after all of it's other code.
 * <p>
 * When using this for testing, call enableTestMode() immediately after construction. Once you have initialised the task appropriately, call
 * execute(...) and then await(). This ensures that the test waits until execution of the task has completed before allowing the test to
 * proceed to it's assertions.
 */
public abstract class TestableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private CountDownLatch countDownLatch;

    /**
     * This should be called whenever this task is being tested, immediately after the objects construction.
     *
     * It ensures that the appropriate internal state is set to allow the async task to be tested.
     */
    public void enableTestMode() {
        this.countDownLatch = new CountDownLatch(1);
    }

    /**
     * Should be called once all work and post execution work has been completed.
     */
    protected void executionFinished() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    /**
     * This default implementation of onPostExecute simply calls executionFinished().
     *
     * This allows AsyncTasks that do not implement onPostExecute to be made testable (by implementing this class), without any changes.
     *
     * @param result The result of the execution.
     */
    protected void onPostExecute(Result result) {
        this.executionFinished();
    }

    /**
     * Call this to wait for the execution and post execution of the task to complete.
     *
     * @throws InterruptedException  if the thread is interrupted during wait.
     * @throws Exception  if enableTestMode() has not been called.
     */
    public void await() throws Exception {
        if (this.countDownLatch == null) {
            throw new Exception(App.getApplicationResources().getString(R.string.enable_test_mode));
        }

        this.countDownLatch.await();
    }

    /**
     * Call this to wait for the execution and post execution of the task to complete, or return early if timeout is reached.
     *
     * @param timeout  The timeout length.
     * @param unit  The unit of the timeout length.
     * @return False if interrupted, true if signalled normally.
     * @throws InterruptedException  if the thread is interrupted during wait.
     * @throws Exception  if enableTestMode() has not been called.
     */
    public boolean await(int timeout, TimeUnit unit) throws Exception {
        if (this.countDownLatch == null) {
            throw new Exception(App.getApplicationResources().getString(R.string.enable_test_mode));
        }

        return this.countDownLatch.await(timeout, unit);
    }
}
