package net.geohex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

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
			GeoHex.encode(0,180,0);
			fail();
		} catch (IllegalArgumentException expected) {}
		try {
			GeoHex.encode(0,-180,61);
			fail();
		} catch (IllegalArgumentException expected) {}
	}
	
	public void testConvertCoodinatesToGeoHex() throws IOException {
		// simple test
	    assertEquals("132KpuG", GeoHex.encode(35.647401,139.716911,1));
	    assertEquals("G028k", GeoHex.encode(24.340565,124.156201,42));
	    FileReader r = new FileReader("test-files/testdata_ll2hex.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] v = line.split("\\t");
	    	double lat = Double.parseDouble(v[1]);
	    	double lon = Double.parseDouble(v[2]);
	    	int level = Integer.parseInt(v[3]);
	    	assertEquals(v[0], GeoHex.encode(lat, lon, level));
	    }
	    br.close();
	}
	public void testConvertGeoHexToCoodinates() throws IOException {
	    // simple test
	    assertEquals("{35.6478085,139.7173629550321}:1", GeoHex.decode("132KpuG").toString());
	    assertEquals("{24.338279000000004,124.1577708779443}:7", GeoHex.decode("70dMV").toString());  
	    assertEquals("{24.338279000000004,124.1577708779443}:7", GeoHex.decode("0dMV").toString());  
	    FileReader r = new FileReader("test-files/testdata_hex2ll.txt");
	    BufferedReader br = new BufferedReader(r);
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] v = line.split("\\t");
	    	assertEquals("{" + v[1] + "," + v[2] + "}:" + v[3], GeoHex.decode(v[0]).toString());
	    }
	    br.close();
	}
}
