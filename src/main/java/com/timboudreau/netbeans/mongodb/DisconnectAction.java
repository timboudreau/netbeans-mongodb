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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
@Messages("ACTION_Disconnect=Disconnect")
public class DisconnectAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private Lookup lkp;

    public DisconnectAction(Lookup lkp) {
        super(Bundle.ACTION_Disconnect());
        this.lkp = lkp;
    }

    public DisconnectAction() {
        this(Utilities.actionsGlobalContext());
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        if (getPropertyChangeListeners().length == 1) {
            addNotify();
        }
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
        if (getPropertyChangeListeners().length == 0) {
            removeNotify();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MongoDisconnect disconnect = lkp.lookup(MongoDisconnect.class);
        if (disconnect != null) {
            disconnect.close();
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new DisconnectAction(lkp);
    }

    private Lookup.Result<MongoDisconnect> res;

    private void addNotify() {
        res = lkp.lookupResult(MongoDisconnect.class);
        res.addLookupListener(this);
        res.allInstances();
        resultChanged(new LookupEvent(res));
    }

    private void removeNotify() {
        res.removeLookupListener(this);
        res = null;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Lookup.Result res = (Lookup.Result) le.getSource();
        setEnabled(!res.allItems().isEmpty());
    }
}
