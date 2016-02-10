package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentityManagementTest {
    private SQRLIdentityManager mIdentityManager;
    private String mIdentityName = "Rick Rubin";

    @Before
    public void setUp() throws Exception {
        this.mIdentityManager = App.getSQRLIdentityManager();
        this.mIdentityManager.save(this.mIdentityName, new byte[32]);
    }

    @After
    public void tearDown() throws Exception {
        this.mIdentityManager.removeAllIdentities();
    }

    @Test
    public void setCurrentIdentityFailsIfIdentityDoesNotExist() {
        try {
            this.mIdentityManager.setCurrentIdentity("Jason Statham");
            fail("IdentityDoesNotExistException was not thrown.");
        } catch (IdentityDoesNotExistException ex) {
            // This indicates success!
        }
    }

    @Test
    public void setCurrentIdentitySucceedsForNullAndValidIdenityNames() throws Exception {
        this.mIdentityManager.setCurrentIdentity(this.mIdentityName);
        this.mIdentityManager.setCurrentIdentity(null);
    }

    @Test
    public void getCurrentIdentityNameReturnsLastSuccessfullySetIdentity() throws Exception {
        assertNull(this.mIdentityManager.getCurrentIdentityName());
        this.mIdentityManager.setCurrentIdentity(this.mIdentityName);
        assertEquals(this.mIdentityName, this.mIdentityManager.getCurrentIdentityName());
        try {
            this.mIdentityManager.setCurrentIdentity("Kylie Minogue");
        } catch (IdentityDoesNotExistException ex) {}
        assertEquals(this.mIdentityName, this.mIdentityManager.getCurrentIdentityName());
        this.mIdentityManager.setCurrentIdentity(null);
        assertNull(this.mIdentityManager.getCurrentIdentityName());
    }

    @Test
    public void deletingCurrentlySetIdentitySetsCurrentIdentityToNull() throws Exception {
        this.mIdentityManager.setCurrentIdentity(this.mIdentityName);
        this.mIdentityManager.removeIdentity(this.mIdentityName);
        assertNull(this.mIdentityManager.getCurrentIdentityName());
    }
}
