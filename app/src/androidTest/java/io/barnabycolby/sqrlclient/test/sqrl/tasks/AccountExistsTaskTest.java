package io.barnabycolby.sqrlclient.test.sqrl.tasks;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFactory;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLQueryRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class AccountExistsTaskTest {
    private Resources mockResources;
    private TextView mockAccountExistsTextView;
    // We use strings that do not look like real strings
    // So that we can verify the code isn't using hardcoded strings
    private String contacting_server = "TestString1";
    private String account_exists = "TestString2";
    private String account_does_not_exist = "TestString3";
    private String something_went_wrong = "TestString4";

    @Before
    public void setUp() throws Exception {
        // Create the resources mock
        this.mockResources = mock(Resources.class);
        when(this.mockResources.getString(R.string.contacting_server)).thenReturn(this.contacting_server);
        when(this.mockResources.getString(R.string.account_exists)).thenReturn(this.account_exists);
        when(this.mockResources.getString(R.string.account_does_not_exist)).thenReturn(this.account_does_not_exist);
        when(this.mockResources.getString(R.string.something_went_wrong)).thenReturn(this.something_went_wrong);

        // Create the accountExistsTask mock text view
        this.mockAccountExistsTextView = mock(TextView.class);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenAccountDoesNotExist() throws Exception {
        accountExistsTestCorrectTextSet(false, this.account_does_not_exist);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenAccountDoesExist() throws Exception {
        accountExistsTestCorrectTextSet(true, this.account_exists);
    }

    @Test
    public void shouldDisplayCorrectMessageWhenExceptionIsThrown() throws Exception {
        // Create the factory that throws the exception
        SQRLQueryRequest mockSQRLRequest = mock(SQRLQueryRequest.class);
        doThrow(new InvalidServerResponseException("Exception thrown by unit test.")).when(mockSQRLRequest).send();
        SQRLRequestFactory mockFactory = mock(SQRLRequestFactory.class);
        when(mockFactory.createQuery()).thenReturn(mockSQRLRequest);

        createAndRunAccountExistsTaskAndVerifyText(mockFactory, this.something_went_wrong);
    }

    @Test
    public void shouldDisplayCreateAccountDialogIfAccountDoesNotExist() throws Exception {
        // Create the required mocks
        SQRLResponse mockSQRLResponse = mock(SQRLResponse.class);
        when(mockSQRLResponse.currentAccountExists()).thenReturn(false);
        SQRLQueryRequest mockSQRLRequest = mock(SQRLQueryRequest.class);
        when(mockSQRLRequest.send()).thenReturn(mockSQRLResponse);
        SQRLRequestFactory mockFactory = mock(SQRLRequestFactory.class);
        when(mockFactory.createQuery()).thenReturn(mockSQRLRequest);
        CreateAccountDialogFactory mockCreateAccountDialogFactory = mock(CreateAccountDialogFactory.class);

        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockResources, mockCreateAccountDialogFactory);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify that the dialog was created
        verify(mockCreateAccountDialogFactory).create();
    }

    @Test
    public void shouldNotDisplayCreateAccountDialogIfAccountAlreadyExists() throws Exception {
        // Create the required mocks
        SQRLResponse mockSQRLResponse = mock(SQRLResponse.class);
        when(mockSQRLResponse.currentAccountExists()).thenReturn(true);
        SQRLQueryRequest mockSQRLRequest = mock(SQRLQueryRequest.class);
        when(mockSQRLRequest.send()).thenReturn(mockSQRLResponse);
        SQRLRequestFactory mockFactory = mock(SQRLRequestFactory.class);
        when(mockFactory.createQuery()).thenReturn(mockSQRLRequest);
        CreateAccountDialogFactory mockCreateAccountDialogFactory = mock(CreateAccountDialogFactory.class);

        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockResources, mockCreateAccountDialogFactory);
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Verify that the dialog was created
        verify(mockCreateAccountDialogFactory, never()).create();
    }

    private void accountExistsTestCorrectTextSet(boolean accountExistsResponse, String expectedText) throws Exception {
        // Create the factory that ensures the account does not exist
        SQRLResponse mockSQRLResponse = mock(SQRLResponse.class);
        when(mockSQRLResponse.currentAccountExists()).thenReturn(accountExistsResponse);
        SQRLQueryRequest mockSQRLRequest = mock(SQRLQueryRequest.class);
        when(mockSQRLRequest.send()).thenReturn(mockSQRLResponse);
        SQRLRequestFactory mockFactory = mock(SQRLRequestFactory.class);
        when(mockFactory.createQuery()).thenReturn(mockSQRLRequest);

        createAndRunAccountExistsTaskAndVerifyText(mockFactory, expectedText);
    }

    private void createAndRunAccountExistsTaskAndVerifyText(SQRLRequestFactory mockFactory, String expectedText) throws Exception {
        // Create the accountExistsTask and tell it it execute
        AccountExistsTask accountExistsTask = new AccountExistsTask(mockFactory, mockAccountExistsTextView, mockResources, mock(CreateAccountDialogFactory.class));
        accountExistsTask.enableTestMode();
        accountExistsTask.execute();
        boolean result = accountExistsTask.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(result);

        // Assert that the correct things happened
        verify(mockAccountExistsTextView).setText(expectedText);
    }
}
