package io.barnabycolby.sqrlclient.test.tasks;

import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFactory;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.tasks.IdentRequestListener;
import io.barnabycolby.sqrlclient.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLQueryRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class AccountExistsTaskTest {
    private TextView mockAccountExistsTextView;
    // We use strings that do not look like real strings
    // So that we can verify the code isn't using hardcoded strings
    private SQRLResponse mockSQRLResponse;
    private SQRLQueryRequest mockSQRLRequest;
    private SQRLRequestFactory mockFactory;
    private CreateAccountDialogFactory mockCreateAccountDialogFactory;
    private IdentRequestListener mockIdentRequestListener;

    @Before
    public void setUp() throws Exception {
        // Create the accountExistsTask mock text view
        this.mockAccountExistsTextView = mock(TextView.class);

        mockSQRLResponse = mock(SQRLResponse.class);
        mockSQRLRequest = mock(SQRLQueryRequest.class);
        mockFactory = mock(SQRLRequestFactory.class);
        mockCreateAccountDialogFactory = mock(CreateAccountDialogFactory.class);
        mockIdentRequestListener = mock(IdentRequestListener.class);
        when(mockSQRLRequest.send()).thenReturn(mockSQRLResponse);
        when(mockFactory.createQuery()).thenReturn(mockSQRLRequest);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenAccountDoesNotExist() throws Exception {
        String expectedText = App.getApplicationResources().getString(R.string.account_does_not_exist);
        accountExistsTestCorrectTextSet(false, expectedText);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenAccountDoesExist() throws Exception {
        String expectedText = App.getApplicationResources().getString(R.string.account_exists);
        accountExistsTestCorrectTextSet(true, expectedText);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenExceptionIsThrown() throws Exception {
        // Create the factory that throws the exception
        doThrow(new InvalidServerResponseException("Exception thrown by unit test.")).when(mockSQRLRequest).send();

        String expectedText = App.getApplicationResources().getString(R.string.something_went_wrong);
        createAndRunAccountExistsTaskAndVerifyText(expectedText);
    }

    @Test
    public void shouldDisplayCreateAccountDialogIfAccountDoesNotExist() throws Exception {
        // Create the required mocks
        when(mockSQRLResponse.currentAccountExists()).thenReturn(false);

        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockCreateAccountDialogFactory, mockIdentRequestListener);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify that the dialog was created
        verify(mockCreateAccountDialogFactory).create();
    }

    @Test
    public void shouldNotDisplayDialogAndCallbackProceedIfAccountAlreadyExists() throws Exception {
        // Create the required mocks
        when(mockSQRLResponse.currentAccountExists()).thenReturn(true);

        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockCreateAccountDialogFactory, mockIdentRequestListener);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify that the dialog was not created and proceedWithIdentRequest was called
        verify(mockCreateAccountDialogFactory, never()).create();
        verify(mockIdentRequestListener).proceedWithIdentRequest();
    }

    @Test
    public void getResponseShouldReturnTheQueryResponse() throws Exception {
        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockCreateAccountDialogFactory, mockIdentRequestListener);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        Assert.assertEquals(mockSQRLResponse, accountExistsTask.getResponse());
    }

    private void accountExistsTestCorrectTextSet(boolean accountExistsResponse, String expectedText) throws Exception {
        // Create the factory that ensures the account does not exist
        when(mockSQRLResponse.currentAccountExists()).thenReturn(accountExistsResponse);

        createAndRunAccountExistsTaskAndVerifyText(expectedText);
    }

    private void createAndRunAccountExistsTaskAndVerifyText(String expectedText) throws Exception {
        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockCreateAccountDialogFactory, mockIdentRequestListener);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Assert that the correct things happened
        verify(mockAccountExistsTextView).setText(expectedText);
    }
}
