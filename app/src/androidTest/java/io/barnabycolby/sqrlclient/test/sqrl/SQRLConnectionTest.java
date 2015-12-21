package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;
import android.net.Uri;

import io.barnabycolby.sqrlclient.sqrl.*;

import java.io.*;
import java.net.HttpURLConnection;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SQRLConnectionTest {
    private Uri uri;
    private SQRLUri sqrlUri;
    private SQRLConnection sqrlConnection;
    private HttpURLConnection connection;

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = new SQRLUri(uri);
        sqrlConnection = new SQRLConnection(sqrlUri);
        connection = sqrlConnection.getConnection();
    }

    @Test
    public void createConnectionToTheCorrectURLAllowingIncomingAndOutgoingTraffic() throws Exception {
        String expected = "https://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD";
        checkConnectionCreatedToTheCorrectURLAllowingIncomingAndOutgoingTraffic(expected);
    }

    @Test
    public void setCorrectMethodAndHeadersBasedOnURI() throws Exception {
        checkConnectionMethodsAndHeaders();
    }

    private void checkConnectionMethodsAndHeaders() throws Exception {
        Assert.assertEquals("POST", connection.getRequestMethod());
        Assert.assertEquals(uri.getHost(), connection.getRequestProperty("Host"));
        Assert.assertEquals("SQRL/1", connection.getRequestProperty("User-Agent"));
        Assert.assertEquals("application/x-www-form-urlencoded", connection.getRequestProperty("Content-type"));
    }

    private void checkConnectionCreatedToTheCorrectURLAllowingIncomingAndOutgoingTraffic(String expectedURL) throws Exception {
        String actual = connection.getURL().toExternalForm();
        Assert.assertEquals(expectedURL, actual);

        Assert.assertTrue(connection.getDoInput());
        Assert.assertTrue(connection.getDoOutput());
    }
}
