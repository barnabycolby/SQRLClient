package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import io.barnabycolby.sqrlclient.exceptions.VersionNotSupportedException;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SQRLResponseTest {
    @Test
    public void constructorShouldThrowExceptionWhenConnectionReturnsNon200() throws Exception {
        assertExceptionThrownWhenConnectionReturnsGivenCode(201);
        assertExceptionThrownWhenConnectionReturnsGivenCode(204);
        assertExceptionThrownWhenConnectionReturnsGivenCode(226);
        assertExceptionThrownWhenConnectionReturnsGivenCode(300);
        assertExceptionThrownWhenConnectionReturnsGivenCode(301);
        assertExceptionThrownWhenConnectionReturnsGivenCode(302);
        assertExceptionThrownWhenConnectionReturnsGivenCode(308);
        assertExceptionThrownWhenConnectionReturnsGivenCode(400);
        assertExceptionThrownWhenConnectionReturnsGivenCode(401);
        assertExceptionThrownWhenConnectionReturnsGivenCode(402);
        assertExceptionThrownWhenConnectionReturnsGivenCode(403);
        assertExceptionThrownWhenConnectionReturnsGivenCode(404);
        assertExceptionThrownWhenConnectionReturnsGivenCode(407);
        assertExceptionThrownWhenConnectionReturnsGivenCode(500);
        assertExceptionThrownWhenConnectionReturnsGivenCode(504);
    }

    @Test
    public void constructRequestWithoutExceptionWhenResponseCodeIs200() throws Exception {
        // Create the necessary mocks
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        when(connectionMock.getOutputStream()).thenReturn(mockOutputStream);
        when(connectionMock.getResponseCode()).thenReturn(200);
        String serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        when(connectionMock.getInputStream()).thenReturn(new ByteArrayInputStream(serverResponse.getBytes()));

        SQRLResponse response = new SQRLResponse(connectionMock);
    }

    @Test
    public void shouldSuccessfullyParseVersionString() throws Exception {
        // ver=17
        String serverResponse = "dmVyPTE3DQpudXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0KdGlmPTI0DQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(VersionNotSupportedException.class, serverResponse);

        // ver=2,4,6
        serverResponse = "dmVyPTIsNCw2DQpudXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0KdGlmPTI0DQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(VersionNotSupportedException.class, serverResponse);
        
        // ver=2,3,1,5
        serverResponse = "dmVyPTIsMywxLDUNCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        assertSuccessForGivenServerResponse(serverResponse);
    }

    @Test
    public void shouldThrowInvalidServerResponseIfNameValuePairsNotSeparatedByEqualsSign() throws Exception {
        // tif24
        String serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWYyNA0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // sfn:GRC
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuR1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);
    }

    @Test
    public void shouldThrowInvalidServerResponseIfServerResponseDoesNotContainRequiredPairs() throws Exception {
        // Missing ver
        String serverResponse = "bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnRpZj0yNA0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // version=1 instead of ver=1
        serverResponse = "dmVyc2lvbj0xDQpudXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0KdGlmPTI0DQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // Missing nut
        serverResponse = "dmVyPTENCnRpZj0yNA0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // Missing tif
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // Missing qry
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // Missing sfn
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0K";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);
    }

    @Test
    public void shouldThrowInvalidServerResponseIfTifIsNotAHexidecimalValue() throws Exception {
        // tif=beans
        String serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9YmVhbnMNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPUdSQw0K";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // tif=1fg
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MWZnDQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);
    }

    @Test
    public void shouldThrowInvalidServerResponseIfRequiredNameValuePairsAreMissingValues() throws Exception {
        // ver=
        String serverResponse = "dmVyPQ0KbnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnRpZj0yNA0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // nut=
        serverResponse = "dmVyPTENCm51dD0NCnRpZj0yNA0KcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQpzZm49R1JDDQo";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // tif=
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9DQpxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0ENCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // qry=
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0NCnNmbj1HUkMNCg";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);

        // sfn=
        serverResponse = "dmVyPTENCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BDQp0aWY9MjQNCnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQ0Kc2ZuPQ0K";
        assertExceptionThrownForGivenServerResponse(InvalidServerResponseException.class, serverResponse);
    }


    private <E extends Exception> void assertExceptionThrownForGivenServerResponse(Class<E> exceptionType, String serverResponse) throws Exception {
        // Create the necessary mocks
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        when(connectionMock.getResponseCode()).thenReturn(200);
        InputStream inputStream = new ByteArrayInputStream(serverResponse.getBytes());
        when(connectionMock.getInputStream()).thenReturn(inputStream);

        try {
            new SQRLResponse(connectionMock);
        } catch (Exception ex) {
            if (exceptionType.isInstance(ex)) {
                return;
            } else {
                throw ex;
            }
        }
        
        // An exception was not thrown
        Assert.fail("SQRLResponse constructor did not throw a " + exceptionType.getSimpleName());
    }

    private void assertSuccessForGivenServerResponse(String serverResponse) throws Exception {
        // Create the necessary mocks
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        when(connectionMock.getResponseCode()).thenReturn(200);
        InputStream inputStream = new ByteArrayInputStream(serverResponse.getBytes());
        when(connectionMock.getInputStream()).thenReturn(inputStream);

        new SQRLResponse(connectionMock);
    }

    private void assertExceptionThrownWhenConnectionReturnsGivenCode(int responseCode) throws Exception {
        // Create the necessary mocks
        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        when(connectionMock.getOutputStream()).thenReturn(mockOutputStream);
        when(connectionMock.getResponseCode()).thenReturn(responseCode);

        // Verify that send throws an exception
        try {
            new SQRLResponse(connectionMock);
        } catch (IOException ex) {
            return;
        }
        Assert.fail("SQRLResponse constructor did not throw an exception for response code " + responseCode);
    }
}
