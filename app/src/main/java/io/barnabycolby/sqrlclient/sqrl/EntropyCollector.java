package io.barnabycolby.sqrlclient.sqrl;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;

import io.barnabycolby.sqrlclient.exceptions.RawUnsupportedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Collects images from the camera in order to harvest the entropy, which is used to generate a random value.
 *
 * The camera used to collect images is specified by the user. The user should retrieve the surface from this class which should then be passed
 * to a camera capture session. Once enough entropy is collected, the final random value can be obtained.
 *
 * Some of the camera-related code was taken directly from the Android-Camera2Raw example project which can be found at:
 * https://github.com/googlesamples/android-Camera2Raw/blob/master/Application/src/main/java/com/example/android/camera2raw/Camera2RawFragment.java
 */
public class EntropyCollector implements ImageReader.OnImageAvailableListener, AutoCloseable {
    private ImageReader mImageReader;
    private int mProgress = 0;
    private ProgressListener mProgressListener;

    /**
     * Constructs an instance of the class using the characteristics of the camera that will be used.
     *
     * @param cameraCharacteristics  The characteristics of the camera that will be used to collect entropy.
     * @throws RawUnsupportedException  If the raw format is unsupported by the given camera.
     */
    public EntropyCollector(CameraCharacteristics cameraCharacteristics) throws RawUnsupportedException {
        initialise(cameraCharacteristics);
    }

    /**
     * Initialises this object using the characteristics of the camera that will be used to collect entropy.
     *
     * @param cameraCharacteristics  The characteristics of the camera that will be used to collect entropy.
     * @throws RawUnsupportedException  If the raw format is unsupported by the given camera.
     */
    private void initialise(CameraCharacteristics cameraCharacteristics) throws RawUnsupportedException {
        // First we need to check that the camera supports the raw capability
        int[] availableCapabilities = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        int rawCapability = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW;
        if (!contains(availableCapabilities, rawCapability)) {
            throw new RawUnsupportedException();
        }

        // Get the largest available raw camera size
        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size largestRawSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());

        // Instantiate the image reader
        this.mImageReader = ImageReader.newInstance(largestRawSize.getWidth(), largestRawSize.getHeight(), ImageFormat.RAW_SENSOR, /*maxImages*/ 5);
        this.mImageReader.setOnImageAvailableListener(this, null);
    }

    /**
     * Reinitialises this object with a new set of characteristics to describe a potentially different camera, which will be used to collect entropy.
     * 
     * @param cameraCharacteristics  The characteristics of the camera.
     * @throws RawUnsupportedException  If the raw format is unsupported by the given camera.
     */
    public void reinitialise(CameraCharacteristics cameraCharacteristics) throws RawUnsupportedException {
        this.mImageReader.close();
        initialise(cameraCharacteristics);
    }

    /**
     * Gets the surface that should be passed to the camera session.
     *
     * @return The surface.
     */
    public Surface getSurface() {
        return this.mImageReader.getSurface();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            image.close();
        }

        updateProgressValue(this.mProgress + 5);
    }

    @Override
    public void close() {
        this.mImageReader.close();
    }

    /**
     * Used to compare two sizes by calculating their area.
     * (Taken from the Android Camera2RAW example.)
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure that multiplications overflow
            return Long.signum((long)lhs.getWidth() * lhs.getHeight() -
                    (long)rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * Return true if the given array contains the given integer.
     *
     * @param modes  Array to check.
     * @param mode  Integer to get for.
     * @return True if the array contains the given integer, otherwise false.
     */
    private static boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i : modes) {
            if (i == mode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the internal progress value to the given value, informing any progress listeners if necessary.
     *
     * TODO: Invoke the listener callbacks on the threads looper.
     *
     * @param newProgressValue  The new progress value.
     */
    private void updateProgressValue(int newProgressValue) {
        int oldProgressValue = this.mProgress;
        this.mProgress = newProgressValue;
        
        if (this.mProgressListener != null) {
            this.mProgressListener.onEntropyCollectionProgressUpdate(newProgressValue);

            if (oldProgressValue < 100 && newProgressValue >= 100) {
                this.mProgressListener.onEntropyCollectionFinished();
            }
        }
    }

    /**
     * Listener interface used to retrieve entropy collection progress updates.
     */
    public interface ProgressListener {
        /**
         * Called when an update is available for the entropy collection progress.
         *
         * <p>
         * Note that the progress value may actually be &gt;100 as the entropy collection continues if more images are available.
         * It continues as the more entropy we collect the better.
         * </p>
         *
         * @param progress The updated progress value as a percentage value (0-100)
         */
        public void onEntropyCollectionProgressUpdate(int progress);

        /**
         * Called when the entropy collection has finished.
         *
         * <p>
         * This function is called when the entropy collection value reaches 100. Note that the entropy collection never actually finishes,
         * it continues as long as images are available for processing.
         * </p>
         */
        public void onEntropyCollectionFinished();
    }

    /**
     * Sets a new listener to receive updates about the progress of the entropy collection.
     *
     * @param newListener  The new progress listener.
     */
    public void setProgressListener(ProgressListener newListener) {
        this.mProgressListener = newListener;
    }

    /**
     * Detaches the current progress listener, preventing it from receiving future updates.
     */
    public void detachProgressListener() {
        this.mProgressListener = null;
    }

    /**
     * Gets the latest entropy collection progress value.
     *
     * <p>
     * Note that the entropy collection value may be &gt; than 100 as entropy collection continues as long as more images are available.
     * </p>
     *
     * @return The current progress value (0-100).
     */
    public int getProgress() {
        return this.mProgress;
    }
}