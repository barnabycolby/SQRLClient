package io.barnabycolby.sqrlclient;

import org.junit.Test;
import android.net.Uri;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SQRLUriTest {

    @Test
    public void throwExceptionForUnknownScheme() throws Exception {
        // Mock the Uri class
        Uri mockedUri = mock(Uri.class);
        when(mockedUri.getScheme()).thenReturn("abc");
        when(mockedUri.getQueryParameter("nut")).thenReturn("Y0thBhbZk3N9DSxgTgfhXg");

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(mockedUri);
        } catch (UnknownSchemeException ex) {
            return;
        }

        fail("UnknownSchemeException was not thrown.");
    }

    @Test
    public void throwExceptionForMissingNut() throws Exception {
        // Mock the Uri class
        Uri mockedUri = mock(Uri.class);
        when(mockedUri.getScheme()).thenReturn("sqrl");
        when(mockedUri.getQueryParameter("nut")).thenReturn(null);

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(mockedUri);
        } catch (NoNutException ex) {
            return;
        }

        fail("NoNutException was not thrown.");
    }

    @Test
    public void noExceptionThrownForValidSQRLUri() throws Exception {
        // Mock the Uri class
        Uri mockedUri = mock(Uri.class);
        when(mockedUri.getScheme()).thenReturn("qrl");
        when(mockedUri.getQueryParameter("nut")).thenReturn("Y0thBhbZk3N9DSxgTgfhXg");

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(mockedUri);
        } catch (Exception ex) {
            fail("NoNutException was not thrown.");
        }
    }
}
