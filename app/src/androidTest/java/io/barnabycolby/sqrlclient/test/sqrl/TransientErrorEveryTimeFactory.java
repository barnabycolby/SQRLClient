package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.*;

public class TransientErrorEveryTimeFactory implements SQRLResponseFactory {
    private String lastServerResponse;

    public TransientErrorEveryTimeFactory(String lastServerResponse) {
        this.lastServerResponse = lastServerResponse;
    }

    public SQRLResponse create(SQRLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException {
        throw new TransientErrorException("Retry and reissue.", getNut(), getQry(), getLastServerResponse());
    }

    public String getNut() {
        return "sqYNVbO3_OVKNtND42wd_A";
    }

    public String getQry() {
        return "/sqrl?nut=" + getNut();
    }

    public String getLastServerResponse() {
        return this.lastServerResponse;
    }
}
