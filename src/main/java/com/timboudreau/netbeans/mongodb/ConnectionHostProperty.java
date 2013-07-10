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
@Messages("ConnectionHost=Host")
public class ConnectionHostProperty extends PropertySupport.ReadWrite<String> {

    private final Lookup lkp;

    ConnectionHostProperty(Lookup lkp) {
        super("connectionHost", String.class, Bundle.ConnectionHost(), null);
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        return info == null ? "[no value]" : info.getHost();
    }

    @Override
    public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ConnectionInfo info = lkp.lookup(ConnectionInfo.class);
        if (info != null) {
            info.setHost(t);
        }
    }
}
