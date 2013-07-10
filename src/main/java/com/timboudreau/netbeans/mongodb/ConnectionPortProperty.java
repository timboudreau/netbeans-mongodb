package com.timboudreau.netbeans.mongodb;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 */
@Messages("ConnectionPort=Port")
public class ConnectionPortProperty extends PropertySupport.ReadWrite<Integer> {

    private final Lookup lkp;

    ConnectionPortProperty(Lookup lkp) {
        super("connectionPort", Integer.class, Bundle.ConnectionPort(), null);
        this.lkp = lkp;
    }

    @Override
    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        return info == null ? -1 : info.getPort();
    }

    @Override
    public void setValue(Integer t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        if (info != null) {
            info.setPort(t);
        }
    }
}
