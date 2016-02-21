package io.barnabycolby.sqrlclient.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;

import java.util.List;

/**
 * Spinner that displays the list of identities associated with this application, allowing the user to select a current identity.
 */
public class IdentitySpinner extends Spinner implements AdapterView.OnItemSelectedListener {
    private static String TAG = IdentitySpinner.class.getName();

    private SQRLIdentityManager mIdentityManager;

    public IdentitySpinner(Context context) {
        super(context);
        initialise(context);
    }

    public IdentitySpinner(Context context, int mode) {
        super(context, mode);
        initialise(context);
    }

    public IdentitySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    public IdentitySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    public IdentitySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        initialise(context);
    }

    public IdentitySpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
        initialise(context);
    }

    public IdentitySpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
        initialise(context);
    }

    /**
     * This serves as the constructor for this class.
     *
     * As the Spinner class has many constructor, we support them all by simply calling their super version and then calling this method.
     */
    private void initialise(Context context) {
        this.mIdentityManager = App.getSQRLIdentityManager();

        this.setOnItemSelectedListener(this);
        this.populateItems(context);
    }

    /**
     * Repopulates the spinner items, should be called if any alterations have been made to the list of identities.
     *
     * @param context  The current context that this spinner exists in.
     */
    public void repopulate(Context context) {
        this.populateItems(context);
    }

    private void populateItems(Context context) {
        // Populate the spinner
        List<String> identityNames = this.mIdentityManager.getIdentityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.identity_spinner_item, identityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);

        // Set the currently selected identity
        if (identityNames.size() > 0) {
            // Check whether a current identity has already been selected
            String currentIdentityName = this.mIdentityManager.getCurrentIdentityName();
            if (currentIdentityName != null && identityNames.contains(currentIdentityName)) {
                this.setSelection(adapter.getPosition(currentIdentityName));
            } else {
                try {
                    this.mIdentityManager.setCurrentIdentity((String)this.getSelectedItem());
                } catch (IdentityDoesNotExistException ex) {
                    Log.wtf(TAG, "Identity name taken from SQRLIdentityManager.getIdentityNames() list does not exist according to setCurrentIdentity.");
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * Called when an item in the identity spinner is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedIdentityName = (String)parent.getItemAtPosition(position);
        try {
            this.mIdentityManager.setCurrentIdentity(selectedIdentityName);
        } catch (IdentityDoesNotExistException ex) {
            Log.wtf(TAG, "Identity selected from identity spinner does not exist.");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
