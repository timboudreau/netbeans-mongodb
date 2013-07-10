package com.timboudreau.netbeans.mongodb;

/**
 *
 * @author Tim Boudreau
 */
public abstract class MongoDisconnect implements AutoCloseable {

    @Override
    public abstract void close();
}
