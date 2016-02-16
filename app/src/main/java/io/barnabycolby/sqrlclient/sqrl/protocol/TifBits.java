package io.barnabycolby.sqrlclient.sqrl.protocol;

/**
 * Maps friendly names of the flags encoded by the tif value to their actual hexadecimal value.
 */
public class TifBits {
    public static final int CURRENT_ID_MATCH = 0x1;
    public static final int FUNCTION_NOT_SUPPORTED = 0x10;
    public static final int TRANSIENT_ERROR = 0x20;
    public static final int COMMAND_FAILED = 0x40;
    public static final int CLIENT_FAILURE = 0x80;
    public static final int BAD_ID_ASSOCIATION = 0x100;
}
