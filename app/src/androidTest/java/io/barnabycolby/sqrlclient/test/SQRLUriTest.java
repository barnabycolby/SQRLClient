package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import android.net.Uri;

import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.exceptions.*;

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
        String sqrlNut = "Y0thBhbZk3N9DSxgTgfhXg";
        Uri qrlUri = getUriSchemeAndNut("qrl", sqrlNut);
        Uri sqrlUri = getUriSchemeAndNut("sqrl", sqrlNut);
        Uri QRLUri = getUriSchemeAndNut("QRL", sqrlNut);
        Uri SQRLUri = getUriSchemeAndNut("SQRL", sqrlNut);

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(qrlUri);
            new SQRLUri(sqrlUri);
            new SQRLUri(QRLUri);
            new SQRLUri(SQRLUri);
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

    @Test
    public void calculateCommunicationURLSuccessfully() throws Exception {
        String baseUrl = "://www.grc.com/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD";
        Uri uri = Uri.parse("sqrl" + baseUrl);
        SQRLUri sqrlUri = new SQRLUri(uri);
        Assert.assertEquals("https" + baseUrl, sqrlUri.getCommunicationURL());

        uri = Uri.parse("qrl" + baseUrl);
        sqrlUri = new SQRLUri(uri);
        Assert.assertEquals("http" + baseUrl, sqrlUri.getCommunicationURL());
    }

    @Test
    public void getFullUriAsStringReturnsUriAsString() throws Exception {
        String uriAsString = "sqrl://www.grc.com/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD";
        Uri uri = Uri.parse(uriAsString);
        SQRLUri sqrlUri = new SQRLUri(uri);
        Assert.assertEquals(uriAsString, sqrlUri.getFullUriAsString());
    }

    private Uri getUriSchemeAndNut(String scheme, String nut) {
        String uri = scheme + "://www.grc.com/sqrl";
        if (nut != null) {
            uri += "?nut=" + nut;
        }

        return Uri.parse(uri);
    }
}
