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

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
public class OneDbNode extends AbstractNode {

    public OneDbNode(DbInfo info) {
        this(info, new InstanceContent());
    }

    OneDbNode(DbInfo info, InstanceContent content) {
        this(info, content, new AbstractLookup(content));
    }

    OneDbNode(DbInfo info, InstanceContent content, AbstractLookup lkp) {
        this(info, content, new ProxyLookup(info.lookup, lkp, Lookups.fixed(info)));
    }

    @Messages("DB_NAME_DESC=The name of the database")
    OneDbNode(DbInfo info, InstanceContent content, ProxyLookup lkp) {
        super(Children.create(new OneDBChildren(lkp), true), lkp);
        content.add(info, new DBConverter());
        setName(info.dbName);
        setDisplayName(info.dbName);
        setIconBaseWithExtension(MongoServicesNode.MONGO_DB);
    }

    @Override
    @Messages("DB_NAME=Database Name")
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new ConnectionHostProperty(getLookup()));
        set.put(new ConnectionPortProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    private class DBConverter implements InstanceContent.Convertor<DbInfo, DB> {

        @Override
        public DB convert(DbInfo t) {
            DbInfo info = getLookup().lookup(DbInfo.class);
            MongoClient client = getLookup().lookup(MongoClient.class);
            return client.getDB(info.dbName);
        }

        @Override
        public Class<? extends DB> type(DbInfo t) {
            return DB.class;
        }

        @Override
        public String id(DbInfo t) {
            return t.dbName;
        }

        @Override
        public String displayName(DbInfo t) {
            return id(t);
        }
    }
}
