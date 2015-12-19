package io.barnabycolby.sqrlclient.test.sqrl;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import java.io.*;
import java.net.HttpURLConnection;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import io.barnabycolby.sqrlclient.exceptions.TransientErrorException;
import io.barnabycolby.sqrlclient.sqrl.*;

@RunWith(AndroidJUnit4.class)
public class SQRLQueryRequestTest {
    private Uri uri;
    private SQRLUri sqrlUri;

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = new SQRLUri(uri);
    }

    @Test
    public void correctlyGenerateQueryRequest() throws Exception {
        // First, we need to mock the connection object
        SQRLConnection connection = SQRLRequestTest.getMockSQRLConnection(this.sqrlUri);

        // Mock the SQRLIdentity
        String expectedClientValue = "dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = "c3FybDovL3d3dy5ncmMuY29tL3Nxcmw_bnV0PVAyS3JfNEdCNDlHcndBRl9rcER1SkEmc2ZuPVIxSkQ";
        String signatureOfExpectedData = "je8rKDoBUnS0PdAyYNQQ-RpZ1YtI_bj4dTZCRnKDvTAcG1Vj_FQtPZlnKeajGFlZCJMH2JRWyBkRs5Y747drDw";
        SQRLIdentity sqrlIdentity = SQRLRequestTest.getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLQueryRequest request = new SQRLQueryRequest(connection, sqrlIdentity, new MockSQRLResponseFactory());

        // Calculate what the expected data should be
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + signatureOfExpectedData;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = connection.getOutputStream().toString();
        Assert.assertEquals(expectedData, dataSent);
    }
}
