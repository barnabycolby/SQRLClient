package io.barnabycolby.sqrlclient.exceptions;

public class TransientErrorException extends Exception {
    private String nut;
    private String qry;

    public TransientErrorException(String message, String nut, String qry) {
        super(message);

        this.nut = nut;
        this.qry = qry;
    }

    public String getNut() {
        return this.nut;
    }

    public String getQry() {
        return this.qry;
    }
}
