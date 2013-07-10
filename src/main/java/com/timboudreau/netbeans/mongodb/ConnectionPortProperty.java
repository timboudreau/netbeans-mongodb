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

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 */
@Messages("ConnectionPort=Port")
public class ConnectionPortProperty extends PropertySupport.ReadWrite<Integer> {

    private final Lookup lkp;

    ConnectionPortProperty(Lookup lkp) {
        super("connectionPort", Integer.class, Bundle.ConnectionPort(), null);
        this.lkp = lkp;
    }

    @Override
    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        return info == null ? -1 : info.getPort();
    }

    @Override
    public void setValue(Integer t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        if (info != null) {
            info.setPort(t);
        }
    }
}
