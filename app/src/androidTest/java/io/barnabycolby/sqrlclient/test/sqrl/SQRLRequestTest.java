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
public class SQRLRequestTest {
    private Uri uri;
    private SQRLUri sqrlUri;
    private String serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = new SQRLUri(uri);
    }

    @Test
    public void correctlyGenerateQueryRequest() throws Exception {
        // First, we need to mock the connection object and the writer object
        SQRLConnection connection = mock(SQRLConnection.class);
        when(connection.getSQRLUri()).thenReturn(this.sqrlUri);
        // We create a partial mock so that we can verify the final message (by calling to string)
        // without having to specify how the message should be constructed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(connection.getOutputStream()).thenReturn(spyOutputStream);
        when(connection.getResponseCode()).thenReturn(200);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(serverResponse.getBytes());
        when(connection.getInputStream()).thenReturn(inputStream);
        // Mock the SQRLIdentity
        String expectedClientValue = "dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = "c3FybDovL3d3dy5ncmMuY29tL3Nxcmw_bnV0PVAyS3JfNEdCNDlHcndBRl9rcER1SkEmc2ZuPVIxSkQ";
        String signatureOfExpectedData = "je8rKDoBUnS0PdAyYNQQ-RpZ1YtI_bj4dTZCRnKDvTAcG1Vj_FQtPZlnKeajGFlZCJMH2JRWyBkRs5Y747drDw";
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLRequest request = new SQRLRequest(connection, sqrlIdentity, new RealSQRLResponseFactory());

        // Calculate what the expected data should be
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + signatureOfExpectedData;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = spyOutputStream.toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    @Test
    public void shouldResendRequestUsingNewNutAndQryIfTransientErrorOccurs() throws Exception {
        // Create the SQRLConnection mock
        SQRLConnection connection = mock(SQRLConnection.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(connection.getSQRLUri()).thenReturn(this.sqrlUri);
        when(connection.getOutputStream()).thenReturn(spyOutputStream);
        doNothing().when(connection).updatePathAndQuery((String)notNull());

        // Mock the SQRLIdentity
        String expectedClientValue = "dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9NjANCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        String signatureOfExpectedData = "dgb3f59FwSxG5n2nRW0WQ14uAsZAJYmL22XNWWvoJFFKXVVEHE5fpYRh3wDQvq4lmx0iOFFUAhE6kBRA8eOEAQ";
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorRetryThenSucceedFactory sqrlResponseFactory = new TransientErrorRetryThenSucceedFactory(connection, expectedServerValue);

        SQRLRequest request = new SQRLRequest(connection, sqrlIdentity, sqrlResponseFactory);
        request.send();

        // Verify that the connection uses the new URL
        verify(connection).updatePathAndQuery(sqrlResponseFactory.getQry());

        // Verify that the second message used the servers last reply for the server parameter
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + signatureOfExpectedData;
        Assert.assertEquals(expectedData, sqrlResponseFactory.getDataSentAfterException());
    }

    @Test
    public void shouldThrowTransientErrorExceptionIfItOccursTwice() throws Exception {
        // Create the SQRLConnection mock
        SQRLConnection connection = mock(SQRLConnection.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(connection.getSQRLUri()).thenReturn(this.sqrlUri);
        when(connection.getOutputStream()).thenReturn(spyOutputStream);
        doNothing().when(connection).updatePathAndQuery((String)notNull());

        // Mock the SQRLIdentity
        String expectedClientValue = "dmVyPTENCmNtZD1xdWVyeQ0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9NjANCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        String signatureOfExpectedData = "dgb3f59FwSxG5n2nRW0WQ14uAsZAJYmL22XNWWvoJFFKXVVEHE5fpYRh3wDQvq4lmx0iOFFUAhE6kBRA8eOEAQ";
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorEveryTimeFactory sqrlResponseFactory = new TransientErrorEveryTimeFactory(expectedServerValue);

        SQRLRequest request = new SQRLRequest(connection, sqrlIdentity, sqrlResponseFactory);

        try {
            request.send();
        } catch (TransientErrorException ex) {
            return;
        }

        Assert.fail("TransientErrorException should have been thrown the second time a TransientErrorException occurred.");
    }

    private SQRLIdentity getMockSQRLIdentity(String expectedClientValue, String expectedServerValue, String signatureOfExpectedData) throws Exception { 
        // We also need to mock the SQRLIdentity object so that we know the keys that will be returned
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        String identityKey = "Jjl2OhUyP93M14-AQ3stYMaoZ2vq1BHfmAhxWjM1CuU";
        when(sqrlIdentity.getIdentityKey()).thenReturn(identityKey);
        // This is the expected client and server values
        String expectedDataToSign = expectedClientValue + expectedServerValue;
        when(sqrlIdentity.signUsingIdentityPrivateKey(expectedDataToSign)).thenReturn(signatureOfExpectedData);

        return sqrlIdentity;
    }
}
