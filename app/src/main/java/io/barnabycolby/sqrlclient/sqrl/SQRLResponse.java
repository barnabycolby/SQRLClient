package io.barnabycolby.sqrlclient.sqrl;

import android.util.Base64;
import android.support.v4.util.ArrayMap;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.*;
import java.lang.Character;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Map;

public class SQRLResponse {
    private Map<String, String> nameValuePairs;
    private int tif;

    public SQRLResponse(SQRLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException {
        // Check the response code
        if (connection.getResponseCode() != 200) {
            throw new IOException();
        }

        // Extract the values from the data
        InputStream inputStream = connection.getInputStream();
        byte[] encodedServerResponse = convertInputStreamToByteArray(inputStream);
        byte[] decodedResponse = Base64.decode(encodedServerResponse, Base64.URL_SAFE);
        String serverResponse = new String(decodedResponse, Charset.forName("UTF-8"));
        this.nameValuePairs = convertServerResponseToMap(serverResponse);

        // Perform response validity checks
        checkThatAllRequiredNameValuePairsArePresent();
        checkVersionIsValidAndSupported();
        checkTifIsValidAndCommandDidNotFail(new String(encodedServerResponse, Charset.forName("UTF-8")));
    }

    /**
     * This method requires the server response as a parameter just in case it needs to throw a TransientErrorException
     */
    private void checkTifIsValidAndCommandDidNotFail(String serverResponse) throws InvalidServerResponseException, CommandFailedException, TransientErrorException {
        String tifValue = nameValuePairs.get("tif");
        
        // As we have no idea how long the tif value is, we iterate over each character
        try {
            this.tif = Integer.parseInt(tifValue, 16);
        } catch (NumberFormatException ex) {
            throw new InvalidServerResponseException("\"tif\" value in server response was not hexadecimal.");
        }

        // Check for the command not failed bit
        if ((this.tif & TifBits.COMMAND_FAILED) != 0) {
            String errorMessage = "An unknown error occurred.";

            if ((this.tif & TifBits.FUNCTION_NOT_SUPPORTED) != 0) {
                errorMessage = "Query function is not supported by server.";
            } else if ((this.tif & TifBits.TRANSIENT_ERROR) != 0) {
                errorMessage = "A transient error occurred, should retry with updated nut and qry values.";
                throw new TransientErrorException(errorMessage, nameValuePairs.get("nut"), nameValuePairs.get("qry"), serverResponse);
            } else if ((this.tif & TifBits.CLIENT_FAILURE) != 0) {
                errorMessage = "Some aspect of the request was incorrect, according to the server.";
            } else if ((this.tif & TifBits.BAD_ID_ASSOCIATION) != 0) {
                errorMessage = "Perhaps the wrong SQRL Identity was used?";
            } else if ((this.tif & TifBits.INVALID_LINK_ORIGIN) != 0) {
                errorMessage = "The url passed to the server was invalid, according to the server.";
            }

            throw new CommandFailedException(errorMessage);
        }
    }

    private void checkVersionIsValidAndSupported() throws VersionNotSupportedException {
        // Check that the version is compatible with the version supported by this client
        String versionString = nameValuePairs.get("ver");
        if (!isVersionSupported(versionString)) {
            throw new VersionNotSupportedException(versionString);
        }
    }

    private void checkThatAllRequiredNameValuePairsArePresent() throws InvalidServerResponseException {
        checkNameValuePairIsPresent("ver");
        checkNameValuePairIsPresent("nut");
        checkNameValuePairIsPresent("tif");
        checkNameValuePairIsPresent("qry");
        checkNameValuePairIsPresent("sfn");
    }

    private void checkNameValuePairIsPresent(String parameter) throws InvalidServerResponseException {
        String errorMessageSuffix = " parameter was not present in server response.";

        String parameterValue = nameValuePairs.get(parameter);
        if (parameterValue == null || parameterValue.isEmpty()) {
            throw new InvalidServerResponseException("\"" + parameter + "\"" + errorMessageSuffix);
        }
    }

    private boolean isVersionSupported(String versionString) {
        String[] serverSupportedVersions = versionString.split(",");
        for (String version : serverSupportedVersions) {
            if (version.equals("1")) {
                return true;
            }
        }

        return false;
    }

    private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

        int numberOfBytesRead;
        byte[] buffer = new byte[32];

        while ((numberOfBytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            dataStream.write(buffer, 0, numberOfBytesRead);
        }

        dataStream.flush();
        return dataStream.toByteArray();
    }

    private Map<String, String> convertServerResponseToMap(String serverResponse) throws InvalidServerResponseException {
        // Split the response into lines, each containing a name and value pair
        String[] nameValuePairsAsStrings = serverResponse.split("\r\n");

        Map<String, String> map = new ArrayMap<String, String>();

        for (String nameValuePairAsString : nameValuePairsAsStrings) {
            String[] separatedNameAndValuePair = nameValuePairAsString.split("=", 2);
            if (separatedNameAndValuePair.length != 2) {
                throw new InvalidServerResponseException("Servers response was in an unrecognised format.");
            }

            map.put(separatedNameAndValuePair[0], separatedNameAndValuePair[1]);
        }

        return map;
    }

    public boolean accountExists() {
        if ((this.tif & TifBits.CURRENT_ID_MATCH) != 0) {
            return true;
        } else {
            return false;
        }
    }
}
