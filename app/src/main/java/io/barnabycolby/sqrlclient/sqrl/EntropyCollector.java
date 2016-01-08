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

    /**
     * Constructs an instance of the class using the characteristics of the camera that will be used.
     *
     * @param cameraCharacteristics  The characteristics of the camera that will be used to collect entropy.
     * @throws RawUnsupportedException  If the raw format is unsupported by the given camera.
     */
    public EntropyCollector(CameraCharacteristics cameraCharacteristics) throws RawUnsupportedException {
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
}
