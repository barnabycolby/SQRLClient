package io.barnabycolby.sqrlclient.test.sqrl;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponseFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Allows easy creation and sending of a SQRL request to be sent to a server.
 */
public class SQRLTestRequest extends SQRLRequest {

    private SQRLConnection sqrlConnection;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;
    private boolean serverUnlockAndVerifyUnlockKeysRequired;
    private String commandString;

    /**
     * Constructs a new SQRLRequest object.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     */
    public SQRLTestRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, boolean serverUnlockAndVerifyUnlockKeysRequired) {
        super(sqrlConnection, sqrlIdentity, sqrlResponseFactory);
        this.serverUnlockAndVerifyUnlockKeysRequired = serverUnlockAndVerifyUnlockKeysRequired;
    }

    /**
     * Constructs a new SQRLRequest object that returns a given command string.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     * @param commandString  The command string that should be returned when getCommandString is called.
     */
    public SQRLTestRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, boolean serverUnlockAndVerifyUnlockKeysRequired, String commandString) {
        super(sqrlConnection, sqrlIdentity, sqrlResponseFactory);
        this.serverUnlockAndVerifyUnlockKeysRequired = serverUnlockAndVerifyUnlockKeysRequired;
        this.commandString = commandString;
    }

    /**
     * Constructs a new SQRLRequest object that returns a given command string.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param serverUnlockAndVerifyUnlockKeysRequired  The value that should be returned when areServerUnlockAndVerifyUnlockKeysRequired is called.
     * @param previousResponse  The previous response sent by the server.
     */
    public SQRLTestRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse, boolean serverUnlockAndVerifyUnlockKeysRequired) throws MalformedURLException, NoNutException, IOException {
        super(sqrlConnection, sqrlIdentity, sqrlResponseFactory, previousResponse);
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
