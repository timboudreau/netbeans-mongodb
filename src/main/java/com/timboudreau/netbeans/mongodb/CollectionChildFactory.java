package com.timboudreau.netbeans.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.List;
import java.util.concurrent.Callable;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
final class CollectionChildFactory extends ChildFactory.Detachable<DBObject> {

    private DBCursor cursor;
    private final Lookup lookup;

    public CollectionChildFactory(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected void removeNotify() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    protected boolean createKeys(final List<DBObject> list) {
        ConnectionProblems pblms = lookup.lookup(ConnectionProblems.class);
        Boolean done = pblms.invoke(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (cursor == null) {
                    DBCollection coll = lookup.lookup(DBCollection.class);
                    cursor = coll.find();
                    int ct = cursor.count();
                    if (ct > 0) {
                        cursor.batchSize(Math.min(CollectionNode.maxCursorSize, ct));
                    } else {
                        cursor = null;
                    }
                }
                boolean done = cursor == null;
                if (!done) {
                    for (int i = 0; i < CollectionNode.maxCursorSize && cursor != null && cursor.hasNext(); i++) {
                        list.add(cursor.next());
                    }
                    done = !cursor.hasNext();
                    if (done) {
                        cursor.close();
                        cursor = null;
                    }
                }
                return done;
            }
        });
        return done == null ? true : done;
    }

    @Override
    protected Node createNodeForKey(DBObject key) {
        String idOrName = key.get("_id") + ""; //NOI18N
        Object o = key.get("name"); //NOI18N
        if (o != null) {
            String s = o.toString();
            if (!s.isEmpty()) {
                idOrName = s;
            }
        }
        return new GenericNode(lookup, idOrName, key.toMap());
    }
}
