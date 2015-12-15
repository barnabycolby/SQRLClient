package io.barnabycolby.sqrlclient.exceptions;

/**
 * Signifies that the SQRL server responded with a transient error, indicated by the tif flags.
 *
 * When a transient error occurs, the nut and qry values contained by the servers response should be used to try and recover from the error.
 * The request should be resent using the new nut and qry values, which can be retrieved from this exception.
 */
public class TransientErrorException extends SQRLException {
    private String nut;
    private String qry;
    private String lastServerResponse;

    /**
     * Constructs a new instance with the necessary values allowing the client to recover if possible.
     *
     * @param nut  The nut value sent in the servers response.
     * @param qry  The qry value sent in the servers response.
     * @param lastServerResponse The last response sent by the server in it's entirity. It should be a base64url encoded list of name value pairs.
     */
    public TransientErrorException(String nut, String qry, String lastServerResponse) {
        super("A transient error occurred, sender should resend request using new nut and qry values.");

        this.nut = nut;
        this.qry = qry;
        this.lastServerResponse = lastServerResponse;
    }

    /**
     * Gets the nut value returned in the server response.
     * The nut value should be used in the resent request to the server.
     */
    public String getNut() {
        return this.nut;
    }

    /**
     * Gets the qry value returned in the server response.
     * The qry value should be used in the resent request to the server.
     */
    public String getQry() {
        return this.qry;
    }

    /**
     * Gets the last server response which should be a base64url encoded name value list.
     * The response value should be used in the resent request to the server.
     */
    public String getLastServerResponse() {
        return this.lastServerResponse;
    }
}
