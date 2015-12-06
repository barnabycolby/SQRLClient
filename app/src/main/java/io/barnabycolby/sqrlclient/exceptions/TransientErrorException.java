package io.barnabycolby.sqrlclient.exceptions;

public class TransientErrorException extends Exception {
    private String nut;
    private String qry;
    private String lastServerResponse;

    public TransientErrorException(String message, String nut, String qry, String lastServerResponse) {
        super(message);

        this.nut = nut;
        this.qry = qry;
        this.lastServerResponse = lastServerResponse;
    }

    public String getNut() {
        return this.nut;
    }

    public String getQry() {
        return this.qry;
    }

    public String getLastServerResponse() {
        return this.lastServerResponse;
    }
}
