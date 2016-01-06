package io.barnabycolby.sqrlclient.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.R;

import java.util.Arrays;
import java.util.List;

/**
 * Activity used to allow the user to create a new SQRL identity.
 *
 * The activity harvests entropy from the camera and uses it to create a new SQRL identity.
 */
public class CreateNewIdentityActivity extends AppCompatActivity {
    private int CAMERA_PERMISSION_REQUEST = 0;

    private String mUnrecoverableErrorKey = "error";
    private String mErrorStringKey = "errorString";
    private boolean mUnrecoverableErrorOccurred = false;
    private CameraManager mCameraManager;
    private String[] mCameraIds;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_identity);

        // Restore the value of mUnrecoverableErrorOccurred if it exists
        if (savedInstanceState != null) {
            this.mUnrecoverableErrorOccurred = savedInstanceState.getBoolean(this.mUnrecoverableErrorKey, false);
        }

        if (this.mUnrecoverableErrorOccurred) {
            restore(savedInstanceState);
        } else {
            initialise();
        }
    }

    private void initialise() {
        // Get the camera manager
        this.mCameraManager = (CameraManager)this.getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager == null) {
            displayErrorMessage(R.string.camera_service_not_supported);
            return;
        }

        try {
            // Check if the system has any cameras
            mCameraIds = mCameraManager.getCameraIdList();
            if (mCameraIds.length == 0) {
                displayErrorMessage(R.string.no_cameras);
                return;
            }

            // Check for, and possibly request, permission to use the camera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } catch (CameraAccessException ex) {
            displayErrorMessage(ex.getMessage());
            return;
        }
    }

    private void restore(Bundle savedInstanceState) {
        String errorMessage = savedInstanceState.getString(this.mErrorStringKey);
        displayErrorMessage(errorMessage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(this.mUnrecoverableErrorKey, this.mUnrecoverableErrorOccurred);
        if (this.mUnrecoverableErrorOccurred) {
            TextView errorTextView = (TextView)this.findViewById(R.id.ErrorTextView);
            outState.putString(this.mErrorStringKey, errorTextView.getText().toString());
        }
    }

    private void displayErrorMessage(int stringId) {
        String errorMessage = this.getResources().getString(stringId);
        displayErrorMessage(errorMessage);
    }

    private void displayErrorMessage(String errorMessage) {
        // Hide the main content
        View mainContent = this.findViewById(R.id.MainContent);
        mainContent.setVisibility(View.GONE);

        // Display the error message
        TextView errorTextView = (TextView)this.findViewById(R.id.ErrorTextView);
        errorTextView.setText(errorMessage);
        errorTextView.setVisibility(View.VISIBLE);

        // Set the error flag so that instance state is handled correctly
        this.mUnrecoverableErrorOccurred = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted();
        } else {
            displayErrorMessage(R.string.camera_permission_not_granted);
            return;
        }
    }

    private void cameraPermissionGranted() {
        try {
            // Attempt to open a camera
            mCameraManager.openCamera(mCameraIds[0], new CameraDevice.StateCallback() {
                @Override
                public void onDisconnected(CameraDevice camera) {
                    displayErrorMessage(R.string.camera_disconnected);
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    displayErrorMessage(R.string.camera_error_occurred);
                }

                @Override
                public void onOpened(CameraDevice camera) {
                    cameraOpened(camera);
                }
            }, null);
        } catch (CameraAccessException ex) {
            displayErrorMessage(ex.getMessage());
            return;
        }
    }

    private void cameraOpened(final CameraDevice camera) {
        TextureView cameraPreview = (TextureView)findViewById(R.id.CameraPreview);
        cameraPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                surfaceTextureAvailable(camera, surfaceTexture);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {}

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}
        });
    }

    private void surfaceTextureAvailable(final CameraDevice camera, SurfaceTexture surfaceTexture) {
        final Surface surface = new Surface(surfaceTexture);
        List<Surface> surfaces = Arrays.asList(surface);

        try {
            camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    displayErrorMessage(R.string.camera_configuration_failed);
                }

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCameraSession = session;
                    cameraSessionCreated(camera, surface);
                }
            }, null);
        } catch (CameraAccessException ex) {
            displayErrorMessage(ex.getMessage());
            return;
        }
    }

    private void cameraSessionCreated(CameraDevice camera, Surface surface) {
        try {
            // Build the capture request
            CaptureRequest.Builder captureRequestBuilder;
            captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            this.mCaptureRequest = captureRequestBuilder.build();

            // Perform the capture
            startCameraCapture();
        } catch (CameraAccessException ex) {
            displayErrorMessage(ex.getMessage());
            return;
        }
    }

    private void startCameraCapture() {
        if (mCameraSession != null && mCaptureRequest != null) {
            try {
                mCameraSession.setRepeatingRequest(mCaptureRequest, null, null);
            } catch (CameraAccessException ex) {
                displayErrorMessage(ex.getMessage());
                return;
            }
        }
    }
}
