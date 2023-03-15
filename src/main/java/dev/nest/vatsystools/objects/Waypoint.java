package dev.nest.vatsystools.objects;

import dev.nest.vatsystools.Point;

public abstract class Waypoint {

    private final String identifier;
    private final Point coordinates;

    public Waypoint(String identifier, Point coordinates) {
        this.identifier = identifier;
        this.coordinates = coordinates;
    }

    public String identifier() {
        return identifier;
    }

    public Point coordinates() {
        return coordinates;
    }

}
