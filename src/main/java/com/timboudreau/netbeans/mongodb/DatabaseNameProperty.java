package com.timboudreau.netbeans.mongodb;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
final class DatabaseNameProperty extends PropertySupport.ReadOnly<String> {
    private final Lookup lkp;

    public DatabaseNameProperty(Lookup lkp) {
        super("databaseName", String.class, Bundle.DB_NAME(), Bundle.DB_NAME_DESC());
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        DbInfo info = lkp.lookup(DbInfo.class);
        return info == null ? "[no name]" : info.dbName;
    }
}
