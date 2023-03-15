package dev.nest.vatsystools.objects;

import dev.nest.vatsystools.collections.SIDS;
import dev.nest.vatsystools.collections.STARS;
import dev.nest.vatsystools.Point;

import java.util.LinkedHashMap;

public record Airport(String identifier,
                      String elevation,
                      Point coordinates,
                      LinkedHashMap<String, Runway> runways,
                      SIDS sids, STARS stars) {
    public record Runway(String identifier, Point coordinates) {}

}
