package dev.nest.vatsystools;


import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;


import java.util.ArrayList;


public class ProjectionTool {


    // Ray Casting algorithm to check if a point is inside a polygon
    /*
    Old algorithm with no buffer, and points on border are considered inside.
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

     */

    /*
    Inaccurate, buffer distance not working.
    public static boolean isInsidePolygon(double[] point, ArrayList<double[]> polygon) {
        // Define the buffer distance in degrees
        double bufferDistance = 2.0 / 69.0;

        // Create a new polygon with the buffer
        ArrayList<double[]> bufferedPolygon = new ArrayList<>();
        for (double[] p : polygon) {
            double[] q = {p[0], p[1]};
            double dx = q[0] - point[0];
            double dy = q[1] - point[1];
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d < bufferDistance) {
                // Point is within buffer distance of the polygon boundary
                return false;
            } else if (d <= bufferDistance * 2.0) {
                // Point is within buffer distance * 2 of the polygon boundary
                double s = bufferDistance / d;
                q[0] = point[0] + s * dx;
                q[1] = point[1] + s * dy;
            }
            bufferedPolygon.add(q);
        }

        // Check if the point is inside the new polygon
        int n = bufferedPolygon.size();
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double[] pi = bufferedPolygon.get(i);
            double[] pj = bufferedPolygon.get(j);
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

     */




/*
working
    public static boolean isInsidePolygon(double[] point, ArrayList<double[]> polygon) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[polygon.size()];
        for (int i = 0; i < polygon.size(); i++) {
            double[] p = polygon.get(i);
            coords[i] = new Coordinate(p[0], p[1]);
        }
        LinearRing shell = gf.createLinearRing(coords);
        Polygon poly = gf.createPolygon(shell, null);
        Coordinate pointCoord = new Coordinate(point[0], point[1]);
        Point pt = gf.createPoint(pointCoord);
        return poly.contains(pt);
    }

 */


    public static boolean isInsidePolygon(double[] point, ArrayList<double[]> polygon) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[polygon.size()];
        for (int i = 0; i < polygon.size(); i++) {
            double[] p = polygon.get(i);
            coords[i] = new Coordinate(p[0], p[1]);
        }
        LinearRing shell = gf.createLinearRing(coords);
        Polygon poly = gf.createPolygon(shell, null);
        Coordinate pointCoord = new Coordinate(point[0], point[1]);
        Point pt = gf.createPoint(pointCoord);

        // Check if the point is within the buffer distance of the polygon
        return pt.isWithinDistance(poly, 0.001);
    }








}