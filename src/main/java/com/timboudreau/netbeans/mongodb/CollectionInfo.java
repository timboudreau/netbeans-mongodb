package com.timboudreau.netbeans.mongodb;

import java.util.Objects;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 */
final class CollectionInfo implements Comparable<CollectionInfo> {

    final String name;
    final Lookup lookup;

    public CollectionInfo(String name, Lookup lookup) {
        Parameters.notNull("lookup", lookup);
        Parameters.notNull("name", name);
        this.name = name;
        this.lookup = lookup;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = o instanceof CollectionInfo;
        if (result) {
            CollectionInfo other = (CollectionInfo) o;
            result = name.equals(other.name);
            if (result) {
                DbInfo mine = lookup.lookup(DbInfo.class);
                DbInfo info = other.lookup.lookup(DbInfo.class);
                result = Objects.equals(mine, info);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(CollectionInfo o) {
        return name.compareToIgnoreCase(o.name);
    }
}
