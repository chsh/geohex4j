# GeoHex

[![Build Status](https://travis-ci.org/teralytics/geohex.svg?branch=master)](https://travis-ci.org/teralytics/geohex)
[![Download](https://api.bintray.com/packages/teralytics/maven/geohex/images/download.svg)](https://bintray.com/teralytics/maven/geohex/_latestVersion)
[![Download](https://img.shields.io/npm/v/terahex.svg)](https://www.npmjs.com/package/terahex)

[GeoHex](http://www.geohex.org) implementation in Scala. Forked from [geohex4j](https://github.com/chsh/geohex4j).

# TeraHex

TeraHex is a geo-hashing scheme inspired by GeoHex with flexible grid sizes and encoders.


# Usage on JVM

Add dependency to `build.sbt`:

    resolvers += Resolver.bintrayRepo("teralytics", "maven")

    libraryDependencies += "net.teralytics" %% "geohex" % "0.1.+"

Use functions of the `net.teralytics.geohex.GeoHex` object:

    def encode(lat: Double, lon: Double, level: Int): String

    def decode(code: String): Zone

    def calcHexSize(level: Int): Double

Use functions of the `net.teralytics.terahex.TeraHex` object:

    def zoneByLocation(loc: LatLon, level: Int): Zone

    def encode(loc: LatLon, level: Int): Long

    def decode(code: Long): Zone

    def size(level: Int): Double


# Usage in JavaScript

Install from NPM registry with:

    $ npm install --save terahex

Or build using SBT with:

    sbt geohexJS/fastOptJS

The resulting JavaScript file will be in `js/target/scala-2.11/geohex-fastopt.js`.

Use the functions of the `terahex` object:

    function decode(code) // Returns Zone object

    function encode(lon, lat, level) // Returns string code

    function zoneByLocation(lon, lat, level) // Returns Zone object

    function size(level) // Returns hexagon side length in degrees at a given level

The resulting `Zone` object has properties:

    {
      "level": 3,
      "size": 12.345, // hexagon side length in degrees
      "location": { "lon": 0.00, "lat": 0.00 },
      "innerRadius": 11.222, // hexagon inner radius in degrees
      "geometry": [{"lon": 0.00, "lat": 0.00}, ... ], // locations of hexagon corners
      "wellKnownText": "POLYGON ((0.00 0.00, ...))", // hexagon geometry in WKT format
      "code": "1300251737352" // zone code as string
    }


# LICENSE

The MIT License (MIT)

Copyright (c) 2009 [sa2da](http://www.geohex.org)

Copyright (c) 2010 [CHIKURA Shinsaku](https://github.com/chsh), [Takuma Maruyama](https://github.com/mattak)

Copyright (c) 2015 [Teralytics AG](https://github.com/teralytics)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
