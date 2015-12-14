package io.barnabycolby.sqrlclient.helpers;

import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class TestableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private CountDownLatch countDownLatch;

    public void enableTestMode() {
        this.countDownLatch = new CountDownLatch(1);
    }

    protected void executionFinished() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    protected void onPostExecute(Result result) {
        this.executionFinished();
    }

    public void await() throws Exception {
        if (this.countDownLatch == null) {
            throw new Exception("You must enable test mode before calling await.");
        }

        this.countDownLatch.await();
    }

    public boolean await(int timeout, TimeUnit unit) throws Exception {
        if (this.countDownLatch == null) {
            throw new Exception("You must enable test mode before calling await.");
        }

        return this.countDownLatch.await(timeout, unit);
    }
}
