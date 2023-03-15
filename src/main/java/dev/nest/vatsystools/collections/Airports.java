package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.Point;
import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Airport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMap used because there cannot be duplicate airport key names.
 */
public class Airports extends LinkedHashMap<String, Airport> {

/*
    public Airports applyFilter(ArrayList<double[]> coordinates) {
        Iterator<Map.Entry<String, Airport>> airportEntryIterator = entrySet().iterator();
        while (airportEntryIterator.hasNext()) {
            Map.Entry<String, Airport> airportEntry = airportEntryIterator.next();
            Airport airport = airportEntry.getValue();
            Point airportPoint = airport.coordinates();
            Point.Longitude longitude = airportPoint.longitude();
            Point.Latitude latitude = airportPoint.latitude();
            double x = Double.parseDouble(longitude.asDecimalDegrees());
            double y = Double.parseDouble(latitude.asDecimalDegrees());
            double[] point = {y, x};
            boolean inside = ProjectionTool.isInsidePolygon(point, coordinates);
            if (!inside) {
                airportEntryIterator.remove();
            }
        }
        return this;
    }

 */


}
