package io.barnabycolby.sqrlclient.sqrl;

import java.net.HttpURLConnection;
import java.io.*;
import io.barnabycolby.sqrlclient.exceptions.*;
import java.util.Map;
import android.support.v4.util.ArrayMap;
import java.nio.charset.Charset;
import android.util.Base64;
import java.lang.Character;

public class SQRLResponse {
    private Map<String, String> nameValuePairs;

    public SQRLResponse(HttpURLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException {
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
        checkVersionIsValid();
        checkTifIsValid();
    }

    private void checkTifIsValid() throws InvalidServerResponseException {
        String tifValue = nameValuePairs.get("tif");
        
        // As we have no idea how long the tif value is, we iterate over each character
        for (int i = 0; i < tifValue.length(); i++) {
            char c = tifValue.charAt(i);
            if (Character.digit(c, 16) == -1) {
                throw new InvalidServerResponseException("\"tif\" value in server response was not hexadecimal.");
            }
        }
    }

    private void checkVersionIsValid() throws VersionNotSupportedException {
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
}
