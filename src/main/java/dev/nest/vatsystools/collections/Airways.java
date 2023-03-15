package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.objects.Airway;

import java.util.LinkedHashMap;

public class Airways extends LinkedHashMap<String, Airway> {


    /*
    public Airways applyFilter(ArrayList<double[]> coordinates) {
        Iterator<Map.Entry<String, Airway>> awyEntryIterator = entrySet().iterator();

        while (awyEntryIterator.hasNext()) {
            Map.Entry<String, Airway> awyEntry = awyEntryIterator.next();
            Airway awy = awyEntry.getValue();
            Point firstPoint = awy.waypoints().get(0).coordinates();
            Point lastPoint = awy.waypoints().get(awy.waypoints().size()-1).coordinates();

            Point.Longitude longitude1 = firstPoint.longitude();
            Point.Latitude latitude1 = firstPoint.latitude();

            Point.Longitude longitude2 = lastPoint.longitude();
            Point.Latitude latitude2 = lastPoint.latitude();

            double x1 = Double.parseDouble(longitude1.asDecimalDegrees());
            double y1 = Double.parseDouble(latitude1.asDecimalDegrees());
            double[] point1 = {y1, x1};

            double x2 = Double.parseDouble(longitude2.asDecimalDegrees());
            double y2 = Double.parseDouble(latitude2.asDecimalDegrees());
            double[] point2 = {y2, x2};

            boolean inside1 = StereographicProjection.isInsidePolygon(point1, coordinates);
            boolean inside2 = StereographicProjection.isInsidePolygon(point2, coordinates);

            if (!inside1 || !inside2) {
                awyEntryIterator.remove();
            }

        }
        return this;
    }

     */



}
