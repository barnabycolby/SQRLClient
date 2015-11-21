package io.barnabycolby.sqrlclient;

import org.junit.Test;
import android.net.Uri;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SQRLUriTest {

    @Test
    public void throwExceptionForUnknownScheme() throws Exception {
        // Mock the Uri class
        Uri mockedUri = getMockedUri("abc", "Y0thBhbZk3N9DSxgTgfhXg");

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
        Uri mockedUri = getMockedUri("sqrl", null);

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
        Uri mockedUri = getMockedUri("qrl", "Y0thBhbZk3N9DSxgTgfhXg");

        // Better JUnit assert exception thrown is not supported until later versions
        try {
            new SQRLUri(mockedUri);
        } catch (Exception ex) {
            fail("NoNutException was not thrown.");
        }
    }

    private Uri getMockedUri(String scheme, String nut) {
        Uri mockedUri = mock(Uri.class);
        when(mockedUri.getScheme()).thenReturn(scheme);
        when(mockedUri.getQueryParameter("nut")).thenReturn(nut);
        return mockedUri;
    }
}
