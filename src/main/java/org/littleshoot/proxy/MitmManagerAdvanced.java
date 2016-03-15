package org.littleshoot.proxy;

/**
 * MITMManagers encapsulate the logic required for letting LittleProxy act as a
 * man in the middle for HTTPS requests.
 */
public interface MitmManagerAdvanced extends MitmManager {

    /**
     * <p>
     * Implements an interface that allows the user to specify which connections
     * has to intercept
     * </p>
     *
     * @param URI
     *            the webserver URI at which SSL is trying to instantiate the connection
     * @return
     *            returns True if the connection must be intercepted, or false in the
     *            opposite case
     */
    boolean hasToIntercept(String URI);

}
