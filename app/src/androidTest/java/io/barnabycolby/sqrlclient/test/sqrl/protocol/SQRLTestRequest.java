package io.barnabycolby.sqrlclient.test.sqrl.protocol;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLConnectionFactory;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLResponseFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLRequest;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Allows easy creation and sending of a SQRL request to be sent to a server.
 */
public class SQRLTestRequest extends SQRLRequest {

    private SQRLConnectionFactory sqrlConnectionFactory;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;
    private boolean serverUnlockAndVerifyUnlockKeysRequired;
    private String commandString;

    /**
     * Constructs a new SQRLRequest object.
     *
     * @param sqrlConnectionFactory  The factory used to create the SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     */
    public SQRLTestRequest(SQRLConnectionFactory sqrlConnectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, boolean serverUnlockAndVerifyUnlockKeysRequired) throws MalformedURLException, IOException {
        super(sqrlConnectionFactory, sqrlIdentity, sqrlResponseFactory);
        this.serverUnlockAndVerifyUnlockKeysRequired = serverUnlockAndVerifyUnlockKeysRequired;
    }

    /**
     * Constructs a new SQRLRequest object that returns a given command string.
     *
     * @param sqrlConnectionFactory  The factory used to create the SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     * @param commandString  The command string that should be returned when getCommandString is called.
     */
    public SQRLTestRequest(SQRLConnectionFactory sqrlConnectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, boolean serverUnlockAndVerifyUnlockKeysRequired, String commandString) throws MalformedURLException, IOException {
        super(sqrlConnectionFactory, sqrlIdentity, sqrlResponseFactory);
        this.serverUnlockAndVerifyUnlockKeysRequired = serverUnlockAndVerifyUnlockKeysRequired;
        this.commandString = commandString;
    }

    /**
     * Constructs a new SQRLRequest object that returns a given command string.
     *
     * @param sqrlConnectionFactory  The factory used to create the SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     * @param previousResponse  The previous response sent by the server.
     */
    public SQRLTestRequest(SQRLConnectionFactory sqrlConnectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse, boolean serverUnlockAndVerifyUnlockKeysRequired) throws MalformedURLException, NoNutException, IOException {
        super(sqrlConnectionFactory, sqrlIdentity, sqrlResponseFactory, previousResponse);
        this.serverUnlockAndVerifyUnlockKeysRequired = serverUnlockAndVerifyUnlockKeysRequired;
        this.commandString = commandString;
    }

    @Override
    protected boolean areServerUnlockAndVerifyUnlockKeysRequired() {
        return this.serverUnlockAndVerifyUnlockKeysRequired;
    }

    @Override
    protected String getCommandString() {
        if (this.commandString == null) {
            this.commandString = "test";
        }

        return this.commandString;
    }
}
