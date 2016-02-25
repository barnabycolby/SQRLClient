package io.barnabycolby.sqrlclient.helpers;

import android.support.v4.util.SimpleArrayMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Allows listeners to be converted to detachable listeners.
 *
 * <p>
 * A detachable listener is a wrapper around a listener with additional attach/detach methods. The underlying listener object can be replaced
 * without the object calling the listener knowing about it.
 * </p>
 *
 * <p>
 * This is useful when an Activity is used as a listener. For example, an AsyncTask may call back to the Activity, but an orientation change
 * required the listener to be replaced.
 * </p>
 *
 * <p>
 * Any calls to the DetachableListener that occur during a detached phase will be replayed to the new listener on attachment.
 * Note that only the last call will be replayed if the same method is called multiple times.
 * </p>
 *
 * <p>
 * In order to utilise this class, you will need to create a new interface that extends the listener as well as
 * DetachableListener.DetachableListenerInterface. It should be passed as the second argument to DetachableListener.create
 * </p>
 */
public class DetachableListener implements InvocationHandler {
    private Object mListener;
    private SimpleArrayMap<Method, Object[]> mCallHistory;

    /**
     * Creates a new DetachableListener of the given type.
     *
     * @param listener  The listener to wrap.
     * @param detachableListenerClass  The class of the detachable interface, as described in the DetachableListener class description.
     */
    public static Object create(Object listener, Class detachableListenerClass) {
        ClassLoader classLoader = detachableListenerClass.getClassLoader();
        Class[] interfaces = { detachableListenerClass };
        InvocationHandler invocationHandler = new DetachableListener(listener);
        return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param listener  The listener to wrap.
     */
    private DetachableListener(Object listener) {
        this.mListener = listener;
        this.mCallHistory = new SimpleArrayMap<Method, Object[]>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // If the method call is for attach or detach then we handle it ourselves
        if (method.getName().equals("attach")) {
            if (args.length < 1) {
                throw new IllegalArgumentException("Not enough args provided for attach(...)");
            }
            this.attach(args[0]);
            return null;
        } else if (method.getName().equals("detach")) {
            this.detach();
            return null;
        }

        // Now we can handle the case where the method call is targeted at the listener
        else if (this.mListener == null) {
            // Record the method and args so that we can replay it later
            this.mCallHistory.put(method, args);
            return null;
        } else {
            return invokeMethodOnListener(method, args);
        }
    }

    /**
     * Invokes a given method on the wrapped listener object.
     *
     * The listener must have a method with the same signature.
     *
     * @param method  The method to invoke.
     * @param args  The method arguments.
     */
    private Object invokeMethodOnListener(Method method, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method methodOnListener = this.mListener.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
        return methodOnListener.invoke(this.mListener, args);
    }

    /**
     * Attaches a new listener to handle callbacks.
     *
     * @param listener  The new listener.
     */
    public void attach(Object listener) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Sanity check: The listener should be of the same type as the last listener
        if (this.mListener != null && (listener == null || !listener.getClass().equals(this.mListener.getClass()))) {
            throw new IllegalArgumentException("Listener was not the same type as the last listener.");
        }

        this.mListener = listener;

        // Replay any method calls
        for (int i = 0; i < this.mCallHistory.size(); i++) {
            Method method = this.mCallHistory.keyAt(i);
            Object[] args = this.mCallHistory.valueAt(i);
            invokeMethodOnListener(method, args);
        }
        this.mCallHistory.clear();
    }

    /**
     * Detaches the currently wrapped listener.
     */
    public void detach() {
        this.mListener = null;
    }

    /**
     * Required for the creation of a DetachableListener object.
     */
    public interface DetachableListenerInterface {
        public void attach(Object listener);
        public void detach();
    }
}
