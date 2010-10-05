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
		assertEquals("jjdK3", c);
		GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
		assertEquals("jjdK3", zone.code);
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
		GeoHex.Zone zone1 = GeoHex.decode("jjdK3");
		assertEquals(35.780516755235475, zone1.lat);
		assertEquals(139.57031250000003, zone1.lon);
		assertEquals(9, zone1.level);
		GeoHex.Zone zone2 = GeoHex.getZoneByCode("jjdK3");
		assertEquals(35.780516755235475, zone2.lat);
		assertEquals(139.57031250000003, zone2.lon);
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
	    	assertEquals(0, (long)d * 1000000000000L);
	    	d = Double.parseDouble(v[1]) - zone.lon;
	    	assertEquals(0, (long)d * 1000000000000L);
	    	assertEquals(Integer.parseInt(v[2]), zone.level);
	    }
	    br.close();
	}
	public void testConvertCoordinatesToGeoHexPolygon() throws IOException {
		GeoHex.Zone zone = GeoHex.getZoneByLocation(35.68526754622903,139.76698413491252,23);
		double[][] polygon = {
				{ 35.685262361266446,139.7669792175293 },
				{ 35.68527242369706,139.76698637008667 },
				{ 35.68527242369706,139.76700067520142 },
				{ 35.685262361266446,139.7670078277588 },
				{ 35.685252298834484,139.76700067520142 },
				{ 35.685252298834484,139.76698637008667 }
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
		GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
		assertEquals(13045.252825520833, zone.getHexSize());
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

	public void testConvertCoordinatesToXY() throws IOException {
		GeoHex.Zone zone = GeoHex.getZoneByLocation(35.780516755235475, 139.57031250000003, 9);
		assertEquals("293.0,-104.0", "" + zone.x + "," + zone.y);
		FileReader r = new FileReader("test-files/testdata_ll2xy.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	if (line.charAt(0) == '#') continue;
	    	String[] v = line.split(",");
	    	double lat = Double.parseDouble(v[0]);
	    	double lon = Double.parseDouble(v[1]);
	    	int level = Integer.parseInt(v[2]);
	    	double expected_x = Double.parseDouble(v[3]);
	    	double expected_y = Double.parseDouble(v[4]);
	    	GeoHex.Zone z = GeoHex.getZoneByLocation(lat, lon, level);
	    	assertEquals("" + expected_x + "," + expected_y,
	    			"" + z.x + "," + z.y);
	    }
	    br.close();
	}

	public void testConvertXYToZone() throws IOException {
		GeoHex.Zone zone = GeoHex.getZoneByXY(293.0, -104.0, 9);
		assertEquals("35.780516755235475,139.57031250000003",
				"" + zone.lat + "," + zone.lon);
		FileReader r = new FileReader("test-files/testdata_xy2ll.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	if (line.charAt(0) == '#') continue;
	    	String[] v = line.split(",");
	    	double x = Double.parseDouble(v[0]);
	    	double y = Double.parseDouble(v[1]);
	    	double expected_lat = Double.parseDouble(v[2]);
	    	double expected_lon = Double.parseDouble(v[3]);
	    	int level = Integer.parseInt(v[4]);
	    	String expected_code = v[5];
	    	GeoHex.Zone z = GeoHex.getZoneByXY(x, y, level);
	    	double d;
	    	d = expected_lat - z.lat;
	    	assertEquals(0, (long)d * 1000000000000L);
	    	d = expected_lon - z.lon;
	    	assertEquals(0, (long)d * 1000000000000L);
	    	assertEquals(level, z.level);
	    	assertEquals(expected_code, z.code);
	    }
	    br.close();
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
