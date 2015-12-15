package io.barnabycolby.sqrlclient;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * This class allows static global access to the application resources.
 */
public class App extends Application {
    private static Context mContext;

    /**
     * Gets the application resources.
     *
     * @return The application resources.
     */
    public static Resources getApplicationResources() {
        return mContext.getResources();
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
