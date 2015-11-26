package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import android.net.Uri;
import java.net.URLConnection;

import io.barnabycolby.sqrlclient.sqrl.*;

@RunWith(AndroidJUnit4.class)
public class SQRLRequestTest {
    private Uri uri;
    private SQRLUri sqrlUri;
    private SQRLRequest request;

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = new SQRLUri(uri);
        request = new SQRLRequest(sqrlUri);
    }

    @Test
    public void createConnectionToTheCorrectURL() throws Exception {
        // Check the correct url was used
        URLConnection connection = request.getConnection();
        String actual = connection.getURL().toExternalForm();
        String expected = "https://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setCorrectHeadersBasedOnURI() throws Exception {
        URLConnection connection = request.getConnection();
        Assert.assertEquals(uri.getHost(), connection.getRequestProperty("Host"));
        Assert.assertEquals("SQRL/1", connection.getRequestProperty("User-Agent"));
        Assert.assertEquals("application/x-www-form-urlencoded", connection.getRequestProperty("Content-type"));
    }
}
