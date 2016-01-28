package io.barnabycolby.sqrlclient;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * This class allows static global access to the application resources.
 */
public class App extends Application {
    private static Context sContext;

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

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
