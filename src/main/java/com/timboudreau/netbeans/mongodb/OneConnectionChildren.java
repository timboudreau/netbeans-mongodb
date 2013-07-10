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
