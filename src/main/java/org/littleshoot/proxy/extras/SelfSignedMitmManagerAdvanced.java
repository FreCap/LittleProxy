package org.littleshoot.proxy.extras;

import org.littleshoot.proxy.MitmManagerAdvanced;

/**
 * An extension of {@link SelfSignedMitmManager} that explicitly says that the server has to intercept all the
 * connections
 */
public class SelfSignedMitmManagerAdvanced extends SelfSignedMitmManager implements MitmManagerAdvanced {

    @Override
    public boolean hasToIntercept(String URI) {
        return true;
    }
}
