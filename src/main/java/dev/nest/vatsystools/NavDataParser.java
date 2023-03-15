package dev.nest.vatsystools;

import ch.qos.logback.classic.Logger;
import dev.nest.vatsystools.collections.*;
import dev.nest.vatsystools.objects.*;

import dev.nest.vatsystools.Point.PointType;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class NavDataParser {

    private final File navDataDirectory;
    private static final Logger log = (Logger) LoggerFactory.getLogger(NavDataParser.class);

    public NavDataParser(File navDataDirectory) {
        this.navDataDirectory = navDataDirectory;
    }

    public Airports parseAirports() {
        File aptsFile = Paths.get(navDataDirectory.getPath() + "\\apts.txt").toFile();
        Airports airports = new Airports();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(aptsFile));
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                String identifier = line.substring(0, 4);
                String elevation = line.substring(45, 51).stripLeading();
                String coordLatY = line.substring(52, 63).stripTrailing();
                String coordLongX = line.substring(65, 76).stripTrailing();
                String runwayMetaData = line.substring(79).stripTrailing();
                String[] runwayMetaDataArr = runwayMetaData.split(" ");
                Point.Latitude latitude = new Point.Latitude(coordLatY, PointType.DecimalDegrees);
                Point.Longitude longitude = new Point.Longitude(coordLongX, PointType.DecimalDegrees);
                Point point = new Point(latitude, longitude);
                Airport airport = new Airport(identifier, elevation, point, new LinkedHashMap<>(), new SIDS(), new STARS());
                for (String rwy : runwayMetaDataArr) {
                    String[] metaSections = rwy.split("_");
                    String rwyIdentifier = metaSections[0];
                    String rwyCoordLatY = metaSections[2];
                    String rwyCoordLongX = metaSections[3];
                    Point.Latitude rwyLatitude = new Point.Latitude(rwyCoordLatY, PointType.DecimalDegrees);
                    Point.Longitude rwyLongitude = new Point.Longitude(rwyCoordLongX, PointType.DecimalDegrees);
                    Point rwyPoint = new Point(rwyLatitude, rwyLongitude);
                    airport.runways().put(rwyIdentifier, new Airport.Runway(rwyIdentifier, rwyPoint));
                }
                airports.put(identifier, airport);
            }
            reader.close();
            log.info("Parsed airports.");
            return airports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public Navaids parseNavs() {
        Navaids navaids = new Navaids();
        File navsFile = Paths.get(navDataDirectory.getPath() + "\\navs.txt").toFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(navsFile));
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                String identifier = line.substring(0, 3).trim();
                String coordLatY = line.substring(50, 61).trim();
                String coordLongX = line.substring(63, 74).trim();
                String navType = line.substring(77, 80).trim();
                String frequency = line.substring(81, 88).trim();
                Point.Latitude latitude = new Point.Latitude(coordLatY, PointType.DecimalDegrees);
                Point.Longitude longitude = new Point.Longitude(coordLongX, PointType.DecimalDegrees);
                Point point = new Point(latitude, longitude);
                Navaid navaid = new Navaid(identifier, point, Navaid.NavAidType.valueOf(navType), frequency);
                navaids.put(point, navaid);
            }
            reader.close();
            log.info("Parsed navaids.");
            return navaids;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Fixes parseInts() {
        Fixes fixes = new Fixes();
        File navsFile = Paths.get(navDataDirectory.getPath() + "\\ints.txt").toFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(navsFile));
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                String identifier = line.substring(0, 5).stripTrailing();
                String coordLatY = line.substring(50, 61).stripTrailing();
                String coordLongX = line.substring(63, 74).stripTrailing();
                Point.Latitude latitude = new Point.Latitude(coordLatY, PointType.DecimalDegrees);
                Point.Longitude longitude = new Point.Longitude(coordLongX, PointType.DecimalDegrees);
                Point point = new Point(latitude, longitude);
                Fix fix = new Fix(identifier, point);
                fixes.put(point, fix);
            }
            reader.close();
            log.info("Parsed fixes.");
            return fixes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Airways parseAirways(Fixes fixes, Navaids navaids) {
        File awysFile = Paths.get(navDataDirectory.getPath() + "\\awys.txt").toFile();
        Airways airways = new Airways();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(awysFile));
            Iterator<String> iterator = reader.lines().iterator();
            int index = -1;
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                String identifier = line.substring(0, 6).trim();
                String waypointNumber = line.substring(7, 12).trim();
                String waypointName = line.substring(13, 18).trim();
                String coordLatY = line.substring(33, 44).trim();
                String coordLongX = line.substring(47, 58).trim();
                Point.Latitude latitude = new Point.Latitude(coordLatY, PointType.DecimalDegrees);
                Point.Longitude longitude = new Point.Longitude(coordLongX, PointType.DecimalDegrees);
                Point point = new Point(latitude, longitude);

                Airway navAirway;
                if (!waypointNumber.equals("0001")) {
                    navAirway = airways.get(index);
                } else {
                    index++;
                    navAirway = new Airway(identifier);
                    airways.add(navAirway);
                }

                if (fixes.containsKey(point)) {
                    Waypoint waypoint = fixes.get(point);
                    navAirway.waypoints().add(waypoint);
                }

                if (navaids.containsKey(point)) {
                    Waypoint waypoint = navaids.get(point);
                    navAirway.waypoints().add(waypoint);
                }
            }
            reader.close();
            log.info("Parsed airways.");
            return airways;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SIDS parseSIDS(Airports airports) {
        File sidsFile = Paths.get(navDataDirectory.getPath() + "\\sids.txt").toFile();
        SIDS sids = new SIDS();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sidsFile));
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    String airportName = line.replace("[", "").replace("]", "").trim();
                    while (iterator.hasNext()) {
                        String line2 = iterator.next();
                        if (line2.startsWith("T ")) {
                            String sidName = line2.substring(5, 14).trim();
                            String runways = line2.substring(31).trim();
                            SID sid = new SID(sidName, airportName, new ArrayList<>());
                            if (runways.contains(",")) {
                                String[] runwayArr = runways.split(",");
                                for (String runway : runwayArr) {
                                    sid.associatedRunways().add(runway);
                                }
                            } else {
                                sid.associatedRunways().add(runways);
                            }
                            sids.add(sid);
                            Airport airport = airports.get(airportName);
                            airport.sids().add(sid);
                        } else {
                            break;
                        }
                    }
                }
            }
            log.info("Parsed sids.");
            reader.close();
            return sids;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public STARS parseSTARS(Airports airports) {
        File starsFile = Paths.get(navDataDirectory.getPath() + "\\stars.txt").toFile();
        STARS stars = new STARS();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(starsFile));
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith(";")) {
                    continue;
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    String airportName = line.replace("[", "").replace("]", "").trim();
                    while (iterator.hasNext()) {
                        String line2 = iterator.next();
                        if (line2.startsWith("T ")) {
                            String starName = line2.substring(5, 14).trim();
                            String runways = line2.substring(31).trim();
                            STAR star = new STAR(starName, airportName, new ArrayList<>());
                            if (runways.contains(",")) {
                                String[] runwayArr = runways.split(",");
                                for (String runway : runwayArr) {
                                    star.associatedRunways().add(runway);
                                }
                            } else {
                                star.associatedRunways().add(runways);
                            }
                            stars.add(star);
                            Airport airport = airports.get(airportName);
                            airport.stars().add(star);
                        } else {
                            break;
                        }
                    }
                }
            }
            log.info("Parsed stars.");
            reader.close();
            return stars;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

