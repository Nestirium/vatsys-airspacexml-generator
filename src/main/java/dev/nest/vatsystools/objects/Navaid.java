package dev.nest.vatsystools.objects;

import dev.nest.vatsystools.Point;

public final class Navaid extends Waypoint {

    public enum NavAidType {

        VOR,
        NDB,
        TAC,
        None

    }

    private final NavAidType navAidType;
    private final String frequency;


    public Navaid(String identifier, Point coordinates, NavAidType navAidType, String frequency) {
        super(identifier, coordinates);
        this.navAidType = navAidType;
        this.frequency = frequency;
    }
    public NavAidType navAidType() {
        return navAidType;
    }

    public String frequency() {
        return String.format("%.3f", Double.parseDouble(frequency));
    }

}
