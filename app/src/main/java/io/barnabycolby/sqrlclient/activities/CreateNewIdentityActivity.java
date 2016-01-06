package io.barnabycolby.sqrlclient.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.R;

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    }
}
