package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import android.net.Uri;
import java.net.URLConnection;

import io.barnabycolby.sqrlclient.sqrl.*;

@RunWith(AndroidJUnit4.class)
public class SQRLRequestTest {
    @Test
    public void createConnectionToTheCorrectURLWithCorrectRequestProperties() throws Exception {
        // Create the SQRL URI
        String baseUri = "://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD";
        Uri uri = Uri.parse("sqrl" + baseUri);
        SQRLUri sqrlUri = new SQRLUri(uri);
        SQRLRequest request = new SQRLRequest(sqrlUri);

        // Check the url used for the connection
        URLConnection connection = request.getConnection();
        String actual = connection.getURL().toExternalForm();
        String expected = "https" + baseUri;
        Assert.assertEquals(expected, actual);

        // Check the request properties are correct
        Assert.assertEquals(uri.getHost(), connection.getRequestProperty("Host"));
        Assert.assertEquals("SQRL/1", connection.getRequestProperty("User-Agent"));
        Assert.assertEquals("application/x-www-form-urlencoded", connection.getRequestProperty("Content-type"));
    }
}
