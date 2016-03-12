package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.exceptions.IncorrectPasswordException;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentityManagerTest {
    private SQRLIdentityManager mIdentityManager;
    private String mIdentityName = "Rick Rubin";

    @Before
    public void setUp() throws Exception {
        this.mIdentityManager = App.getSQRLIdentityManager();
        this.mIdentityManager.save(this.mIdentityName, new byte[32], "C5E8Yz5T4&kbipkN");
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

    @Test
    public void getCurrentIdentityShouldSucceedWithCorrectPassword() throws Exception {
        String identityName = "Abraham Lincoln";
        String password = "mKkyWf*5K&@pSEcU";
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("5A078E19A846275E34F525895BA90AA0CC1FA274B5582E121FA216E30CBB04A7");

        // Create the mock Uri
        SQRLUri uri = mock(SQRLUri.class);
        when(uri.getHost()).thenReturn("reddit.com");

        this.mIdentityManager.save(identityName, masterKey, password);
        this.mIdentityManager.setCurrentIdentity(identityName);
        this.mIdentityManager.getCurrentIdentityForSite(uri, password);
    }

    @Test public void getCurrentIdentityShouldThrowExceptionWithIncorrectPassword() throws Exception {
        String identityName = "Barack Obama";
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("148A68E32EAB1C0D78A142C415D993E91044577CDA40B153B60CC40AC087F704");

        // Create the mock Uri
        final SQRLUri uri = mock(SQRLUri.class);
        when(uri.getHost()).thenReturn("cr.yp.to");

        this.mIdentityManager.save(identityName, masterKey, "AGBLQ^e91ot5&Qdy");
        this.mIdentityManager.setCurrentIdentity(identityName);

        // Assert that an IncorrectPasswordException is thrown
        io.barnabycolby.sqrlclient.test.Helper.assertExceptionThrown(IncorrectPasswordException.class, new Lambda() {
            public void run() throws Exception {
                mIdentityManager.getCurrentIdentityForSite(uri, "TJjt*G9GP$@M&c*D");
            }
        });
    }
}
