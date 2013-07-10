package com.timboudreau.netbeans.mongodb;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
final class GenericNode extends AbstractNode {

    public GenericNode(Lookup lkp, String name, Map<String, Object> map) {
        this(name, map, new ProxyLookup(Lookups.fixed(map), lkp, Lookups.fixed(map.values().toArray(new Object[0]))));
    }

    public GenericNode(String name, Map<String, Object> map, ProxyLookup lkp) {
        super(hasMapValues(map)
                ? new GenericChildren(map, lkp)
                : Children.LEAF, lkp);

        setName(name);
        setDisplayName(name);
        setIconBaseWithExtension(MongoServicesNode.MONGO_ITEM);
    }

    @Override
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(getName());
        char c = sb.length() == 0 ? 'x' : sb.charAt(0);
        sb.setCharAt(0, Character.toUpperCase(c));
        return sb.toString();
    }

    private static boolean hasMapValues(Map<String, Object> m) {
        boolean result = false;
        for (Map.Entry<String, Object> e : m.entrySet()) {
            if (e.getValue() instanceof Map) {
                result = true;
                break;
            }
        }
        return result;
    }

    private final HashSet<Class<?>> types = new HashSet<>(Arrays.asList(new Class<?>[]{
        Integer.class,
        Integer.TYPE,
        Long.class,
        Long.TYPE,
        String.class,
        Character.TYPE,
        Short.TYPE,
        Short.class,
        Float.class,
        Float.TYPE,
        Double.class,
        Double.TYPE,
        Byte.class,
        Byte.TYPE,
        Boolean.class,
        Boolean.TYPE
    }));

    private boolean isJsonPrimitiveType(Object o) {
        return types.contains(o.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        Map<String, Object> map = getLookup().lookup(Map.class);
        List<Map.Entry<String, Object>> pairs = new ArrayList<>(map.entrySet());
        Collections.sort(pairs, new EntryComparator());
        for (Map.Entry<String, Object> e : pairs) {
            if (isJsonPrimitiveType(e.getValue())) {
                createProperty(e.getKey(), e.getValue(), set);
            }
        }
        sheet.put(set);

        Sheet.Set expert = Sheet.createExpertSet();
        expert.put(new CollectionNameProperty(getLookup()));
        expert.put(new DatabaseNameProperty(getLookup()));
        expert.put(new ConnectionNameProperty(getLookup()));
        expert.put(new ConnectionHostProperty(getLookup()));
        expert.put(new ConnectionPortProperty(getLookup()));
        sheet.put(expert);
        return sheet;
    }

    private static class EntryComparator implements Comparator<Map.Entry<String, Object>> {

        @Override
        public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
            return o1.getKey().compareToIgnoreCase(o2.getKey());
        }

    }

    private <T> void createProperty(String s, T value, Sheet.Set set) {
        set.put(new ItemProp<>(s, value, s, null));
    }

    private class ItemProp<T> extends PropertySupport.ReadWrite<T> {

        @SuppressWarnings("unchecked")
        public ItemProp(String name, T value, String displayName, String shortDescription) {
            super(name, (Class<T>) value.getClass(), displayName, shortDescription);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            Map<String, Object> map = getLookup().lookup(Map.class);
            return (T) map.get(getName());
        }

        @Override
        public String getDisplayName() {
            String s = super.getDisplayName();
            if (s != null && !s.isEmpty()) {
                StringBuilder sb = new StringBuilder(s);
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                s = sb.toString();
            }
            return s;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValue(T t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Map<String, Object> map = getLookup().lookup(Map.class);
            if (t == null) {
                map.remove(getName());
            } else {
                map.put(getName(), t);
            }
        }
    }
}
