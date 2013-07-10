package com.timboudreau.netbeans.mongodb;

import com.mongodb.MongoClient;
import java.util.List;
import java.util.concurrent.Callable;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
final class OneConnectionChildren extends ChildFactory.Detachable<DbInfo> {

    private final Lookup lookup;
    private MongoClient client;

    public OneConnectionChildren(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected void addNotify() {
        client = lookup.lookup(MongoClient.class);
    }

    @Override
    protected void removeNotify() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    protected boolean createKeys(final List<DbInfo> list) {
        ConnectionProblems problems = lookup.lookup(ConnectionProblems.class);
        if (client != null) {
            problems.invoke(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (String dbName : client.getDatabaseNames()) {
                        list.add(new DbInfo(lookup, dbName));
                    }
                    return null;
                }
            });
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DbInfo key) {
        return new OneDbNode(key);
    }
}
