NetBeans MongoDB
================

A [NetBeans](http://netbeans.org) plugin for browsing [MongoDB](http://mongodb.org).  It adds
a node to the Services tab.  Right click it to add servers.

You can browse into collections;  responses to queries are browsable as subnodes;  the property sheet
shows all non-Map, non-List properties (lists and Maps become additional subnodes).

![NetBeans MongoDB Plugin Screen Shot](screenshot.png "NetBeans MongoDB Plugin Screen Shot")


Status
------

Stable and works.  One known issue: The MongoDB driver insists on logging connection exceptions as
``Level.SEVERE`` before the plugin can intercept them.  So currently, you may see some popup 
exception dialogs - they are harmless.

Note that as yet there is no write support, just browsing.


Build And Run / Downloads
-------------------------

It's a Maven project built using the NBM Maven Plugin - just check out and build.

A continuous build of it, with download, exists [on timboudreau.com](http://timboudreau.com/builds/job/netbeans-mongodb/).  To install in NetBeans, simply download and install using Tools | Plugins on the Downloaded tab.


License
-------

MIT license

