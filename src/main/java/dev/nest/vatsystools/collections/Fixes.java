package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.Point;
import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Fix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Fixes extends LinkedHashMap<Point, Fix> {

/*
    public Fixes applyFilter(ArrayList<double[]> coordinates) {
        Iterator<Map.Entry<Point, Fix>> fixEntryIterator = entrySet().iterator();

        while (fixEntryIterator.hasNext()) {
            Map.Entry<Point, Fix> fixEntry = fixEntryIterator.next();
            Point fixPoint = fixEntry.getKey();

            Point.Longitude longitude = fixPoint.longitude();
            Point.Latitude latitude = fixPoint.latitude();

            double x = Double.parseDouble(longitude.asDecimalDegrees());
            double y = Double.parseDouble(latitude.asDecimalDegrees());
            double[] point = {y, x};

            boolean inside = ProjectionTool.isInsidePolygon(point, coordinates);

            if (!inside) {
                fixEntryIterator.remove();
            }

        }
        return this;
    }

 */


}
