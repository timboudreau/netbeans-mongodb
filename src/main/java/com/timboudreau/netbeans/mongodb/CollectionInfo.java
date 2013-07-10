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
