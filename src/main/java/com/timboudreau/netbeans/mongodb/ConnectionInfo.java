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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 */
final class ConnectionInfo implements Comparable<ConnectionInfo>, AutoCloseable {

    public static final String PREFS_KEY_DISPLAY_NAME = "displayName"; //NOI18N
    public static final String PREFS_KEY_HOST = "host"; //NOI18N
    public static final String PREFS_KEY_PORT = "port"; //NOI18N
    public static final String PREFS_KEY_ID = "id"; //NOI18N
    public static final String DEFAULT_HOST = "localhost"; //NOI18N
    public static final int DEFAULT_PORT = 27017;

    private final Preferences node;
    private final String id;
    private static volatile int count;

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    public ConnectionInfo(String id, Preferences node) {
        Parameters.notNull("node", node); //NOI18N
        this.node = node;
        this.id = id;
    }

    public ConnectionInfo(Preferences parent) {
        id = System.currentTimeMillis() + "-" + count++; //NOI18N
        node = parent.node(id);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supp.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supp.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        supp.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        supp.removePropertyChangeListener(propertyName, listener);
    }

    public String getDisplayName() {
        return node.get(PREFS_KEY_DISPLAY_NAME, toString());
    }

    public void setDisplayName(String displayName) {
        Parameters.notNull(PREFS_KEY_DISPLAY_NAME, displayName);
        String old = getDisplayName();
        if (!displayName.equals(old)) {
            if (displayName.trim().isEmpty()) {
                node.remove(PREFS_KEY_DISPLAY_NAME);
            } else {
                node.put(PREFS_KEY_DISPLAY_NAME, displayName.trim());
            }
            supp.firePropertyChange(PREFS_KEY_DISPLAY_NAME, old, displayName.trim());
        }
    }

    public String getHost() {
        return node.get(PREFS_KEY_HOST, DEFAULT_HOST);
    }

    public int getPort() {
        return node.getInt(PREFS_KEY_PORT, DEFAULT_PORT);
    }

    public void setHost(String host) {
        Parameters.notNull(PREFS_KEY_HOST, host);
        String old = getHost();
        if (!host.equals(old)) {
            node.put(PREFS_KEY_HOST, host);
            supp.firePropertyChange(PREFS_KEY_HOST, old, host);
        }
    }

    public void setPort(int port) {
        int old = getPort();
        if (old != port) {
            node.putInt(PREFS_KEY_PORT, port);
            supp.firePropertyChange(PREFS_KEY_PORT, old, port);
        }
    }

    @Override
    public String toString() {
        return getHost() + ":" + getPort() + " (" + id + ")"; //NOI18N
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionInfo && ((ConnectionInfo) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(ConnectionInfo o) {
        return id.compareTo(o.id);
    }

    private void save() {
        try {
            node.parent().flush();
            node.parent().sync();
            node.flush();
            node.parent().sync();
            node.sync();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    String id() {
        return id;
    }

    @Override
    public void close() {
        save();
    }

}
