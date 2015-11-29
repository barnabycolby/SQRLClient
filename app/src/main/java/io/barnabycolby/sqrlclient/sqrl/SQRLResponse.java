package io.barnabycolby.sqrlclient.sqrl;

import java.net.HttpURLConnection;
import java.io.IOException;

public class SQRLResponse {
    public SQRLResponse(HttpURLConnection connection) throws IOException {
        // Check the response code
        if (connection.getResponseCode() != 200) {
            throw new IOException();
        }
    }
}
