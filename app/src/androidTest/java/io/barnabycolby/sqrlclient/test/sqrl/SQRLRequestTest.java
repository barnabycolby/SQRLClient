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
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLConnectionFactory;

@RunWith(AndroidJUnit4.class)
public class SQRLRequestTest {
    private Uri uri;
    private SQRLUri sqrlUri;

    /**
     * These values represent a standard request.
     */
    private static String defaultExpectedClientValue = "dmVyPTENCmNtZD10ZXN0DQppZGs9SmpsMk9oVXlQOTNNMTQtQVEzc3RZTWFvWjJ2cTFCSGZtQWh4V2pNMUN1VQ0K";
    private static String defaultExpectedServerValue = "c3FybDovL3d3dy5ncmMuY29tL3Nxcmw_bnV0PVAyS3JfNEdCNDlHcndBRl9rcER1SkEmc2ZuPVIxSkQ";
    private static String defaultSignatureOfExpectedData = "CLTMX_JDFk0ir0G5IOeF1MHPUEjjGw6cuVlCu1y3UTs2HlLDjp-I41ltGwnUmQxEexAXOOhxwP9N91cnZWafCg";

    /**
     * These variables should be used for the second request in the chain, the server value will be replaced with the content of the last response, instead of the encoded uri.
     */
    private static String expectedTransientServerValue = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9NjANCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
    private static String signatureOfExpectedTransientData = "h9hvfEq0u21TD2QkxsBiYwyPWw9VBjCd9WfB5oOpJy0CSODIvEMjYlNu5cJRHyQqb5sq0bDaGWutzjFxBXjnDA";
    private SQRLConnection mConnection;
    private SQRLConnectionFactory mConnectionFactory;
    private HttpURLConnection mHttpURLConnection;

    @Before
    public void setUp() throws Exception {
        // Create the SQRL URI
        uri = Uri.parse("sqrl://www.grc.com/sqrl?nut=P2Kr_4GB49GrwAF_kpDuJA&sfn=R1JD");
        sqrlUri = spy(new SQRLUri(uri));

        // Create the required mocks
        this.mConnection = mock(SQRLConnection.class);
        this.mConnectionFactory = mock(SQRLConnectionFactory.class);
        this.mHttpURLConnection = mock(HttpURLConnection.class);
        when(mConnectionFactory.create()).thenReturn(mConnection);
        when(mConnectionFactory.create(any(String.class))).thenReturn(mConnection);
        when(mConnection.getConnection()).thenReturn(mHttpURLConnection);
        when(mConnection.getSQRLUri()).thenReturn(sqrlUri);
        
        // We create a partial mock so that we can verify the final message (by calling to string)
        // without having to specify how the message should be constructed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream spyOutputStream = spy(outputStream);
        when(mHttpURLConnection.getOutputStream()).thenReturn(spyOutputStream);
    }

    //region TESTS

    @Test
    public void shouldResendRequestUsingNewNutAndQryIfTransientErrorOccurs() throws Exception {
        // Create the required mocks
        SQRLIdentity sqrlIdentity = getMockTransientSQRLIdentity();

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorRetryThenSucceedFactory sqrlResponseFactory = new TransientErrorRetryThenSucceedFactory(mConnection, expectedTransientServerValue);

        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, sqrlResponseFactory, false);
        request.send();

        // Verify that the connection uses the new URL
        verify(mConnectionFactory).create(sqrlResponseFactory.getQry());

        // Verify that the second message used the servers last reply for the server parameter
        String expectedData = "client=" + defaultExpectedClientValue;
        expectedData += "&server=" + expectedTransientServerValue;
        expectedData += "&ids=" + signatureOfExpectedTransientData;
        Assert.assertEquals(expectedData, sqrlResponseFactory.getDataSentAfterException());
    }

    @Test
    public void shouldThrowTransientErrorExceptionIfItOccursTwice() throws Exception {
        // Create the required mocks
        when(mHttpURLConnection.getOutputStream()).thenReturn(mock(ByteArrayOutputStream.class));
        SQRLIdentity sqrlIdentity = getMockTransientSQRLIdentity();

        // Create the TransientErrorRetryThenSucceedFactory that allows us to mock SQRLResponse behaviour
        TransientErrorEveryTimeFactory sqrlResponseFactory = new TransientErrorEveryTimeFactory(defaultExpectedServerValue);

        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, sqrlResponseFactory, false);

        try {
            request.send();
        } catch (TransientErrorException ex) {
            return;
        }

        Assert.fail("TransientErrorException should have been thrown the second time a TransientErrorException occurred.");
    }

    @Test
    public void shouldNotSendUnlockRequestKeysAndSignatureIfAreServerUnlockAndVerifyUnlockKeysRequiredReturnsFalse() throws Exception {
        // First, we need to create the mocks
        SQRLIdentity sqrlIdentity = SQRLRequestTest.getMockSQRLIdentity();

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), false);

        // Calculate what the expected data should be
        String expectedData = "client=" + defaultExpectedClientValue;
        expectedData += "&server=" + defaultExpectedServerValue;
        expectedData += "&ids=" + defaultSignatureOfExpectedData;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = mConnection.getConnection().getOutputStream().toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    @Test
    public void shouldSendUnlockRequestKeysAndSignatureIfAreServerUnlockAndVerifyUnlockKeysRequiredReturnsTrue() throws Exception {
        // Define the expected data
        String serverUnlockKey = "Jq36nLpcKdVwfVBYWgl1Gnq_zZIX_wR3IZrX46P-RWc";
        String verifyUnlockKey = "vS8wxf9HnPWd8VbrEVriDNkuHEXTdf6FpAty9Y062Mk";
        String expectedClientValue = "dmVyPTENCmNtZD10ZXN0DQppZGs9SmpsMk9oVXlQOTNNMTQtQVEzc3RZTWFvWjJ2cTFCSGZtQWh4V2pNMUN1VQ0Kc3VrPUpxMzZuTHBjS2RWd2ZWQllXZ2wxR25xX3paSVhfd1IzSVpyWDQ2UC1SV2MNCnZ1az12Uzh3eGY5SG5QV2Q4VmJyRVZyaUROa3VIRVhUZGY2RnBBdHk5WTA2Mk1rDQo";
        String expectedServerValue = defaultExpectedServerValue;
        String identitySignature = "vw3vpeICTgbdAiBekuE3ozifBDcT47jQuq8bEU8Ettf7Tdof2bDApkexCUvtjEEmPSqQ54o3rPycUmeK2BMsBg";

        // First, we need to create the mocks
        SQRLIdentity sqrlIdentity = SQRLRequestTest.getMockSQRLIdentity(expectedClientValue, expectedServerValue, identitySignature);
        when(sqrlIdentity.getServerUnlockKey()).thenReturn(serverUnlockKey);
        when(sqrlIdentity.getVerifyUnlockKey()).thenReturn(verifyUnlockKey);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), true);

        // Calculate what the expected data should be
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + identitySignature;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = mConnection.getConnection().getOutputStream().toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    @Test
    public void shouldCorrectlySetCmdValue() throws Exception {
        // Define the expected data
        // cmd=sausages
        String expectedClientValue = "dmVyPTENCmNtZD1zYXVzYWdlcw0KaWRrPUpqbDJPaFV5UDkzTTE0LUFRM3N0WU1hb1oydnExQkhmbUFoeFdqTTFDdVUNCg";
        String expectedServerValue = defaultExpectedServerValue;
        String identitySignature = "jXIbDT2_7zycMGLDViWHfu9ABymsLNuTILnAdRYUaLo_HCFHrjDM4uLM8hVs7sC6SpMN0AOiqxtKc8h9JEnUCw";

        // First, we need to create the mocks
        SQRLIdentity sqrlIdentity = SQRLRequestTest.getMockSQRLIdentity(expectedClientValue, expectedServerValue, identitySignature);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), false, "sausages");

        // Calculate what the expected data should be
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + identitySignature;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = mConnection.getConnection().getOutputStream().toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    @Test
    public void updateConnectionAndModifyServerValueUsingLastResponse() throws Exception {
        String lastServerRawResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MQ0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        String previousResponseQry = "qry=/sqrl?nut=sqYNVbO3_OVKNtND42wd_A";

        // Define the expected data (normal client value, server value equal to last response)
        String expectedClientValue = defaultExpectedClientValue;
        String expectedServerValue = lastServerRawResponse;
        String identitySignature = "qIDJavPfsjVOHhslKwdF0K404b0xrHuTFERftMY1bzMUVNN3LYu0eQ2ddQ6TPwx4YKV8MQGwNEjnc_5ZPUdACg";

        // First, we need to create the mocks
        SQRLIdentity sqrlIdentity = SQRLRequestTest.getMockSQRLIdentity(expectedClientValue, expectedServerValue, identitySignature);
        SQRLResponse previousResponse = mock(SQRLResponse.class);
        when(previousResponse.toString()).thenReturn(lastServerRawResponse);
        when(previousResponse.getQry()).thenReturn(previousResponseQry);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLTestRequest request = new SQRLTestRequest(mConnectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), previousResponse, false);

        // Verify that updatePathAndQuery was called
        verify(mConnectionFactory).create(previousResponseQry);

        // Calculate what the expected data should be
        String expectedData = "client=" + expectedClientValue;
        expectedData += "&server=" + expectedServerValue;
        expectedData += "&ids=" + identitySignature;

        // Ask the request object to send the data, and then verify it
        request.send();
        String dataSent = mConnection.getConnection().getOutputStream().toString();
        Assert.assertEquals(expectedData, dataSent);
    }

    //endregion

    /**
     * Returns identity where second server value is the last server response.
     */
    public static SQRLIdentity getMockTransientSQRLIdentity() throws Exception {
        return getMockSQRLIdentity(defaultExpectedClientValue, expectedTransientServerValue, signatureOfExpectedTransientData);
    }

    public static SQRLIdentity getMockSQRLIdentity() throws Exception {
        return getMockSQRLIdentity(defaultExpectedClientValue, defaultExpectedServerValue, defaultSignatureOfExpectedData);
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
