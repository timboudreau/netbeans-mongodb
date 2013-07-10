package com.timboudreau.netbeans.mongodb;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 */
@Messages(value = {"CollectionName=Collection Name"})
final class CollectionNameProperty extends PropertySupport.ReadOnly<String> {
    private final Lookup lkp;
    
    CollectionNameProperty(Lookup lkp) {
        super("collectionName", String.class, Bundle.CollectionName(), null);
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        CollectionInfo info = lkp.lookup(CollectionInfo.class);
        return info == null ? "[no name]" : info.name;
    }
}
