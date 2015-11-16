package org.geohex.geohex4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class GeoHexTest {
    private double LOCATION_PRECISION = 0.0000000000010;

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
    public void convertCoordinatesToGeoHex() throws IOException {
        String c = GeoHex.encode(35.780516755235475, 139.57031250000003, 9);
        assertEquals("XM566370240", c);

        GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
        assertEquals("XM566370240", zone.code);

        for (String[] v : parseCsv("test-files/testdata_ll2hex.txt")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            String code = v[3];
            String rcode = GeoHex.encode(lat, lon, level);
            assertEquals(code, rcode);
        }
    }

    @Test
    public void convertGeoHexToCoordinates() throws IOException {
        GeoHex.Zone zone1 = GeoHex.decode("XM566370240");
        assertLatitude(35.78044332128244, zone1.lat);
        assertLongitude(139.57018747142206, zone1.lon);
        assertEquals(9, zone1.level);

        GeoHex.Zone zone2 = GeoHex.getZoneByCode("XM566370240");
        assertLatitude(35.78044332128244, zone2.lat);
        assertLongitude(139.57018747142206, zone2.lon);
        assertEquals(9, zone2.level);

        for (String[] v : parseCsv("test-files/testdata_hex2ll.txt")) {
            String code = v[3];
            GeoHex.Zone zone = GeoHex.decode(code);
            assertLatitude(Double.parseDouble(v[0]), zone.lat);
            assertLongitude(Double.parseDouble(v[1]), zone.lon);
            assertEquals(Integer.parseInt(v[2]), zone.level);
        }
    }

    @Test
    public void convertCoordinatesToGeoHexPolygon() throws IOException {
        GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
        double[][] polygon = {
                {35.78044332128244, 139.56951006790973},
                {35.78091924645671, 139.5698487696659},
                {35.78091924645671, 139.57052617317822},
                {35.78044332128244, 139.5708648749344},
                {35.779967393259035, 139.57052617317822},
                {35.779967393259035, 139.5698487696659}
        };
        assertPolygon(polygon, zone.getHexCoords());

        for (String[] v : parseCsv("test-files/testdata_ll2polygon.txt")) {
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
    public void convertCoordinatesToGeoHexSize() throws IOException {
        GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
        assertLatitude(37.70410702222824, zone.getHexSize());

        for (String[] v : parseCsv("test-files/testdata_ll2hexsize.txt")) {
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            double expected_hex_size = Double.parseDouble(v[3]);
            GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
            assertEquals(expected_hex_size, z.getHexSize(), LOCATION_PRECISION);
        }
    }

    private void assertPolygon(double[][] expected_polygon, GeoHex.Loc[] polygon) {
        for (int i = 0; i < expected_polygon.length; i++) {
            double[] latlon = expected_polygon[i];
            double d;
            d = latlon[0] - polygon[i].lat;
            assertEquals(0, (long) d * 1000000000000L);
            d = latlon[1] - polygon[i].lon;
            assertEquals(0, (long) d * 1000000000000L);
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