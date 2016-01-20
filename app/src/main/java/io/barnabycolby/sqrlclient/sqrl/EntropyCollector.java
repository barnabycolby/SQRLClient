package io.barnabycolby.sqrlclient.sqrl;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;

import io.barnabycolby.sqrlclient.exceptions.RawUnsupportedException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.abstractj.kalium.NaCl.sodium;

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
    // The more entropy the better, in practice, this target seems to be a good trade-off between collection time and amount of entropy
    private long TARGET_ENTROPY_IN_BITS = 50 * 1024 * 1024;

    private ImageReader mImageReader;
    private int mProgress = 0;
    private ProgressListener mProgressListener;
    private byte[] mCumulativeHash;
    private long mEntropyBitsCollected = 0;
    private AtomicBoolean mReadyToProcessNextImage = new AtomicBoolean(true);

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
        // Get the image
        final Image image = reader.acquireLatestImage();
        if (image == null) {
            return;
        }

        if (mReadyToProcessNextImage.get()) {
            mReadyToProcessNextImage.set(false);
            Thread cumulativeHashUpdateThread = new Thread(new Runnable() {
                @Override
                public void run () {
                    // Add the image data to the hash
                    Image.Plane[] imagePlanes = image.getPlanes();
                    for (Image.Plane plane : imagePlanes) {
                        ByteBuffer byteBuffer = plane.getBuffer();
                        if (byteBuffer.hasArray()) {
                            byte[] planeData = byteBuffer.array();
                            addEntropyToCumulativeHash(planeData);
                        } else {
                            // Because the data contained in the buffer will likely be very large, we add it in smaller pieces
                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            while (byteBuffer.hasRemaining()) {
                                if (byteBuffer.remaining() < bufferSize) {
                                    buffer = new byte[byteBuffer.remaining()];
                                }
                                byteBuffer.get(buffer);
                                addEntropyToCumulativeHash(buffer);
                            }
                        }
                    }

                    // Make sure we close the image to avoid running out of memory
                    image.close();

                    mReadyToProcessNextImage.set(true);
                }
            });
            cumulativeHashUpdateThread.start();
        }
    }
    
    /**
     * Uses the given data as entropy, which is mixed into the existing cumulative hash to generate a new cumulative value.
     *
     * This method also notifies any listeners of the progress.
     *
     * @param data  A byte array representing the data that should be added to the hash.
     */
    private void addEntropyToCumulativeHash(byte[] data) {
        byte[] dataToHash;
        if (this.mCumulativeHash == null) {
            dataToHash = data;
        } else {
            // Create a byte array containing the new data and the existing cumulative hash
            // This ensures that the entropy from the old hash is preserved in the result
            dataToHash = new byte[data.length + this.mCumulativeHash.length];
            System.arraycopy(data, 0, dataToHash, 0, data.length);
            System.arraycopy(this.mCumulativeHash, 0, dataToHash, data.length, this.mCumulativeHash.length);
        }

        // Update the cumulative hash
        byte[] newCumulativeHash = new byte[32];
        sodium().crypto_hash_sha256(newCumulativeHash, dataToHash, dataToHash.length);
        this.mCumulativeHash = newCumulativeHash;

        // Update the progress
        this.mEntropyBitsCollected = this.mEntropyBitsCollected + (data.length * 8);
        int newProgressValue = (int)((double)this.mEntropyBitsCollected / (double)this.TARGET_ENTROPY_IN_BITS);
        updateProgressValue(newProgressValue);
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
