package io.barnabycolby.sqrlclient;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;

/**
 * This class allows static global access to the application resources.
 */
public class App extends Application {
    private static Context sContext;
    private static SQRLIdentityManager sIdentityManager;

    /**
     * Gets the application resources.
     *
     * @return The application resources.
     */
    public static Resources getApplicationResources() {
        return sContext.getResources();
    }

    /**
     * Gets the application context.
     *
     * @return The application context.
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * Gets the SQRLIdentityManager associated with this application instance.
     *
     * If an instance does not already exist, it will be created.
     */
    public static SQRLIdentityManager getSQRLIdentityManager() throws IdentitiesCouldNotBeLoadedException {
        if (sIdentityManager == null) {
            sIdentityManager = new SQRLIdentityManager();
        }

        return sIdentityManager;
    }

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
