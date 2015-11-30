package io.barnabycolby.sqrlclient.exceptions;

public class UnknownSchemeException extends Exception {
    private String scheme;

    public UnknownSchemeException(String scheme) {
        super("The uri scheme " + scheme + "is not supported.");
        this.scheme = scheme;
    }

    public String getScheme() {
        return this.scheme;
    }
}
