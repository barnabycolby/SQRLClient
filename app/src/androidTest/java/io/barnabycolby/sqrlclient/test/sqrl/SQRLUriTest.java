package io.barnabycolby.sqrlclient.test.sqrl;

import android.os.Parcel;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.test.Helper;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void throwExceptionWhenUpdateQueryCalledWithInvalidQuery() throws Exception {
        String uriAsString = "sqrl://www.grc.com/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD";
        Uri uri = Uri.parse(uriAsString);
        SQRLUri sqrlUri = new SQRLUri(uri);

        String queryString = "/foo(bar)baz quux";
        try {
            sqrlUri.updatePathAndQuery(queryString);
        } catch (MalformedURLException ex) {
            return;
        }

        Assert.fail("MalformedURLException was not thrown when updateQuery called with \"" + queryString + "\"");
    }

    @Test
    public void throwExceptionWhenUpdateQueryCalledWithoutNut() throws Exception {
        String uriAsString = "sqrl://www.grc.com/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD";
        Uri uri = Uri.parse(uriAsString);
        SQRLUri sqrlUri = new SQRLUri(uri);

        String queryString = "/auth?sfn=R1JD";
        try {
            sqrlUri.updatePathAndQuery(queryString);
        } catch (NoNutException ex) {
            return;
        }

        Assert.fail("NoNutException was not thrown when updateQuery called with \"" + queryString + "\"");
    }

    @Test
    public void correctlyUpdateUriWhenUpdatePathAndQueryCalled() throws Exception {
        String originalPathAndQuery = "/sqrl?nut=rOL2Cj3VMlyfRhwOTAl-7w&sfn=R1JD";
        String baseUri = "sqrl://www.grc.com";
        String uriAsString = baseUri + originalPathAndQuery;
        Uri uri = Uri.parse(uriAsString);
        SQRLUri sqrlUri = new SQRLUri(uri);

        String newPathAndQueryString = "/auth?nut=xrLqqZwU8Xpk71NfAD2mOQ";
        sqrlUri.updatePathAndQuery(newPathAndQueryString);

        String expectedUri = baseUri + newPathAndQueryString;
        Assert.assertEquals(expectedUri, sqrlUri.getFullUriAsString());
    }

    @Test
    public void parcelAndUnparcelCreatesTheSameObject() throws Exception {
        Uri uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=uediASXDPlsTTYBI-x7s3g&sfn=R1JD");
        SQRLUri sqrlUri = new SQRLUri(uri);

        // Parcel and unparcel the identity
        Parcel parcel = Parcel.obtain();
        sqrlUri.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        SQRLUri recreatedSQRLUri = SQRLUri.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        
        // Check that the two instances are equivalent
        assertEquals(sqrlUri, recreatedSQRLUri);
    }

    @Test
    public void throwExceptionForUriWithoutScheme() throws Exception {
        final Uri uri = Uri.parse("6VraX@9qf8r0$BX0");
        Helper.assertExceptionThrown(UnknownSchemeException.class, new Lambda() {
            public void run() throws Exception {
                new SQRLUri(uri);
            }
        });
    }

    private Uri getUriSchemeAndNut(String scheme, String nut) {
        String uri = scheme + "://www.grc.com/sqrl";
        if (nut != null) {
            uri += "?nut=" + nut;
        }

        return Uri.parse(uri);
    }
}
