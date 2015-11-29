package io.barnabycolby.sqrlclient.test;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import android.net.Uri;
import java.net.HttpURLConnection;
import java.io.*;

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

        SQRLResponse response = new SQRLResponse(connectionMock);
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
