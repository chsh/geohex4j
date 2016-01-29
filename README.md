# geohex4j

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.geohex.geohex4j/geohex4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser)

GeoHex for Java

# DESCRIPTION

GeoHex V3 for Java implemented by CHIKURA Shinsaku.

- http://www.simplegimmick.com/
- http://twitter.com/chshii

# GeoHex class

GeoHex class can convert between latitude/longitude and GeoHex

V3: ported from JavaScript implementation by @sa2da.

Currently only #decode, #encode, #getZoneByLocation and #getZoneByCode methods are ready.

# INSTALL

Gradle:

```
compile 'org.geohex.geohex4j:geohex4j:3.2.2'
```

or simply copy org.geohex.geohex4j.GeoHex.java into your app.

# USAGE

```java
import org.geohex.geohex4j.GeoHex;

String code = GeoHex.encode(35.780516755235475, 139.57031250000003, 9);
// code -> "XM566370240"
GeoHex.Zone zone1 = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
// zone1.lat -> 35.78044332128247
// zone1.lon -> 139.57018747142203
// zone1.level -> 9
// zone1.getHexSize() -> 37.70410702222824

GeoHex.Zone zone2 = GeoHex.getZoneByCode("XM566370240");
// zone2.lat -> 35.78044332128247
// zone2.lon -> 139.57018747142203
// zone2.level -> 9
// zone2.getHexSize() -> 37.70410702222824
...
```

# LICENSE

The MIT License (MIT), in honor of @sa2da. http://twitter.com/sa2da
comes from http://www.geohex.org/

Copyright (C) 2010 @chsh, @mattak

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
