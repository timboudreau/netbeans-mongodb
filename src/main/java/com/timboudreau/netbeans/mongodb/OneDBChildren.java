package com.timboudreau.netbeans.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
final class OneDBChildren extends ChildFactory<CollectionInfo> {

    private final Lookup lookup;

    public OneDBChildren(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(final List<CollectionInfo> list) {
        ConnectionProblems problems = lookup.lookup(ConnectionProblems.class);
        problems.invoke(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                MongoClient client = lookup.lookup(MongoClient.class);
                DbInfo info = lookup.lookup(DbInfo.class);
                DB db = client.getDB(info.dbName);
                List<String> names = new LinkedList<>(db.getCollectionNames());
                for (String name : names) {
                    list.add(new CollectionInfo(name, lookup));
                }
                return null;
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(CollectionInfo key) {
        return new CollectionNode(key);
    }
}
