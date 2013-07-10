/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
