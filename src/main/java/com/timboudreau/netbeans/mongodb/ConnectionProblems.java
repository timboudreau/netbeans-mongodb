package com.timboudreau.netbeans.mongodb;

import java.util.concurrent.Callable;

/**
 *
 * @author Tim Boudreau
 */
public abstract class ConnectionProblems {

    public abstract void handleException(Exception e, String logMessage);

    public final void handleException(Exception e) {
        handleException(e, null);
    }

    protected boolean shouldRetryOn(Exception e) {
        return e instanceof IllegalStateException;
    }

    protected abstract <T> T retry(Callable<T> callable, Exception ex) throws Exception;

    public <T> T invoke(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (shouldRetryOn(e)) {
                try {
                    return retry(callable, e);
                } catch (Exception e1) {
                    handleException(e1);
                    return null;
                }
            }
            handleException(e);
            return null;
        }
    }
}
