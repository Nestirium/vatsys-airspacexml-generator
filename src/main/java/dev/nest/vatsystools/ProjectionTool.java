package dev.nest.vatsystools;

import java.util.ArrayList;

public class ProjectionTool {

    // Stereographic projection formula
    public static double[] toStereographic(double lon, double lat) {
        double x = (2 * Math.tan(lon/2)) / (1 + Math.sin(lat));
        double y = (2 * Math.tan(lat/2)) / (1 + Math.sin(lat));
        return new double[]{x, y};
    }

    // Ray Casting algorithm to check if a point is inside a polygon
    public static boolean isInsidePolygon(double[] point, ArrayList<double[]> polygon) {
        int n = polygon.size();
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double[] pi = polygon.get(i);
            double[] pj = polygon.get(j);
            if (pi[1] == pj[1] && pi[1] == point[1] && point[0] >= Math.min(pi[0], pj[0]) && point[0] <= Math.max(pi[0], pj[0])) {
                // Point is on a horizontal polygon edge
                return true;
            }
            if (((pi[1] > point[1]) != (pj[1] > point[1])) &&
                    (point[0] <= (pj[0] - pi[0]) * (point[1] - pi[1]) / (pj[1] - pi[1]) + pi[0])) {
                inside = !inside;
            }
        }
        return inside;
    }

}