package io.barnabycolby.sqrlclient.helpers;

import android.app.Activity;
import android.os.Looper;

/**
 * A helper class that contains static functions, encapsulating common functionality across the codebase.
 */
public class Helper {
    /**
     * Ensures that a given lambda is run on the UI thread.
     *
     * @param lambda  The lambda to run.
     * @throws Exception  If the lambda throws an exception.
     */
    public static void runOnUIThread(Activity activity, final Lambda lambda) throws Exception {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            lambda.run();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lambda.run();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }
}
