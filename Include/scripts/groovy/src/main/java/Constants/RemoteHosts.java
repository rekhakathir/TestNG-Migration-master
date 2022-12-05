package src.main.java.Constants;

/**
 * Enum class for available remote hosts.
 */
public enum RemoteHosts {

    /**
     * Selenium grid.
     */
    GRID,

    /**
     * Sauce labs.
     */
    SAUCE;

    /**
     * Gets the browser.
     *
     * @param host the remote host value to get
     * @return the remote host enum value
     * @throws IllegalArgumentException Throws exception if remote host is not available
     */
    public static RemoteHosts remoteHostForName(String host) {
        for (RemoteHosts b : values()) {
            if (b.toString().equalsIgnoreCase(host)) {
                return b;
            }
        }
        throw remoteHostNotFound(host);
    }

    /**
     * Throws new exception if remote host is not found.
     *
     * @param outcome the outcome.
     * @return Return new IllegalArgumentException
     */
    private static IllegalArgumentException remoteHostNotFound(String outcome) {
        return new IllegalArgumentException(("Invalid remote host [" + outcome + "]"));
    }
}
