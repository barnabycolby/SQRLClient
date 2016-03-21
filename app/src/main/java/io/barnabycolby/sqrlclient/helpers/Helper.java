package io.barnabycolby.sqrlclient.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;

/**
 * A helper class that contains static functions, encapsulating common functionality across the codebase.
 */
public class Helper {
    /**
     * Ensures that a given lambda is run on the UI thread.
     *
     * @param activity  Provides access to the runOnUiThread method.
     * @param lambda  The lambda to run.
     *
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

    /**
     * Checks whether any identities still exist, starting the NoIdentity activity if not.
     *
     * @param context  The context that should be used to start the NoIdentity activity.
     *
     * @return True if some identities exist, and false if not. Note that false also indicates an intent has been started for the NoIdentityActivity.
     */
    public static boolean checkIdentitiesExist(Context context) {
        // Check whether there are actually some identities
        if (!App.getSQRLIdentityManager().containsIdentities()) {
            Intent intent = new Intent(context, NoIdentityActivity.class);
            context.startActivity(intent);
            return false;
        }

        return true;
    }

    // Taken from http://stackoverflow.com/questions/8890174/in-java-how-do-i-convert-a-hex-string-to-a-byte
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] array = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            array[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }

        return array;
    }
}
