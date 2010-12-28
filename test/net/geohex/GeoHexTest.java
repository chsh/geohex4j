package net.geohex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import net.geohex.GeoHex;

public class GeoHexTest extends TestCase {
	public void testInvalidArguments() {
		try {
			GeoHex.encode(-91,100,1);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(91,100,1);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(90,181,1);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(-90,-181,1);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(0,180,-1);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(0,-180,25);
			fail();
		} catch (IllegalArgumentException expected) {}
	}
	public void testConvertCoordinatesToGeoHex() throws IOException {
		String c = GeoHex.encode(35.780516755235475, 139.57031250000003, 9);
		assertEquals("XM566370240", c);
		GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
		assertEquals("XM566370240", zone.code);
		FileReader r = new FileReader("test-files/testdata_ll2hex.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	if (line.charAt(0) == '#') continue;
	    	String[] v = line.split(",");
	    	double lat = Double.parseDouble(v[0]);
	    	double lon = Double.parseDouble(v[1]);
	    	int level = Integer.parseInt(v[2]);
	    	String code = v[3];
	    	String rcode = GeoHex.encode(lat, lon, level);
	    	assertEquals(code, rcode);
	    }
	    br.close();
	}
	public void testConvertGeoHexToCoordinates() throws IOException {
		GeoHex.Zone zone1 = GeoHex.decode("XM566370240");
		assertDouble(35.78044332128244, zone1.lat);
		assertDouble(139.57018747142206, zone1.lon);
		assertEquals(9, zone1.level);
		GeoHex.Zone zone2 = GeoHex.getZoneByCode("XM566370240");
		assertDouble(35.78044332128244, zone2.lat);
		assertDouble(139.57018747142206, zone2.lon);
		assertEquals(9, zone2.level);
	    FileReader r = new FileReader("test-files/testdata_hex2ll.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] v = line.split(",");
	    	String code = v[3];
	    	GeoHex.Zone zone = GeoHex.decode(code);
	    	double d;
	    	d = Double.parseDouble(v[0]) - zone.lat;
	    	assertEquals(0, (long)d * 10000000000000L);
	    	d = Double.parseDouble(v[1]) - zone.lon;
	    	long dl = (long)d * 10000000000000L;
	    	if (dl == -3600000000000000L || dl == 3600000000000000L) {
	    		dl = 0L;
	    	}
	    	assertEquals(0, dl);
	    	assertEquals(Integer.parseInt(v[2]), zone.level);
	    }
	    br.close();
	}
	
    public void testConvertCoordinatesToGeoHexPolygon() throws IOException {
        GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475,139.57031250000003,9);
        double[][] polygon = {
        		{ 35.78044332128244,139.56951006790973 },
        		{ 35.78091924645671,139.5698487696659 },
        		{ 35.78091924645671,139.57052617317822 },
        		{ 35.78044332128244,139.5708648749344 },
        		{ 35.779967393259035,139.57052617317822 },
        		{ 35.779967393259035,139.5698487696659 }
        };
        assertPolygon(polygon, zone.getHexCoords());
        FileReader r = new FileReader("test-files/testdata_ll2polygon.txt");
        BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == '#') continue;
            String[] v = line.split(",");
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
            double[][] expected_polygon = {
                    { Double.parseDouble(v[3]), Double.parseDouble(v[4]) }, // [0]
                    { Double.parseDouble(v[5]), Double.parseDouble(v[6]) }, // [1]
                    { Double.parseDouble(v[7]), Double.parseDouble(v[8]) }, // [2]
                    { Double.parseDouble(v[9]), Double.parseDouble(v[10]) }, // [3]
                    { Double.parseDouble(v[11]), Double.parseDouble(v[12]) }, // [4]
                    { Double.parseDouble(v[13]), Double.parseDouble(v[14]) }, // [6]
            };
            assertPolygon(expected_polygon, z.getHexCoords());
        }
        br.close();
    }
 
    public void testConvertCoordinatesToGeoHexSize() throws IOException {
    	GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475,139.57031250000003,9);
        assertEquals(37.70410702222824, zone.getHexSize());
        FileReader r = new FileReader("test-files/testdata_ll2hexsize.txt");
        BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == '#') continue;
            String[] v = line.split(",");
            double lat = Double.parseDouble(v[0]);
            double lon = Double.parseDouble(v[1]);
            int level = Integer.parseInt(v[2]);
            double expected_hex_size = Double.parseDouble(v[3]);
            GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
            assertEquals(expected_hex_size, z.getHexSize());
        }
        br.close();
    }

    private void assertDouble(double expected, double actual) {
		assertEquals((long)(expected * 10000000000000L),
				(long)(actual * 10000000000000L));
	}

    private void assertPolygon(double[][] expected_polygon, GeoHex.Loc[] polygon) {
        for (int i = 0; i < expected_polygon.length; i++) {
            double[] latlon = expected_polygon[i];
            double d;
            d = latlon[0] - polygon[i].lat;
            assertEquals(0, (long)d * 1000000000000L);
            d = latlon[1] - polygon[i].lon;
            assertEquals(0, (long)d * 1000000000000L);
        }
    }
}
