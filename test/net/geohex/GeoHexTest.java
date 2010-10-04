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
		GeoHex.Zone z = GeoHex.decode("jjdK3");
		assertEquals(35.780516755235475, z.lat);
		assertEquals(139.57031250000003, z.lon);
		assertEquals(9, z.level);
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

}
