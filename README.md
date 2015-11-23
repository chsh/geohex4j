# GeoHex

[![Build Status](https://travis-ci.org/teralytics/geohex.svg?branch=master)](https://travis-ci.org/teralytics/geohex)
[![Download](https://api.bintray.com/packages/teralytics/maven/geohex/images/download.svg)](https://bintray.com/teralytics/maven/geohex/_latestVersion)

[GeoHex](http://www.geohex.org) implementation in Scala. Forked from [geohex4j](https://github.com/chsh/geohex4j).

# TeraHex

TeraHex is a geo-hashing scheme inspired by GeoHex with flexible grid sizes and encoders.

# Usage

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
