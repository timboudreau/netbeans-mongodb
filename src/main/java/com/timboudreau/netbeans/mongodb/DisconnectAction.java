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
