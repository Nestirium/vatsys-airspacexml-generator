package dev.nest.vatsystools.collections;

import dev.nest.vatsystools.Point;
import dev.nest.vatsystools.ProjectionTool;
import dev.nest.vatsystools.objects.Airport;
import dev.nest.vatsystools.objects.SID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SIDS extends ArrayList<SID> {

    private final List<SID> nonRelevantSids;

    public SIDS() {
        this.nonRelevantSids = new ArrayList<>();
    }

    public SIDS applyFilter(Airports airports, ArrayList<double[]> coordinates) {

        Iterator<SID> sidIterator = listIterator();
        while (sidIterator.hasNext()) {
            SID sid = sidIterator.next();

            Airport airport = airports.get(sid.airportName());

            Point p = airport.coordinates();

            double x = Double.parseDouble(p.longitude().asDecimalDegrees());
            double y = Double.parseDouble(p.latitude().asDecimalDegrees());
            double[] point = {y, x};
            if (!ProjectionTool.isInsidePolygon(point, coordinates)) {
                nonRelevantSids.add(sid);
                sidIterator.remove();
            }
        }

        return this;
    }

    public List<SID> nonRelevantSids() {

        return nonRelevantSids;

    }

}
