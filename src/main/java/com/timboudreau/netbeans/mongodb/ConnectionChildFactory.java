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

import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
final class ConnectionChildFactory extends ChildFactory<ConnectionInfo> {

    private ConnectionInfo[] connections() {
        try {
            Preferences prefs = MongoServicesNode.prefs();
            String[] kids = prefs.childrenNames();
            System.out.println("CHILD NAMES: " + Arrays.asList(kids));
            ConnectionInfo[] result = new ConnectionInfo[kids.length];
            for (int i = 0; i < kids.length; i++) {
                String kid = kids[i];
                Preferences node = prefs.node(kid);
                result[i] = new ConnectionInfo(kid, node);
            }
            System.out.println("RETURNING " + result.length + " connections: " + Arrays.asList(result));
            return result;
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
            return new ConnectionInfo[0];
        }
    }

    public void doRefresh() {
        super.refresh(false);
    }

    @Override
    protected boolean createKeys(List<ConnectionInfo> list) {
        list.addAll(Arrays.asList(connections()));
        return true;
    }

    @Override
    protected Node createNodeForKey(ConnectionInfo key) {
        return new OneConnectionNode(key);
    }
}
