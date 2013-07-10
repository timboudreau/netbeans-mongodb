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
