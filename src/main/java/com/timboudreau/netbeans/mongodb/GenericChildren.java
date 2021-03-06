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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class GenericChildren extends Children.Keys<String> {

    private final java.util.Map<String, Object> map;
    private final Lookup lkp;

    GenericChildren(java.util.Map<String, Object> map, Lookup lkp) {
        this.map = map;
        this.lkp = lkp;
    }

    @Override
    protected void addNotify() {
        List<String> relevant = new ArrayList<>(map.size());
        for (java.util.Map.Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() instanceof java.util.Map || e.getValue() instanceof List<?>) {
                relevant.add(e.getKey());
            }
        }
        setKeys(relevant);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Node[] createNodes(String t) {
        Object o = map.get(t);
        if (o instanceof java.util.Map) {
            java.util.Map<String, Object> childMap = (java.util.Map<String, Object>) map.get(t);
            Node result = new GenericNode(lkp, t, childMap);
            return new Node[]{result};
        } else if (o instanceof List<?>) {
            List<?> list = (List<?>) o;
            GenericNode nue = new GenericNode(lkp, t, listToMap(list, t));
            return new Node[] { nue };
        } else {
//            List<?> l = (List<?>) o;
//            for (Object o : l) {
//                
//            }
            return new Node[0];
        }
    }
    
    private java.util.Map<String,Object> listToMap(List<?> list, String name) {
        java.util.Map<String,Object> result = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            result.put(name + " " + i, list.get(i));
        }
        return result;
    }
}
