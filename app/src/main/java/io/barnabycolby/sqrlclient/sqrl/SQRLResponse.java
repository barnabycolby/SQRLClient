package io.barnabycolby.sqrlclient.sqrl;

import android.util.Base64;
import android.support.v4.util.ArrayMap;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.*;
import java.lang.Character;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Parses a SQRL servers response to allow easy access to the information it contains.
 */
public class SQRLResponse {
    private Map<String, String> nameValuePairs;
    private int tif;

    /**
     * Constructs a SQRLResponse object using the given connection.
     *
     * @param connection The connection that the request was sent over.
     *
     * @throws IOException  If an IO error occurs when reading the response.
     * @throws SQRLException  If the servers response resulted in an unrecoverable error.
     * @throws TransientErrorException  If the servers response indicates that a transient error occurs.
     */
    public SQRLResponse(SQRLConnection connection) throws IOException, SQRLException, TransientErrorException {
        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException(App.getApplicationResources().getString(R.string.non_200_response_code, responseCode));
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
     * Checks the tif value in the servers response for any possible errors.
     *
     * @param serverResponse  The server response is required just in case a TransientErrorException needs to be thrown.
     *
     * @throws InvalidServerResponseException  If the tif value is not valid.
     * @throws CommandFailedException  If the tif value indicates a command failed error.
     * @throws TransientErrorException  If the tif value indicates a transient error.
     */
    private void checkTifIsValidAndCommandDidNotFail(String serverResponse) throws InvalidServerResponseException, CommandFailedException, TransientErrorException {
        String tifValue = nameValuePairs.get("tif");
        
        // As we have no idea how long the tif value is, we iterate over each character
        try {
            this.tif = Integer.parseInt(tifValue, 16);
        } catch (NumberFormatException ex) {
            throw new InvalidServerResponseException(App.getApplicationResources().getString(R.string.tif_value_not_hexadecimal));
        }

        // Check for the command not failed bit
        if ((this.tif & TifBits.COMMAND_FAILED) != 0) {
            String errorMessage = App.getApplicationResources().getString(R.string.unknown_error);

            if ((this.tif & TifBits.FUNCTION_NOT_SUPPORTED) != 0) {
                errorMessage = App.getApplicationResources().getString(R.string.query_function_not_supported);
            } else if ((this.tif & TifBits.TRANSIENT_ERROR) != 0) {
                throw new TransientErrorException(nameValuePairs.get("nut"), nameValuePairs.get("qry"), serverResponse);
            } else if ((this.tif & TifBits.CLIENT_FAILURE) != 0) {
                errorMessage = App.getApplicationResources().getString(R.string.client_failure);
            } else if ((this.tif & TifBits.BAD_ID_ASSOCIATION) != 0) {
                errorMessage = App.getApplicationResources().getString(R.string.bad_id_association);
            }

            throw new CommandFailedException(errorMessage);
        }
    }

    /**
     * Checks the version value in the servers response is valid and supported.
     *
     * @throws VersionNotSupportedException  If the versions supported by the server are not supported by this client.
     */
    private void checkVersionIsValidAndSupported() throws VersionNotSupportedException {
        // Check that the version is compatible with the version supported by this client
        String versionString = nameValuePairs.get("ver");
        if (!isVersionSupported(versionString)) {
            throw new VersionNotSupportedException(versionString);
        }
    }

    /**
     * Checks that all required name value pairs are present in the servers response.
     *
     * @throws InvalidServerResponseException  If some of the required name value pairs are not present in the servers response.
     */
    private void checkThatAllRequiredNameValuePairsArePresent() throws InvalidServerResponseException {
        checkNameValuePairIsPresent("ver");
        checkNameValuePairIsPresent("nut");
        checkNameValuePairIsPresent("tif");
        checkNameValuePairIsPresent("qry");
    }

    /**
     * Checks whether a name value pair is present in the servers response.
     *
     * @param parameter  The name of the parameter in the servers response.
     * @throws InvalidServerResponseException  If the name value pair was not present in the response.
     */
    private void checkNameValuePairIsPresent(String parameter) throws InvalidServerResponseException {
        String parameterValue = nameValuePairs.get(parameter);
        if (parameterValue == null || parameterValue.isEmpty()) {
            String errorMessage = App.getApplicationResources().getString(R.string.server_response_missing_parameter, parameter);
            throw new InvalidServerResponseException(errorMessage);
        }
    }

    /**
     * Checks whether the version string contains a version supported by this client.
     *
     * @param versionString  The value of the version parameter in the servers response.
     * 
     * @return True if the version of this client is supported, false otherwise.
     */
    private boolean isVersionSupported(String versionString) {
        String[] serverSupportedVersions = versionString.split(",");
        for (String version : serverSupportedVersions) {
            if (version.equals("1")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts an input stream to a byte array.
     *
     * @param inputStream  The input stream to convert to a byte array.
     * @return The resulting byte array.
     *
     * @throws IOException  If an IO error occurs.
     */
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

    /**
     * Converts the server response string to a map of the name value pairs.
     *
     * @param serverResponse  The server response as a string.
     * @return The resulting map of name value pairs.
     *
     * @throws InvalidServerResponseException  If the servers response was invalid.
     */
    private Map<String, String> convertServerResponseToMap(String serverResponse) throws InvalidServerResponseException {
        // Split the response into lines, each containing a name and value pair
        String[] nameValuePairsAsStrings = serverResponse.split("\r\n");

        Map<String, String> map = new ArrayMap<String, String>();

        for (String nameValuePairAsString : nameValuePairsAsStrings) {
            String[] separatedNameAndValuePair = nameValuePairAsString.split("=", 2);
            if (separatedNameAndValuePair.length != 2) {
                throw new InvalidServerResponseException();
            }

            map.put(separatedNameAndValuePair[0], separatedNameAndValuePair[1]);
        }

        return map;
    }

    /**
     * Checks whether tthe user account exists on the SQRL server already.
     *
     * @return True if the account exists, false otherwise.
     */
    public boolean accountExists() {
        if ((this.tif & TifBits.CURRENT_ID_MATCH) != 0) {
            return true;
        } else {
            return false;
        }
    }
}
