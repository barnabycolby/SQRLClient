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
public class SQRLRequestTest {
    private Uri uri;
    private SQRLUri sqrlUri;
    private SQRLIdentity sqrlIdentity;
    private SQRLRequest request;
    private String serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = new SQRLUri(uri);
        sqrlIdentity = getMockSQRLIdentity();
        request = new SQRLRequest(sqrlUri, sqrlIdentity, new RealSQRLResponseFactory());
    }

    @Test
    public void createConnectionToTheCorrectURLAllowingIncomingAndOutgoingTraffic() throws Exception {
        HttpURLConnection connection = request.getConnection();
        String actual = connection.getURL().toExternalForm();
        String expected = "https://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD";
        Assert.assertEquals(expected, actual);

        Assert.assertTrue(connection.getDoInput());
        Assert.assertTrue(connection.getDoOutput());
    }

    @Test
    public void setCorrectMethodAndHeadersBasedOnURI() throws Exception {
        HttpURLConnection connection = request.getConnection();
        Assert.assertEquals("POST", connection.getRequestMethod());
        Assert.assertEquals(uri.getHost(), connection.getRequestProperty("Host"));
        Assert.assertEquals("SQRL/1", connection.getRequestProperty("User-Agent"));
        Assert.assertEquals("application/x-www-form-urlencoded", connection.getRequestProperty("Content-type"));
    }

    @Test
    public void correctlyGenerateQueryRequest() throws Exception {
        // First, we need to mock the connection object and the writer object
        HttpURLConnection connection = mock(HttpURLConnection.class);
        // We create a partial mock so that we can verify the final message (by calling to string)
        // without having to specify how the message should be constructed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(connection.getOutputStream()).thenReturn(spyOutputStream);
        when(connection.getResponseCode()).thenReturn(200);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(serverResponse.getBytes());
        when(connection.getInputStream()).thenReturn(inputStream);
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity();

        // Next, instantiate a SQRLRequest object with the mocked objects
        request = new SQRLRequest(sqrlUri, sqrlIdentity, new RealSQRLResponseFactory(), connection);

        // Calculate what the expected data should be
        String expectedData = "client=dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        expectedData += "&server=c3FybDovL3d3dy5ncmMuY29tL3Nxcmw_bnV0PVAyS3JfNEdCNDlHcndBRl9rcER1SkEmc2ZuPVIxSkQ";
        expectedData += "&ids=je8rKDoBUnS0PdAyYNQQ-RpZ1YtI_bj4dTZCRnKDvTAcG1Vj_FQtPZlnKeajGFlZCJMH2JRWyBkRs5Y747drDw";

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = spyOutputStream.toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    private SQRLIdentity getMockSQRLIdentity() throws Exception { 
        // We also need to mock the SQRLIdentity object so that we know the keys that will be returned
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        String identityKey = "Jjl2OhUyP93M14-AQ3stYMaoZ2vq1BHfmAhxWjM1CuU";
        when(sqrlIdentity.getIdentityKey()).thenReturn(identityKey);
        // This is the expected client and server values
        String expectedClientValue = "dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = "c3FybDovL3d3dy5ncmMuY29tL3Nxcmw_bnV0PVAyS3JfNEdCNDlHcndBRl9rcER1SkEmc2ZuPVIxSkQ";
        String expectedDataToSign = expectedClientValue + expectedServerValue;
        String signatureOfExpectedData = "je8rKDoBUnS0PdAyYNQQ-RpZ1YtI_bj4dTZCRnKDvTAcG1Vj_FQtPZlnKeajGFlZCJMH2JRWyBkRs5Y747drDw";
        when(sqrlIdentity.signUsingIdentityPrivateKey(expectedDataToSign)).thenReturn(signatureOfExpectedData);

        return sqrlIdentity;
    }
}
