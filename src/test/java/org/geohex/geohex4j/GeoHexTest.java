package org.geohex.geohex4j;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GeoHexTest {
    private double LOCATION_PRECISION = 0.0000000000001;

    @Test
    public void invalidArguments() {
        try {
            GeoHex.encode(-91, 100, 1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            GeoHex.encode(91, 100, 1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            GeoHex.encode(90, 181, 1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            GeoHex.encode(-90, -181, 1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            GeoHex.encode(0, 180, -1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            GeoHex.encode(0, -180, 25);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void getZoneByLocation() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getZoneByLocation_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            String code = v[3];
            GeoHex.Zone zone = GeoHex.getZoneByLocation(lat, lon, level);
            assertEquals(code, zone.code);
        }
    }

    @Test
    public void getXYByLocation() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getXYByLocation_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            double x = Double.parseDouble(v[3]);
            double y = Double.parseDouble(v[4]);
            GeoHex.XY xy = GeoHex.getXYByLocation(lat, lon, level);
            assertEquals(x, xy.x, LOCATION_PRECISION);
            assertEquals(y, xy.y, LOCATION_PRECISION);
        }
    }

    @Test
    public void getZoneByCode() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getZoneByCode_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            String code = v[3];
            GeoHex.Zone zone = GeoHex.getZoneByCode(code);
            assertEquals(zone.code, code);
            assertLatitude(zone.lat, lat);
            assertLongitude(zone.lon, lon);
            assertEquals(zone.getLevel(), level);
        }
    }

    @Test
    public void getZoneByXY() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getZoneByXY_v3.2.csv")) {
            double x = Double.parseDouble(v[0]);
            double y = Double.parseDouble(v[1]);
            double lat = Double.parseDouble(v[2]);
            double lon = Double.parseDouble(v[3]);
            int level = Integer.parseInt(v[4]);
            String code = v[5];
            GeoHex.Zone zone = GeoHex.getZoneByXY(x, y, level);
            assertLatitude(lat, zone.lat);
            assertLongitude(lon, zone.lon);
            assertEquals(level, zone.getLevel());
            assertEquals(code, zone.code);
        }
    }

    @Test
    public void adjustXY() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_adjustXY_v3.2.csv")) {
            double x = Double.parseDouble(v[0]);
            double y = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            double ex = Double.parseDouble(v[3]);
            double ey = Double.parseDouble(v[4]);
            GeoHex.XY resultXY = GeoHex.adjustXY(x, y, level);
            assertEquals(ex, resultXY.x, 0);
            assertEquals(ey, resultXY.y, 0);
        }
    }

    @Test
    public void encode() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getZoneByLocation_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            String code = v[3];
            String rcode = GeoHex.encode(lat, lon, level);
            assertEquals(code, rcode);
        }
    }

    @Test
    public void decode() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getZoneByCode_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            String code = v[3];
            GeoHex.Zone zone = GeoHex.decode(code);
            assertLatitude(lat, zone.lat);
            assertLongitude(lon, zone.lon);
            assertEquals(level, zone.getLevel());
        }
    }

    @Test
    public void getHexCoords() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getHexCoords_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
            double[][] expected_polygon = {
                    {Double.parseDouble(v[3]), Double.parseDouble(v[4])}, // [0]
                    {Double.parseDouble(v[5]), Double.parseDouble(v[6])}, // [1]
                    {Double.parseDouble(v[7]), Double.parseDouble(v[8])}, // [2]
                    {Double.parseDouble(v[9]), Double.parseDouble(v[10])}, // [3]
                    {Double.parseDouble(v[11]), Double.parseDouble(v[12])}, // [4]
                    {Double.parseDouble(v[13]), Double.parseDouble(v[14])}, // [6]
            };
            assertPolygon(expected_polygon, z.getHexCoords());
        }
    }

    @Test
    public void getHexSize() throws IOException {
        for (String[] v : parseCsv("test-files/testdata_getHexSize_v3.2.csv")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            double expected_hex_size = Double.parseDouble(v[3]);
            GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
            assertEquals(expected_hex_size, z.getHexSize(), LOCATION_PRECISION);
        }
    }

    @Test
    public void equalsZone() {
        GeoHex.Zone zone = GeoHex.getZoneByXY(1, 2, 6);
        GeoHex.Zone same = GeoHex.getZoneByXY(1, 2, 6);
        GeoHex.Zone other = GeoHex.getZoneByXY(1, 3, 6);
        assertTrue(zone.equals(same));
        assertFalse(zone.equals(other));
    }

    @Test
    public void hashCodeZone() {
        GeoHex.Zone zone = GeoHex.getZoneByXY(1, 2, 6);
        GeoHex.Zone same = GeoHex.getZoneByXY(1, 2, 6);
        GeoHex.Zone other = GeoHex.getZoneByXY(1, 3, 6);
        assertEquals(zone.hashCode(), same.hashCode());
        assertNotEquals(zone.hashCode(), other.hashCode());
    }

    @Test
    public void equalsXY() {
        GeoHex.XY xy = new GeoHex.XY(1, 1);
        GeoHex.XY same = new GeoHex.XY(1, 1);
        GeoHex.XY other = new GeoHex.XY(1, 2);
        assertTrue(xy.equals(same));
        assertFalse(xy.equals(other));
    }

    @Test
    public void hashCodeXY() {
        GeoHex.XY xy = new GeoHex.XY(1, 1);
        GeoHex.XY same = new GeoHex.XY(1, 1);
        GeoHex.XY other = new GeoHex.XY(1, 2);
        assertEquals(xy.hashCode(), same.hashCode());
        assertNotEquals(xy.hashCode(), other.hashCode());
    }

    @Test
    public void equalsLoc() {
        GeoHex.Loc loc = new GeoHex.Loc(1.f, 1.f);
        GeoHex.Loc same = new GeoHex.Loc(1.f, 1.f);
        GeoHex.Loc other = new GeoHex.Loc(1.f, 2.f);
        assertTrue(loc.equals(same));
        assertFalse(loc.equals(other));
    }

    @Test
    public void hashCodeLoc() {
        GeoHex.Loc loc = new GeoHex.Loc(1.f, 1.f);
        GeoHex.Loc same = new GeoHex.Loc(1.f, 1.f);
        GeoHex.Loc other = new GeoHex.Loc(1.f, 2.f);
        assertEquals(loc.hashCode(), same.hashCode());
        assertNotEquals(loc.hashCode(), other.hashCode());
    }

    private void assertPolygon(double[][] expected_polygon, GeoHex.Loc[] polygon) {
        for (int i = 0; i < expected_polygon.length; i++) {
            double[] latlon = expected_polygon[i];
            assertLatitude(latlon[0], polygon[i].lat);
            assertLongitude(latlon[1], polygon[i].lon);
        }
    }

    private void assertLatitude(double expected_latitude, double latitude) {
        assertEquals(expected_latitude, latitude, LOCATION_PRECISION);
    }

    private void assertLongitude(double expected_longitude, double longitude) {
        if (Math.abs(expected_longitude - longitude) + LOCATION_PRECISION >= 360.0) {
            if (longitude >= 0) {
                assertEquals(expected_longitude, longitude - 360.0, LOCATION_PRECISION);
            } else {
                assertEquals(expected_longitude, longitude + 360.0, LOCATION_PRECISION);
            }
        } else {
            assertEquals(expected_longitude, longitude, LOCATION_PRECISION);
        }
    }

    private List<String[]> parseCsv(String file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String[]> list = new ArrayList<String[]>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.charAt(0) == '#') continue;
            String[] verb = line.split(",");
            list.add(verb);
        }

        bufferedReader.close();
        return list;
    }
}