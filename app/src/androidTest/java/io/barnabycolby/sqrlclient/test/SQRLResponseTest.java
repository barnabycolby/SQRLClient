package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import android.net.Uri;
import java.net.HttpURLConnection;
import java.io.*;
import io.barnabycolby.sqrlclient.exceptions.VersionNotSupportedException;

import io.barnabycolby.sqrlclient.sqrl.*;

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
        // ver=
        String serverResponse = "dmVyPQpudXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQp0aWY9MjQKcXJ5PS9zcXJsP251dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BCnNmbj1HUkMK";
        assertExceptionThrownForGivenServerResponse(VersionNotSupportedException.class, serverResponse);

        // ver=10
        serverResponse = "dmVyPTEwCm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BCnRpZj0yNApxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0EKc2ZuPUdSQwo";
        assertExceptionThrownForGivenServerResponse(VersionNotSupportedException.class, serverResponse);

        // ver=2,4,6
        serverResponse = "dmVyPTIsNCw2Cm51dD1zcVlOVmJPM19PVktOdE5ENDJ3ZF9BCnRpZj0yNApxcnk9L3Nxcmw_bnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0EKc2ZuPUdSQwo";
        assertExceptionThrownForGivenServerResponse(VersionNotSupportedException.class, serverResponse);
        
        // ver=2,3,1,5
        serverResponse = "dmVyPTIsMywxLDUKbnV0PXNxWU5WYk8zX09WS050TkQ0MndkX0EKdGlmPTI0CnFyeT0vc3FybD9udXQ9c3FZTlZiTzNfT1ZLTnRORDQyd2RfQQpzZm49R1JDCg";
        assertSuccessForGivenServerResponse(serverResponse);
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
