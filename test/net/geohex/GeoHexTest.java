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
	    	assertEquals(0, (long)d * 10000000000000L);
	    	assertEquals(Integer.parseInt(v[2]), zone.level);
	    }
	    br.close();
	}
	
	private void assertDouble(double expected, double actual) {
		assertEquals((long)(expected * 10000000000000L),
				(long)(actual * 10000000000000L));
	}

}
