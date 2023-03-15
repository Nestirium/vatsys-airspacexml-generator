package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.Point;
import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Airport;
import dev.nest.vatsystools.objects.STAR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class STARS extends ArrayList<STAR> {

    private final List<STAR> nonRelevantStars;

    public STARS() {
        this.nonRelevantStars = new ArrayList<>();
    }

    public STARS applyFilter(Airports airports, ArrayList<double[]> coordinates) {

        Iterator<STAR> starIterator = listIterator();
        while (starIterator.hasNext()) {
            STAR star = starIterator.next();

            Airport airport = airports.get(star.airportName());

            Point p = airport.coordinates();

            double x = Double.parseDouble(p.longitude().asDecimalDegrees());
            double y = Double.parseDouble(p.latitude().asDecimalDegrees());
            double[] point = {y, x};
            if (!ProjectionTool.isInsidePolygon(point, coordinates)) {
                nonRelevantStars.add(star);
                starIterator.remove();
            }
        }

        return this;
    }

    public List<STAR> nonRelevantStars() {

        return nonRelevantStars;

    }

}
