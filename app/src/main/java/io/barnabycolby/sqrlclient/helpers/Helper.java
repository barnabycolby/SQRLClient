package io.barnabycolby.sqrlclient.helpers;

import android.app.Activity;
import android.os.Looper;

public class Helper {
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
