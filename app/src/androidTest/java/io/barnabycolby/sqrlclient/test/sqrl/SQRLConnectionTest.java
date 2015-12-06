package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import android.net.Uri;
import java.net.HttpURLConnection;
import java.io.*;

import io.barnabycolby.sqrlclient.sqrl.*;

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
        String actual = connection.getURL().toExternalForm();
        String expected = "https://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD";
        Assert.assertEquals(expected, actual);

        Assert.assertTrue(connection.getDoInput());
        Assert.assertTrue(connection.getDoOutput());
    }

    @Test
    public void setCorrectMethodAndHeadersBasedOnURI() throws Exception {
        Assert.assertEquals("POST", connection.getRequestMethod());
        Assert.assertEquals(uri.getHost(), connection.getRequestProperty("Host"));
        Assert.assertEquals("SQRL/1", connection.getRequestProperty("User-Agent"));
        Assert.assertEquals("application/x-www-form-urlencoded", connection.getRequestProperty("Content-type"));
    }
}
