#!/usr/bin/env bash

DIR=$(dirname $0)
FILES=('testdata_hex2ll.json' 'testdata_ll2hex.json' 'testdata_ll2hexsize.json' 'testdata_ll2polygon.json')

for f in "${FILES[@]}"; do
    curl -o "$DIR/${f}" -L "https://github.com/ilyabo/geohex.js/raw/master/test/${f}"
done
