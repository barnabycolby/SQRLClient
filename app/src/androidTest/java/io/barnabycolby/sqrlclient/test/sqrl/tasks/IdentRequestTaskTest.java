package io.barnabycolby.sqrlclient.test.sqrl.tasks;

import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.tasks.IdentRequestTask;

import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class IdentRequestTaskTest {
    private SQRLRequestFactory mRequestFactory;
    private SQRLIdentRequest mIdentRequest;
    private SQRLResponse mResponse;
    private TextView mTextView;

    @Before
    public void setUp() throws Exception {
        // Create the mocks
        mRequestFactory = mock(SQRLRequestFactory.class);
        mIdentRequest = mock(SQRLIdentRequest.class);
        mResponse = mock(SQRLResponse.class);
        mTextView = mock(TextView.class);

        // Define the behaviour of the mocks
        when(mRequestFactory.createIdent(mResponse)).thenReturn(mIdentRequest);
    }

    @Test
    public void shouldCreateAndSendAnIdentRequest() throws Exception {
        // Execute the ident request
        IdentRequestTask identRequestTask = new IdentRequestTask(mRequestFactory, mTextView, mResponse);
        identRequestTask.enableTestMode();
        identRequestTask.execute();
        boolean result = identRequestTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify the behaviour of the task
        verify(mRequestFactory).createIdent(mResponse);
        verify(mIdentRequest).send();
    }

    @Test
    public void shouldSetSuccessTextWhenNoException() throws Exception {
        // Execute the ident request
        IdentRequestTask identRequestTask = new IdentRequestTask(mRequestFactory, mTextView, mResponse);
        identRequestTask.enableTestMode();
        identRequestTask.execute();
        boolean result = identRequestTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify the behaviour of the task
        String expected = App.getApplicationResources().getString(R.string.authorisation_request_sent);
        verify(mTextView).setText(expected);
    }

    @Test
    public void shouldSetFailedTextWhenSendThrowsException() throws Exception {
        // Mock the request to throw an exception on send
        doThrow(new InvalidServerResponseException("Thrown from a unit test.")).when(mIdentRequest).send();

        // Execute the ident request
        IdentRequestTask identRequestTask = new IdentRequestTask(mRequestFactory, mTextView, mResponse);
        identRequestTask.enableTestMode();
        identRequestTask.execute();
        boolean result = identRequestTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify the behaviour of the task
        String expected = App.getApplicationResources().getString(R.string.authorisation_request_failed);
        verify(mTextView).setText(expected);
    }
}
