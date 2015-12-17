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
    public void shouldResendRequestUsingNewNutAndQryIfTransientErrorOccurs() throws Exception {
        // Create the SQRLConnection mock
        SQRLConnection connection = mock(SQRLConnection.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(connection.getSQRLUri()).thenReturn(this.sqrlUri);
        when(connection.getOutputStream()).thenReturn(spyOutputStream);
        doNothing().when(connection).updatePathAndQuery((String)notNull());

        // Mock the SQRLIdentity
        String expectedClientValue = "dmVyPTENCmNtZD10ZXN0DQppZGs9SmpsMk9oVXlQOTNNMTQtQVEzc3RZTWFvWjJ2cTFCSGZtQWh4V2pNMUN1VQ0K";
        String expectedServerValue = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9NjANCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        String signatureOfExpectedData = "h9hvfEq0u21TD2QkxsBiYwyPWw9VBjCd9WfB5oOpJy0CSODIvEMjYlNu5cJRHyQqb5sq0bDaGWutzjFxBXjnDA";
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorRetryThenSucceedFactory sqrlResponseFactory = new TransientErrorRetryThenSucceedFactory(connection, expectedServerValue);

        SQRLTestRequest request = new SQRLTestRequest(connection, sqrlIdentity, sqrlResponseFactory, false);
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
        String expectedClientValue = "dmVyPTENCmNtZD10ZXN0DQppZGs9SmpsMk9oVXlQOTNNMTQtQVEzc3RZTWFvWjJ2cTFCSGZtQWh4V2pNMUN1VQ0K";
        String expectedServerValue = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9NjANCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        String signatureOfExpectedData = "h9hvfEq0u21TD2QkxsBiYwyPWw9VBjCd9WfB5oOpJy0CSODIvEMjYlNu5cJRHyQqb5sq0bDaGWutzjFxBXjnDA";
        SQRLIdentity sqrlIdentity = getMockSQRLIdentity(expectedClientValue, expectedServerValue, signatureOfExpectedData);

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorEveryTimeFactory sqrlResponseFactory = new TransientErrorEveryTimeFactory(expectedServerValue);

        SQRLTestRequest request = new SQRLTestRequest(connection, sqrlIdentity, sqrlResponseFactory, false);

        try {
            request.send();
        } catch (TransientErrorException ex) {
            return;
        }

        Assert.fail("TransientErrorException should have been thrown the second time a TransientErrorException occurred.");
    }

    public static SQRLIdentity getMockSQRLIdentity(String expectedClientValue, String expectedServerValue, String signatureOfExpectedData) throws Exception { 
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
