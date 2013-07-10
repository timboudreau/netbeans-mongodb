package com.timboudreau.netbeans.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
final class CollectionNode extends AbstractNode {

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.lookup));
    }

    CollectionNode(CollectionInfo collection, InstanceContent content, ProxyLookup lkp) {
        super(Children.create(new CollectionChildFactory(lkp), true), lkp);
        content.add(collection);
        content.add(collection, new CollectionConverter());
        setDisplayName(collection.name);
        setName(collection.name);
        setIconBaseWithExtension(MongoServicesNode.MONGO_COLLECTION);
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new CollectionNameProperty(getLookup()));
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new ConnectionHostProperty(getLookup()));
        set.put(new ConnectionPortProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    static int maxCursorSize = 40;

    private class CollectionConverter implements InstanceContent.Convertor<CollectionInfo, DBCollection> {

        @Override
        public DBCollection convert(CollectionInfo t) {
            DB db = getLookup().lookup(DB.class);
            return db.getCollection(t.name);
        }

        @Override
        public Class<? extends DBCollection> type(CollectionInfo t) {
            return DBCollection.class;
        }

        @Override
        public String id(CollectionInfo t) {
            return t.name;
        }

        @Override
        public String displayName(CollectionInfo t) {
            return id(t);
        }
    }
}
