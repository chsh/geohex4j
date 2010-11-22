package net.geohex;

/*
 * GeoHex by @sa2da (http://geogames.net) is licensed under Creative Commons BY-SA 2.1 Japan License.
 * GeoHex V2 for Java implemented by @chshii is licensed under Creative Commons BY-SA 2.1 Japan License.
 */

public class GeoHex {
	public  static final String VERSION = "2.03";
	
	// *** Share with all instances ***
	public static final String h_key = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final double h_base = 20037508.34;
	public static final double h_deg = Math.PI*(30.0/180.0);
	public static final double h_k = Math.tan(h_deg);

	// *** Share with all instances ***
	// private static
	private static double calcHexSize(int level) {
		return h_base/Math.pow(2.0, level)/3.0;
	}

	// private class
	public static final class Zone {
		public double lat;
		public double lon;
		public double x; // ?
		public double y; // ?
		public String code;
		public int level;

		public Zone(double lat, double lon, double x, double y, String code) {
			this.lat = lat;
			this.lon = lon;
			this.x = x;
			this.y = y;
			this.code = code;
			this.level = getLevel();
		}
		public double getHexSize() {
			return calcHexSize(this.getLevel());
		}
		public Loc[] getHexCoords() {
			double h_lat = this.lat;
			double h_lon = this.lon;
			XY h_xy = loc2xy(h_lon, h_lat);
			double h_x = h_xy.x;
			double h_y = h_xy.y;
			double h_deg = Math.tan(Math.PI * (60.0 / 180.0));
			double h_size = this.getHexSize();
			double h_top = xy2loc(h_x, h_y + h_deg *  h_size).lat;
			double h_btm = xy2loc(h_x, h_y - h_deg *  h_size).lat;
	
			double h_l = xy2loc(h_x - 2 * h_size, h_y).lon;
			double h_r = xy2loc(h_x + 2 * h_size, h_y).lon;
			double h_cl = xy2loc(h_x - 1 * h_size, h_y).lon;
			double h_cr = xy2loc(h_x + 1 * h_size, h_y).lon;
			return new Loc[]{
				new Loc(h_lat, h_l),
				new Loc(h_top, h_cl),
				new Loc(h_top, h_cr),
				new Loc(h_lat, h_r),
				new Loc(h_btm, h_cr),
				new Loc(h_btm, h_cl)
			};
		}
		private int getLevel() {
			return h_key.indexOf(this.code.charAt(0));
		}
	}
	
	public static final String encode(double lat, double lon, int level) {
		return getZoneByLocation(lat, lon, level).code;
	}
	public static final Zone decode(String code) {
		return getZoneByCode(code);
	}

	// public static
	public static final Zone getZoneByLocation(double lat, double lon, int level) {
		if (lat < -90 || lat > 90) 
			throw new IllegalArgumentException("latitude must be between -90 and 90");
		if (lon < -180 || lon > 180)
			throw new IllegalArgumentException("longitude must be between -180 and 180");
		if (level < 0 || level > 24) 
			throw new IllegalArgumentException("level must be between 1 and 60");

		double h_size = calcHexSize(level);

		XY z_xy = loc2xy(lon, lat);
		double lon_grid = z_xy.x;
		double lat_grid = z_xy.y;
		double unit_x = 6 * h_size;
		double unit_y = 6 * h_size * h_k;
		double h_pos_x = (lon_grid + lat_grid / h_k) / unit_x;
		double h_pos_y = (lat_grid - h_k * lon_grid) / unit_y;
		long h_x_0 = (long)Math.floor(h_pos_x);
		long h_y_0 = (long)Math.floor(h_pos_y);
		double h_x_q = h_pos_x - h_x_0; //桁丸め修正
		double h_y_q = h_pos_y - h_y_0;
		long h_x = Math.round(h_pos_x);
		long h_y = Math.round(h_pos_y);

		long h_max=Math.round(h_base / unit_x + h_base / unit_y);

		if (h_y_q > -h_x_q + 1) {
			if((h_y_q < 2 * h_x_q) && (h_y_q > 0.5 * h_x_q)){
				h_x = h_x_0 + 1;
				h_y = h_y_0 + 1;
			}
		} else if (h_y_q < -h_x_q + 1) {
			if ((h_y_q > (2 * h_x_q) - 1) && (h_y_q < (0.5 * h_x_q) + 0.5)){
				h_x = h_x_0;
				h_y = h_y_0;
			}
		}

		double h_lat = (h_k * h_x * unit_x + h_y * unit_y) / 2;
		double h_lon = (h_lat - h_y * unit_y) / h_k;

		Loc z_loc = xy2loc(h_lon, h_lat);
		double z_loc_x = z_loc.lon;
		double z_loc_y = z_loc.lat;
		if(h_base - h_lon < h_size){
			z_loc_x = 180;
			long h_xy = h_x;
			h_x = h_y;
			h_y = h_xy;
		}

		long h_x_p =0;
		long h_y_p =0;
		if (h_x < 0) h_x_p = 1;
		if (h_y < 0) h_y_p = 1;
		long h_x_abs = Math.abs(h_x) * 2 + h_x_p;
		long h_y_abs = Math.abs(h_y) * 2 + h_y_p;
//		int h_x_100000 = (int)Math.floor(h_x_abs/777600000);
		int h_x_10000 = (int)Math.floor((h_x_abs%777600000)/12960000);
		int h_x_1000 = (int)Math.floor((h_x_abs%12960000)/216000);
		int h_x_100 = (int)Math.floor((h_x_abs%216000)/3600);
		int h_x_10 = (int)Math.floor((h_x_abs%3600)/60);
		int h_x_1 = (int)Math.floor((h_x_abs%3600)%60);
//		int h_y_100000 = (int)Math.floor(h_y_abs/777600000);
		int h_y_10000 = (int)Math.floor((h_y_abs%777600000)/12960000);
		int h_y_1000 = (int)Math.floor((h_y_abs%12960000)/216000);
		int h_y_100 = (int)Math.floor((h_y_abs%216000)/3600);
		int h_y_10 = (int)Math.floor((h_y_abs%3600)/60);
		int h_y_1 = (int)Math.floor((h_y_abs%3600)%60);

		StringBuffer sb = new StringBuffer();
		sb.append(h_key.charAt(level % 60));

//		if(h_max >=77600000/2) h_code += h_key.charAt(h_x_100000) + h_key.charAt(h_y_100000);
		if(h_max >=12960000/2) sb.append(h_key.charAt(h_x_10000)).append(h_key.charAt(h_y_10000));
		if(h_max >=216000/2) sb.append(h_key.charAt(h_x_1000)).append(h_key.charAt(h_y_1000));
		if(h_max >=3600/2) sb.append(h_key.charAt(h_x_100)).append(h_key.charAt(h_y_100));
		if(h_max >=60/2) sb.append(h_key.charAt(h_x_10)).append(h_key.charAt(h_y_10));
		sb.append(h_key.charAt(h_x_1)).append(h_key.charAt(h_y_1));

		String h_code = sb.toString();
		return new Zone(z_loc_y, z_loc_x, h_x, h_y, h_code);
	}
	
	public static final Zone getZoneByCode(String code) {
//		int c_length = code.length();
		int level = h_key.indexOf(code.charAt(0));
//		int scl = level;
		double h_size =  calcHexSize(level);
		double unit_x = 6 * h_size;
		double unit_y = 6 * h_size * h_k;
		long h_max = Math.round(h_base / unit_x + h_base / unit_y);
		long h_x = 0;
		long h_y = 0;

	/*	if (h_max >= 777600000 / 2) {
		h_x = h_key.indexOf(code.charAt(1)) * 777600000 + 
			  h_key.indexOf(code.charAt(3)) * 12960000 + 
			  h_key.indexOf(code.charAt(5)) * 216000 + 
			  h_key.indexOf(code.charAt(7)) * 3600 + 
			  h_key.indexOf(code.charAt(9)) * 60 + 
			  h_key.indexOf(code.charAt(11));
		h_y = h_key.indexOf(code.charAt(2)) * 777600000 + 
			  h_key.indexOf(code.charAt(4)) * 12960000 + 
			  h_key.indexOf(code.charAt(6)) * 216000 + 
			  h_key.indexOf(code.charAt(8)) * 3600 + 
			  h_key.indexOf(code.charAt(10)) * 60 + 
			  h_key.indexOf(code.charAt(12));
		} else
	*/
		if (h_max >= 12960000 / 2) {
			h_x = h_key.indexOf(code.charAt(1)) * 12960000 + 
				  h_key.indexOf(code.charAt(3)) * 216000 + 
				  h_key.indexOf(code.charAt(5)) * 3600 + 
				  h_key.indexOf(code.charAt(7)) * 60 + 
				  h_key.indexOf(code.charAt(9));
			h_y = h_key.indexOf(code.charAt(2)) * 12960000 + 
				  h_key.indexOf(code.charAt(4)) * 216000 + 
				  h_key.indexOf(code.charAt(6)) * 3600 + 
				  h_key.indexOf(code.charAt(8)) * 60 + 
				  h_key.indexOf(code.charAt(10));
		} else if (h_max >= 216000 / 2) {
			h_x = h_key.indexOf(code.charAt(1)) * 216000 + 
				  h_key.indexOf(code.charAt(3)) * 3600 + 
				  h_key.indexOf(code.charAt(5)) * 60 + 
				  h_key.indexOf(code.charAt(7));
			h_y = h_key.indexOf(code.charAt(2)) * 216000 + 
				  h_key.indexOf(code.charAt(4)) * 3600 + 
				  h_key.indexOf(code.charAt(6)) * 60 + 
				  h_key.indexOf(code.charAt(8));
		} else if (h_max >= 3600 / 2) {
			h_x = h_key.indexOf(code.charAt(1)) * 3600 + 
				  h_key.indexOf(code.charAt(3)) * 60 + 
				  h_key.indexOf(code.charAt(5));
			h_y = h_key.indexOf(code.charAt(2)) * 3600 + 
				  h_key.indexOf(code.charAt(4)) * 60 + 
				  h_key.indexOf(code.charAt(6));
		} else if (h_max >= 60 / 2) {
			h_x = h_key.indexOf(code.charAt(1)) * 60 + 
				  h_key.indexOf(code.charAt(3));
			h_y = h_key.indexOf(code.charAt(2)) * 60 + 
				  h_key.indexOf(code.charAt(4));
		}else{
			h_x = h_key.indexOf(code.charAt(1));
			h_y = h_key.indexOf(code.charAt(2));
		}

		h_x = ((h_x % 2) != 0) ? -(h_x - 1) / 2 : h_x / 2;
		h_y = ((h_y % 2) != 0) ? -(h_y - 1) / 2 : h_y / 2;
		double h_lat_y = (h_k * h_x * unit_x + h_y * unit_y) / 2;
		double h_lon_x = (h_lat_y - h_y * unit_y) / h_k;

		Loc h_loc = xy2loc(h_lon_x, h_lat_y);
		return new Zone(h_loc.lat, h_loc.lon, h_x, h_y, code);
	}
	
	public static final Zone getZoneByXY(double x, double y, int level) {
//		int scl = level;
		double h_size =  calcHexSize(level);
		double unit_x = 6.0 * h_size;
		double unit_y = 6.0 * h_size * h_k;
		long h_max = Math.round(h_base / unit_x + h_base / unit_y);
		double h_lat_y = (h_k * x * unit_x + y * unit_y) / 2.0;
		double h_lon_x = (h_lat_y - y * unit_y) / h_k;

		Loc h_loc = xy2loc(h_lon_x, h_lat_y);
		int x_p =0;
		int y_p =0;
		if (x < 0) x_p = 1;
		if (y < 0) y_p = 1;
		long x_abs = (long)(Math.abs(x) * 2 + x_p);
		long y_abs = (long)Math.abs(y) * 2 + y_p;
//		int x_100000 = (int)Math.floor(x_abs/777600000);
		int x_10000 = (int)Math.floor((x_abs%777600000)/12960000);
		int x_1000 = (int)Math.floor((x_abs%12960000)/216000);
		int x_100 = (int)Math.floor((x_abs%216000)/3600);
		int x_10 = (int)Math.floor((x_abs%3600)/60);
		int x_1 = (int)Math.floor((x_abs%3600)%60);
//		int y_100000 = (int)Math.floor(y_abs/777600000);
		int y_10000 = (int)Math.floor((y_abs%777600000)/12960000);
		int y_1000 = (int)Math.floor((y_abs%12960000)/216000);
		int y_100 = (int)Math.floor((y_abs%216000)/3600);
		int y_10 = (int)Math.floor((y_abs%3600)/60);
		int y_1 = (int)Math.floor((y_abs%3600)%60);

		StringBuffer sb = new StringBuffer();
		sb.append(h_key.charAt(level % 60));

//		if(h_max >=77600000/2) h_code += h_key.charAt(x_100000) + h_key.charAt(y_100000);
		if(h_max >=12960000/2) sb.append(h_key.charAt(x_10000)).append(h_key.charAt(y_10000));
		if(h_max >=216000/2) sb.append(h_key.charAt(x_1000)).append(h_key.charAt(y_1000));
		if(h_max >=3600/2) sb.append(h_key.charAt(x_100)).append(h_key.charAt(y_100));
		if(h_max >=60/2) sb.append(h_key.charAt(x_10)).append(h_key.charAt(y_10));
		sb.append(h_key.charAt(x_1)).append(h_key.charAt(y_1));

		String h_code = sb.toString();
		return new Zone(h_loc.lat, h_loc.lon, x, y, h_code);
	}

	public static final class XY {
		public double x, y;
		public XY(double x, double y) {
			this.x = x; this.y = y;
		}
	}
	public static final class Loc {
		public double lat, lon;
		public Loc(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}
	}

	// private static
	private static XY loc2xy(double lon, double lat) {
		double x = lon * h_base / 180.0;
		double y = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
		y *= h_base / 180.0;
		return new XY(x, y);
	}
	// private static
	private static Loc xy2loc(double x, double y) {
		double lon = (x / h_base) * 180.0;
		double lat = (y / h_base) * 180.0;
		lat = 180 / Math.PI * (2.0 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new Loc(lat, lon);
	}
}
