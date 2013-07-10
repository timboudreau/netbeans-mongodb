package com.timboudreau.netbeans.mongodb;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 */
@Messages("ConnectionHost=Host")
public class ConnectionHostProperty extends PropertySupport.ReadWrite<String> {

    private final Lookup lkp;

    ConnectionHostProperty(Lookup lkp) {
        super("connectionHost", String.class, Bundle.ConnectionHost(), null);
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        return info == null ? "[no value]" : info.getHost();
    }

    @Override
    public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        if (info != null) {
            info.setHost(t);
        }
    }
}
