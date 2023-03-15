package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.Point;
import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Airway;
import dev.nest.vatsystools.objects.Waypoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Airways extends ArrayList<Airway> {

    private final List<Airway> nonRelevantAirways;

    public Airways() {
        this.nonRelevantAirways = new ArrayList<>();
    }

    public Airways applyFilter(ArrayList<double[]> coordinates) {

        Iterator<Airway> awyIterator = listIterator();
        while (awyIterator.hasNext()) {
            boolean inside = false;
            Airway awy = awyIterator.next();

            for (Waypoint wpt : awy.waypoints()) {

                Point p = wpt.coordinates();

                double x = Double.parseDouble(p.longitude().asDecimalDegrees());
                double y = Double.parseDouble(p.latitude().asDecimalDegrees());
                double[] point = {y, x};

                if (ProjectionTool.isInsidePolygon(point, coordinates)) {
                    inside = true;
                    break;
                }

            }

            if (!inside) {
                nonRelevantAirways.add(awy);
                awyIterator.remove();
            }

        }

        for (Airway awy : this) {

            Iterator<Waypoint> waypointIterator = awy.waypoints().listIterator();
            while (waypointIterator.hasNext()) {

                Waypoint wpt = waypointIterator.next();

                Point p = wpt.coordinates();

                double x = Double.parseDouble(p.longitude().asDecimalDegrees());
                double y = Double.parseDouble(p.latitude().asDecimalDegrees());
                double[] point = {y, x};

                boolean inside = ProjectionTool.isInsidePolygon(point, coordinates);

                if (!inside) {
                    waypointIterator.remove();
                }

            }

        }

        return this;
    }

    public List<Airway> nonRelevantAirways() {

        return nonRelevantAirways;

    }


}
