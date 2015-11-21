package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import android.net.Uri;

import io.barnabycolby.sqrlclient.*;

@RunWith(AndroidJUnit4.class)
public class SQRLUriTest {

    @Test
    public void throwExceptionForUnknownScheme() throws Exception {
        // Mock the Uri class
        Uri uri = getUriSchemeAndNut("abc", "Y0thBhbZk3N9DSxgTgfhXg");

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(uri);
        } catch (UnknownSchemeException ex) {
            return;
        }

        Assert.fail("UnknownSchemeException was not thrown.");
    }

    @Test
    public void throwExceptionForMissingNut() throws Exception {
        // Mock the Uri class
        Uri uri = getUriSchemeAndNut("sqrl", null);

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(uri);
        } catch (NoNutException ex) {
            return;
        }

        Assert.fail("NoNutException was not thrown.");
    }

    @Test
    public void noExceptionThrownForValidSQRLUri() throws Exception {
        // Mock the Uri class
        Uri uri = getUriSchemeAndNut("qrl", "Y0thBhbZk3N9DSxgTgfhXg");

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(uri);
        } catch (Exception ex) {
            Assert.fail("NoNutException was not thrown.");
        }
    }
    
    @Test
    public void displayNameMatchesHostname() throws Exception {
        String hostname = "www.grc.com";
        Uri uri = Uri.parse("sqrl://" + hostname + "/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w");
        SQRLUri sqrlUri = new SQRLUri(uri);
        Assert.assertEquals(hostname, sqrlUri.getDisplayName());
    }

    @Test
    public void displayNameMatchesFriendlyName() throws Exception {
        Uri uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD");
        SQRLUri sqrlUri = new SQRLUri(uri);
        Assert.assertEquals("GRC", sqrlUri.getDisplayName());
    }

    private Uri getUriSchemeAndNut(String scheme, String nut) {
        String uri = scheme + "://www.grc.com/sqrl";
        if (nut != null) {
            uri += "?nut=" + nut;
        }

        return Uri.parse(uri);
    }
}
