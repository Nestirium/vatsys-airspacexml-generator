package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Navaid;
import dev.nest.vatsystools.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Point as key because it is impossible for a navigation point coordinate to duplicate.
 */
public class Navaids extends LinkedHashMap<Point, Navaid> {


    private final LinkedHashMap<Point, Navaid> nonRelevantNavaids;

    public Navaids() {

        this.nonRelevantNavaids = new LinkedHashMap<>();

    }

    public Navaids applyFilter(ArrayList<double[]> coordinates) {
        Iterator<Map.Entry<Point, Navaid>> navaidEntryIterator = entrySet().iterator();

        while (navaidEntryIterator.hasNext()) {
            Map.Entry<Point, Navaid> navaidEntry = navaidEntryIterator.next();
            Point fixPoint = navaidEntry.getKey();

            Point.Longitude longitude = fixPoint.longitude();
            Point.Latitude latitude = fixPoint.latitude();

            double x = Double.parseDouble(longitude.asDecimalDegrees());
            double y = Double.parseDouble(latitude.asDecimalDegrees());
            double[] point = {y, x};

            boolean inside = ProjectionTool.isInsidePolygon(point, coordinates);

            if (!inside) {
                nonRelevantNavaids.put(fixPoint, navaidEntry.getValue());
                navaidEntryIterator.remove();
            }

        }
        return this;
    }

    public LinkedHashMap<Point, Navaid> nonRelevantNavaids() {

        return nonRelevantNavaids;

    }


}
