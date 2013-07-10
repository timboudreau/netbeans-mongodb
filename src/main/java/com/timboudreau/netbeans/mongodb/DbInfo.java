package com.timboudreau.netbeans.mongodb;

import java.util.Objects;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 */
final class DbInfo implements Comparable<DbInfo> {

    final Lookup lookup;
    final String dbName;

    public DbInfo(Lookup lookup, String dbName) {
        Parameters.notNull("dbName", dbName);
        this.lookup = lookup;
        this.dbName = dbName;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = o instanceof DbInfo;
        if (result) {
            DbInfo other = (DbInfo) o;
            result = dbName.equals(other.dbName);
            if (result) {
                ConnectionInfo mine = lookup.lookup(ConnectionInfo.class);
                ConnectionInfo info = other.lookup.lookup(ConnectionInfo.class);
                result = Objects.equals(mine, info);
            }
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return dbName.hashCode();
    }
    
    @Override
    public String toString() {
        return dbName;
    }

    @Override
    public int compareTo(DbInfo o) {
        return toString().compareToIgnoreCase(o.toString());
    }
}
