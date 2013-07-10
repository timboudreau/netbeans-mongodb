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

import com.mongodb.MongoClient;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.ConnectException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
class OneConnectionNode extends AbstractNode implements PropertyChangeListener {

    private MongoClient mongo;
    private static final Logger logger = Logger.getLogger(OneConnectionNode.class.getName());
    private final Object lock = new Object();
    private final Disconnecter disconnecter = new Disconnecter();
    private final InstanceContent content;
    private final Problems problems = new Problems();
    private final ConnectionConverter converter = new ConnectionConverter();
    private volatile boolean problem;
    @StaticResource
    private final static String ERROR_BADGE = "com/timboudreau/netbeans/mongodb/error.png"; //NOI18N

    OneConnectionNode(ConnectionInfo connection) {
        this(connection, new InstanceContent());
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection)));
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content, ProxyLookup lkp) {
        super(Children.create(new OneConnectionChildren(lkp), true), lkp);
        this.content = content;
        content.add(problems);
        content.add(connection, converter);
        setDisplayName(connection.getDisplayName());
        setName(connection.id());
        setIconBaseWithExtension(MongoServicesNode.MONGO_CONNECTION);
        connection.addPropertyChangeListener(WeakListeners.propertyChange(this, connection));
    }

    private void setProblem(boolean problem) {
        this.problem = problem;
    }

    private boolean isProblem() {
        return problem;
    }

    @Override
    public Image getIcon(int ignored) {
        Image result = super.getIcon(ignored);
        if (isProblem()) {
            Image errorBadge = ImageUtilities.loadImage(ERROR_BADGE);
            result = ImageUtilities.mergeImages(result, errorBadge, 0, 0);
        }
        return result;
    }

    @Override
    public Image getOpenedIcon(int ignored) {
        return getIcon(ignored);
    }

    @Override
    public Action[] getActions(boolean ignored) {
        Action[] orig = super.getActions(ignored);
        Action[] nue = new Action[orig.length + 1];
        System.arraycopy(orig, 0, nue, 1, orig.length);
        nue[0] = new DisconnectAction(getLookup());
        return nue;
    }

    private MongoClient connect(boolean create) {
        synchronized (lock) {
            if (create && (mongo == null || !mongo.getConnector().isOpen())) {
                ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
                try {
                    mongo = new MongoClient(connection.getHost(), connection.getPort());
                    mongo.getDatabaseNames();
                    content.add(disconnecter);
                    setProblem(false);
                } catch (Exception ex) {
                    problems.handleException(ex, null);
                    if (ex instanceof ConnectException) {
                        // replaces the children so we can try again
                        disconnecter.close();
                    }
                }
            }
        }
        return mongo;
    }

    private class Problems extends ConnectionProblems {

        @Override
        public void handleException(Exception ex, String logMessage) {
            ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
            if (logMessage == null) {
                logMessage = ex.getMessage();
            }
            if (logMessage == null) {
                logMessage = "Problem connecting to " + connection;
            }
            logger.log(Level.FINE, logMessage, ex); //NOI18N
            String msg = ex.getLocalizedMessage();
            if (msg != null) {
                setShortDescription(msg);
            }
            setProblem(true);
            StatusDisplayer.getDefault().setStatusText(logMessage);
            fireIconChange();
        }

        @Override
        protected <T> T retry(Callable<T> callable, Exception ex) throws Exception {
            try {
                MongoClient client;
                synchronized (lock) {
                    client = mongo;
                    mongo = null;
                }
                if (client != null && client.getConnector().isOpen()) {
                    client.close();
                }
            } catch (Exception e) {
                disconnecter.close();
                logger.log(Level.INFO, "Reconnecting", e);
            } finally {
                ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
                content.remove(info, converter);
                content.add(info, converter);
            }
            connect(true);
            return callable.call();
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new ConnectionHostProperty(getLookup()));
        set.put(new ConnectionPortProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ConnectionInfo.PREFS_KEY_DISPLAY_NAME:
                setDisplayName((String) evt.getNewValue());
                break;
            case ConnectionInfo.PREFS_KEY_HOST:
            case ConnectionInfo.PREFS_KEY_PORT:
                MongoDisconnect disconnect = getLookup().lookup(MongoDisconnect.class);
                if (disconnect != null) {
                    disconnect.close();
                }
                break;
        }
    }

    private class Disconnecter extends MongoDisconnect implements Runnable {

        @Override
        public void close() {
            setChildren(Children.create(new OneConnectionChildren(getLookup()), true));
            RequestProcessor.getDefault().post(this);
            setProblem(false);
            setShortDescription("");
        }

        @Override
        public void run() {
            MongoClient client;
            try {
                synchronized (lock) {
                    client = mongo;
                    mongo = null;
                }
                if (client != null) {
                    client.close();
                }
            } finally {
                ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
                content.remove(info, converter);
                content.add(info, converter);
            }
        }
    }

    private class ConnectionConverter implements InstanceContent.Convertor<ConnectionInfo, MongoClient> {

        @Override
        public MongoClient convert(ConnectionInfo t) {
            return connect(true);
        }

        @Override
        public Class<? extends MongoClient> type(ConnectionInfo t) {
            return MongoClient.class;
        }

        @Override
        public String id(ConnectionInfo t) {
            return "mongo"; //NOI18N
        }

        @Override
        public String displayName(ConnectionInfo t) {
            return id(t);
        }
    }
}
