package dev.nest.vatsystools.objects;



import java.util.LinkedList;

public final class Airway {

    private final String identifier;
    private final LinkedList<Waypoint> waypoints;

    public Airway(String identifier) {
        this.identifier = identifier;
        this.waypoints = new LinkedList<>();
    }


    public String identifier() {
        return identifier;
    }

    public LinkedList<Waypoint> waypoints() {
        return waypoints;
    }


}
