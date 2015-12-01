package io.barnabycolby.sqrlclient.sqrl;

public class TifBits {
    public static final int CURRENT_ID_MATCH = 0x1;
    public static final int FUNCTION_NOT_SUPPORTED = 0x10;
    public static final int TRANSIENT_ERROR = 0x20;
    public static final int COMMAND_FAILED = 0x40;
    public static final int CLIENT_FAILURE = 0x80;
    public static final int BAD_ID_ASSOCIATION = 0x100;
    public static final int INVALID_LINK_ORIGIN = 0x200;
}
