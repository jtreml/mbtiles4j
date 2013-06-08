mbtiles4j
=========

An [MBTiles](https://github.com/mapbox/mbtiles-spec) server written in Java.

[![Build Status](https://travis-ci.org/jtreml/mbtiles4j.png?branch=master)](https://travis-ci.org/jtreml/mbtiles4j)

Configuration
-------------

Edit the ``mbtiles4j.properties`` file in ``src/main/resources`` to configure your [MBTiles](https://github.com/mapbox/mbtiles-spec) databases:

```properties
tile-dbs = db1,db2,db3

db1.path = /path/to/your/database1.mbtiles
db2.path = /path/to/your/database2.mbtiles
db3.path = /path/to/your/database3.mbtiles
```

The name specified in this file will be the name used in the URL to access the tiles, e.g. the _db1_ tiles in the example configuration will be available at ``htp://<address>:<port>/mbtiles4j/db1/z/x/y.png``.

Build & Deployment
------------------

Type in 

```
mvn package
```

and deploy the ``mbtiles4j.war`` file found in ``target/`` to an application server of your choice.
