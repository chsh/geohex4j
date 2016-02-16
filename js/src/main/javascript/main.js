function findRightLevel(map) {
    var bb = map.getBounds();
    var totalLon = Math.abs(bb.getNorth() - bb.getSouth());
    var level = 1;
    while (terahex.size(level) * 5 > totalLon) { level++; }
    return level;
}

function showZone(map, loc, level) {
    var zone = terahex.zoneByLocation(loc.lng, loc.lat, level);
    var hex = omnivore.wkt
        .parse(zone.wellKnownText)
        .addTo(map);
    var text = 'Code: ' + zone.code;
    text += '<br />Level: ' + zone.level;
    text += '<br />Location: ' + zone.location.lon + ',' + zone.location.lat;
    L.popup()
        .setLatLng(zone.location)
        .setContent(text)
        .openOn(map);
}

function coverBoundingBox(map) {
    var zones = terahex.zonesWithin(90, -66.51326044311186, 180, 0, 4);
    zones.forEach(function (z) {
        omnivore.wkt
            .parse(z.wellKnownText)
            .addTo(map);
    });
}

window.onload = function() {

    var map = L.map('map');
    window.map = map;
    map.setView([40.7127, -74.0059], 15);

    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);


    function onMapClick(e) {
        var level = findRightLevel(map);
        showZone(map, e.latlng, level);
    }

    map.on('click', onMapClick);
}