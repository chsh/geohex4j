/*
 * GeoHex by @sa2da (http://geogames.net) is licensed under Creative Commons BY-SA 2.1 Japan License.
 * GeoHex V3 for Java implemented by @chshii is licensed under Creative Commons BY-SA 2.1 Japan License.
 */

package org.geohex.geohex4j;

import net.teralytics.geohex.*;

public class GeoHex {

    public static final double h_deg = Math.PI * (30.0 / 180.0);
    public static final double h_k = Math.tan(h_deg);

    public static Cell getCellByLocation(double lat, double lon, int level) {
        if (lat < -90 || lat > 90)
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        if (lon < -180 || lon > 180)
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        if (level < 0 || level > 15)
            throw new IllegalArgumentException("level must be between 0 and 15");

        XY z_xy = package$.MODULE$.loc2xy(lon, lat);

        XY unit = package$.MODULE$.unitSize(level);
        double h_pos_x = (z_xy.x() + z_xy.y() / h_k) / unit.x();
        double h_pos_y = (z_xy.y() - h_k * z_xy.x()) / unit.y();
        long h_x_0 = (long) Math.floor(h_pos_x);
        long h_y_0 = (long) Math.floor(h_pos_y);
        double h_x_q = h_pos_x - h_x_0;
        double h_y_q = h_pos_y - h_y_0;
        long h_x = Math.round(h_pos_x);
        long h_y = Math.round(h_pos_y);

        if (h_y_q > -h_x_q + 1) {
            if ((h_y_q < 2 * h_x_q) && (h_y_q > 0.5 * h_x_q)) {
                h_x = h_x_0 + 1;
                h_y = h_y_0 + 1;
            }
        } else if (h_y_q < -h_x_q + 1) {
            if ((h_y_q > (2 * h_x_q) - 1) && (h_y_q < (0.5 * h_x_q) + 0.5)) {
                h_x = h_x_0;
                h_y = h_y_0;
            }
        }

        return new Cell(h_x, h_y);
    }

}
