package net.geohex;

public class GeoHex {
	public static final String VERSION = "1.0.0";
	
	public static final String H_KEY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWX";

	public static class Zone {
		public static final int DEFAULT_LEVEL = 7;
		public double lat, lon;
		public int level;
		public Zone(double lat, double lon, int level) {
			this.lat = lat;
			this.lon = lon;
			this.level = level;
			verifyLatLonRange(this.lat, this.lon, this.level);
		}
		public Zone(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
			this.level = DEFAULT_LEVEL;
			verifyLatLonRange(this.lat, this.lon, this.level);
		}
		private void verifyLatLonRange(double lat, double lon, int level) {
			if (lat < -90 || lat > 90) 
				throw new IllegalArgumentException("latitude must be between -90 and 90");
			if (lon < -180 || lon > 180)
				throw new IllegalArgumentException("longitude must be between -180 and 180");
			if (level < 1 || level > 60) 
				throw new IllegalArgumentException("level must be between 1 and 60");
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append('{');
			sb.append(lat);
			sb.append(',');
			sb.append(lon);
			sb.append("}:");
			sb.append(level);
			return sb.toString();
		}
	}
	public static final double MIN_X_LON = 122930.0; // 与那国島
	public static final double MIN_X_LAT = 24448.0;
	public static final double MIN_Y_LON = 141470.0; // 南硫黄島	
	public static final double  MIN_Y_LAT = 24228.0;
	public static final int H_GRID = 1000;
	public static final double H_SIZE = 0.5;

	private String code;
	private Zone zone;
	
	public String getCode() { return code; }
	public Zone getZone() { return zone; }
	
	public GeoHex(double lat, double lon, int level) {
		this.zone = new Zone(lat, lon, level);
		this.code = encode(this.zone);
	}
	public GeoHex(Zone zone) {
		this.zone = zone;
		this.code = encode(this.zone);
	}
	public GeoHex(String code) {
		this.code = code;
		this.zone = decode(this.code);
	}
	
	public static String encode(double lat, double lon, int level) {
		return encode(new Zone(lat, lon, level));
	}
	public static String encode(Zone zone) {
	    double lon_grid = zone.lon * H_GRID;
	    double lat_grid = zone.lat * H_GRID;
	    double unit_x   = 6.0  * zone.level * H_SIZE;
	    double unit_y   = 2.8  * zone.level * H_SIZE;
	    double h_k      = ((double)Math.round( (1.4 / 3) * H_GRID) / H_GRID);

	    double base_x   = Math.floor( (MIN_X_LON + MIN_X_LAT / h_k      ) / unit_x);
	    double base_y   = Math.floor( (MIN_Y_LAT - h_k      * MIN_Y_LON) / unit_y);
	    double h_pos_x  = ( lon_grid + lat_grid / h_k     ) / unit_x - base_x;
	    double h_pos_y  = ( lat_grid - h_k      * lon_grid) / unit_y - base_y;
	    long h_x_0    = (long)Math.floor(h_pos_x);
	    long h_y_0    = (long)Math.floor(h_pos_y);
	    double h_x_q    = Math.floor((h_pos_x - h_x_0) * 100) / 100;
	    double h_y_q    = Math.floor((h_pos_y - h_y_0) * 100) / 100;
	    long h_x      = Math.round(h_pos_x);
	    long h_y      = Math.round(h_pos_y);

	      
	    if ( h_y_q > -h_x_q + 1 ) { 
	      if ( h_y_q < (2 * h_x_q ) &&  h_y_q > (0.5 * h_x_q ) ) {
	        h_x = h_x_0 + 1;
	        h_y = h_y_0 + 1;
	      }
	    } else if ( h_y_q < -h_x_q + 1 ) { 
	    	if( (h_y_q > (2 * h_x_q ) - 1 ) && ( h_y_q < ( 0.5 * h_x_q ) + 0.5 ) ) { 
	    		h_x = h_x_0;
	    		h_y = h_y_0;
	    	}
	    }
	    return hyhx2geohex( h_y, h_x, zone.level);
	}
	private static String hyhx2geohex(double h_y, double h_x, int level) { 
	    long h_x_100 = (long)Math.floor( h_x / 3600);
	    long h_x_10  = (long)Math.floor((h_x % 3600) / 60);
	    long h_x_1   = (long)Math.floor((h_x % 3600) % 60);
	    long h_y_100 = (long)Math.floor( h_y / 3600);
	    long h_y_10  = (long)Math.floor((h_y % 3600) / 60);
	    long h_y_1   = (long)Math.floor((h_y % 3600) % 60);
	    
	    long[] indexes = null;

	    if ( level < 7 ) {
	    	indexes = new long[]{ level % 60, h_x_100, h_y_100, h_x_10, h_y_10, h_x_1, h_y_1 };
	    } else if ( level == 7 ) { 
	    	indexes = new long[]{ h_x_10, h_y_10, h_x_1, h_y_1 };
	    } else { 
	    	indexes = new long[]{ level % 60, h_x_10, h_y_10, h_x_1, h_y_1 };
	    }
	    return codeFrom(H_KEY, indexes);
	}
	private static String codeFrom(String string, long[] points) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < points.length; i++) {
			sb.append(string.charAt((int)points[i]));
		}
		return sb.toString();
	}

	public static Zone decode(String code) {
		HyHx hh = geohex2hyhx(code);
	    
	    double h_lat = ( hh.h_k   * ( hh.h_x + hh.base_x ) * hh.unit_x + ( hh.h_y + hh.base_y ) * hh.unit_y ) / 2;
	    double h_lon = ( h_lat - ( hh.h_y + hh.base_y ) * hh.unit_y ) / hh.h_k;
	    double lat      = h_lat / H_GRID;
	    double lon      = h_lon / H_GRID;

	    return new Zone(lat, lon, hh.level); 
	}

	private static final class HyHx {
		public double h_y;
		public double h_x;
		public int level;
		public double unit_x;
		public double unit_y;
		public double h_k;
		public double base_x;
		public double base_y;
		public HyHx(double h_y, double h_x, int level, double unit_x, double unit_y,
				double h_k, double base_x, double base_y) {
			this.h_y = h_y;
			this.h_x = h_x;
			this.level = level;
			this.unit_x = unit_x;
			this.unit_y = unit_y;
			this.h_k = h_k;
			this.base_x = base_x;
			this.base_y = base_y;
		}
	}

	private static final class Level {
		public int level;
		public int c_length;
		public char[] code;
		public Level(int level, int c_length, char[] code) {
			this.level = level;
			this.c_length = c_length;
			this.code = code;
		}
	}
	
	private static HyHx geohex2hyhx(String hexcode) {
		Level l = geohex2level(hexcode);

		double unit_x = 6.0 * l.level * H_SIZE;
	    double unit_y = 2.8 * l.level * H_SIZE;
	    double h_k    = ( (double)Math.round( ( 1.4 / 3 ) * H_GRID ) ) / H_GRID;
	    double base_x = Math.floor( ( MIN_X_LON + MIN_X_LAT / h_k ) / unit_x );
	    double base_y = Math.floor( ( MIN_Y_LAT - h_k * MIN_Y_LON ) / unit_y );

	    double h_x, h_y;
	    if ( l.c_length > 5 ) {
	      h_x = H_KEY.indexOf(l.code[0]) * 3600 + H_KEY.indexOf(l.code[2]) * 60 + H_KEY.indexOf(l.code[4]);
	      h_y = H_KEY.indexOf(l.code[1]) * 3600 + H_KEY.indexOf(l.code[3]) * 60 + H_KEY.indexOf(l.code[5]);
	    } else { 
	      h_x = H_KEY.indexOf(l.code[0]) * 60   + H_KEY.indexOf(l.code[2]);
	      h_y = H_KEY.indexOf(l.code[1]) * 60   + H_KEY.indexOf(l.code[3]);
	    }
	    
	    return new HyHx(h_y, h_x, l.level, unit_x, unit_y, h_k, base_x, base_y);
	}
	
	private static Level geohex2level(String hexcode) {
	    char[] code     = hexcode.toCharArray();
	    int c_length = code.length;

	    int level = 0;
	    if ( c_length > 4 ) {
	    	level = H_KEY.indexOf(code[0]);
	    	if (level == -1)
	    		throw new IllegalArgumentException("Code format is something wrong");
	    	char[] code2 = new char[code.length-1];
	    	for (int i = 1; i < code.length; i++) {
	    		code2[i-1] = code[i];
	    	}
	    	code = code2;
	    	if ( level == 0 ) level = 60; 
	    } else { 
	        level = 7;
	    }
	    return new Level(level, c_length, code);
	}
}
