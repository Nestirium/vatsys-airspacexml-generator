package dev.nest.vatsystools;

import ch.qos.logback.classic.Logger;
import dev.nest.vatsystools.collections.*;
import dev.nest.vatsystools.objects.*;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class AirspaceGenerator {

    private static AirspaceGenerator instance;
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory tf = TransformerFactory.newInstance();
    private static final Logger log = (Logger) LoggerFactory.getLogger(AirspaceGenerator.class);
    private final Transformer t;
    private final Document doc;
    private final Element airspaceElement;

    private AirspaceGenerator() {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            t = tf.newTransformer();
            doc = db.newDocument();
            airspaceElement = doc.createElement("Airspace");
            doc.appendChild(airspaceElement);
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public AirspaceGenerator generateAirports(Airports airports) {

        Element airportsElement = doc.createElement("Airports");

        airportsElement.appendChild(doc.createComment("Airports within specified filter area."));
        for (Map.Entry<String, Airport> airportEntry : airports.entrySet()) {

            String airportName = airportEntry.getKey();
            Airport airport = airportEntry.getValue();

            Element airportElement = doc.createElement("Airport");
            airportElement.setAttribute("Elevation", airport.elevation());
            airportElement.setAttribute("Name", airportName);

            Point airportPosition = airport.coordinates();

            airportElement.setAttribute("Position", airportPosition.latitude().asVatSys()+airportPosition.longitude().asVatSys());

            for (Map.Entry<String, Airport.Runway> runwayEntry : airport.runways().entrySet()) {

                String runwayName = runwayEntry.getKey();
                Airport.Runway runway = runwayEntry.getValue();

                Element runwayElement = doc.createElement("Runway");
                runwayElement.setAttribute("Name", runwayName);

                Point runwayPosition = runway.coordinates();

                runwayElement.setAttribute("Position", runwayPosition.latitude().asVatSys()+runwayPosition.longitude().asVatSys());

                airportElement.appendChild(runwayElement);
            }
            airportsElement.appendChild(airportElement);
        }

        airportsElement.appendChild(doc.createComment("Irrelevant remaining airports."));
        for (Map.Entry<String, Airport> airportEntry : airports.nonRelevantAirports().entrySet()) {

            String airportName = airportEntry.getKey();
            Airport airport = airportEntry.getValue();

            Element airportElement = doc.createElement("Airport");
            airportElement.setAttribute("Elevation", airport.elevation());
            airportElement.setAttribute("Name", airportName);

            Point airportPosition = airport.coordinates();

            airportElement.setAttribute("Position", airportPosition.latitude().asVatSys()+airportPosition.longitude().asVatSys());

            for (Map.Entry<String, Airport.Runway> runwayEntry : airport.runways().entrySet()) {

                String runwayName = runwayEntry.getKey();
                Airport.Runway runway = runwayEntry.getValue();

                Element runwayElement = doc.createElement("Runway");
                runwayElement.setAttribute("Name", runwayName);

                Point runwayPosition = runway.coordinates();

                runwayElement.setAttribute("Position", runwayPosition.latitude().asVatSys()+runwayPosition.longitude().asVatSys());

                airportElement.appendChild(runwayElement);
            }
            airportsElement.appendChild(airportElement);
        }

        airspaceElement.appendChild(airportsElement);
        log.info("Generated XML airports.");
        return this;
    }

    public AirspaceGenerator generateIntersections(Fixes fixes, Navaids navaids) {

        Element intersectionsElement = doc.createElement("Intersections");

        intersectionsElement.appendChild(doc.createComment("Fixes within specified filter area."));
        for (Fix fix : fixes.values()) {

            Element intersectionElement = doc.createElement("Point");
            intersectionElement.setAttribute("Name", fix.identifier());
            intersectionElement.setAttribute("Type", "Fix");

            Point point = fix.coordinates();

            intersectionElement.setTextContent(point.latitude().asVatSys()+point.longitude().asVatSys());

            intersectionsElement.appendChild(intersectionElement);
        }

        intersectionsElement.appendChild(doc.createComment("Irrelevant remaining fixes."));
        for (Fix fix : fixes.nonRelevantFixes().values()) {

            Element intersectionElement = doc.createElement("Point");
            intersectionElement.setAttribute("Name", fix.identifier());
            intersectionElement.setAttribute("Type", "Fix");

            Point point = fix.coordinates();

            intersectionElement.setTextContent(point.latitude().asVatSys()+point.longitude().asVatSys());

            intersectionsElement.appendChild(intersectionElement);
        }

        intersectionsElement.appendChild(doc.createComment("Navaids within specified filter area."));
        for (Navaid navaid : navaids.values()) {

            Element intersectionElement = doc.createElement("Point");

            intersectionElement.setAttribute("Name", navaid.identifier());
            intersectionElement.setAttribute("Type", "Navaid");
            intersectionElement.setAttribute("NavaidType", navaid.navAidType().name());
            intersectionElement.setAttribute("Frequency", navaid.frequency());

            Point point = navaid.coordinates();

            intersectionElement.setTextContent(point.latitude().asVatSys()+point.longitude().asVatSys());

            intersectionsElement.appendChild(intersectionElement);
        }

        intersectionsElement.appendChild(doc.createComment("Irrelevant remaining navaids."));
        for (Navaid navaid : navaids.nonRelevantNavaids().values()) {

            Element intersectionElement = doc.createElement("Point");

            intersectionElement.setAttribute("Name", navaid.identifier());
            intersectionElement.setAttribute("Type", "Navaid");
            intersectionElement.setAttribute("NavaidType", navaid.navAidType().name());
            intersectionElement.setAttribute("Frequency", navaid.frequency());

            Point point = navaid.coordinates();

            intersectionElement.setTextContent(point.latitude().asVatSys()+point.longitude().asVatSys());

            intersectionsElement.appendChild(intersectionElement);
        }

        airspaceElement.appendChild(intersectionsElement);
        log.info("Generated XML intersections.");
        return this;
    }




    public AirspaceGenerator generateAirways(Airways airways) {

        Element airwaysElement = doc.createElement("Airways");

        StringBuilder builder = new StringBuilder();

        airwaysElement.appendChild(doc.createComment("""
                Airways within specified filter area.\040
                WAYPOINTS TRUNCATED TO AREA OF RELEVANCE.
                If you see an airway with only a single waypoint, this means that the airway starts from that single point
                on your FIR border, and extends outside your FIR, but since the points outside your FIR are truncated, they are not included.
                I have left them in-case you manually want to extend your airways slightly outwards your FIR.
                The rest of the irrelevant airways are airways that do not touch your FIR by any DIRECT means.
                """));
        for (Airway airway : airways) {

            String airwayName = airway.identifier();

            Element airwayElement = doc.createElement("Airway");
            airwayElement.setAttribute("Name", airwayName);

            Iterator<Waypoint> waypointEntryIterator = airway.waypoints().iterator();

            while (waypointEntryIterator.hasNext()) {

                Waypoint waypoint = waypointEntryIterator.next();

                String waypointName = waypoint.identifier();

                if (waypoint instanceof Navaid navaid) {
                    if (waypointEntryIterator.hasNext()) {
                        builder.append(waypointName).append(" ").append(navaid.navAidType().name()).append("/");
                        continue;
                    }
                    builder.append(waypointName).append(" ").append(navaid.navAidType().name());
                    continue;
                }

                if (waypointEntryIterator.hasNext()) {
                    builder.append(waypointName).append("/");
                    continue;
                }
                builder.append(waypointName);
            }

            airwayElement.setTextContent(builder.toString());
            builder.setLength(0);
            builder.trimToSize();
            airwaysElement.appendChild(airwayElement);
        }


        airwaysElement.appendChild(doc.createComment("Irrelevant remaining airways."));
        for (Airway airway : airways.nonRelevantAirways()) {

            String airwayName = airway.identifier();

            Element airwayElement = doc.createElement("Airway");
            airwayElement.setAttribute("Name", airwayName);

            Iterator<Waypoint> waypointEntryIterator = airway.waypoints().iterator();

            while (waypointEntryIterator.hasNext()) {

                Waypoint waypoint = waypointEntryIterator.next();

                String waypointName = waypoint.identifier();

                if (waypoint instanceof Navaid navaid) {
                    if (waypointEntryIterator.hasNext()) {
                        builder.append(waypointName).append(" ").append(navaid.navAidType().name()).append("/");
                        continue;
                    }
                    builder.append(waypointName).append(" ").append(navaid.navAidType().name());
                    continue;
                }

                if (waypointEntryIterator.hasNext()) {
                    builder.append(waypointName).append("/");
                    continue;
                }
                builder.append(waypointName);
            }
            airwayElement.setTextContent(builder.toString());
            builder.setLength(0);
            builder.trimToSize();
            airwaysElement.appendChild(airwayElement);
        }

        airspaceElement.appendChild(airwaysElement);
        log.info("Generated XML airways.");
        return this;
    }

    public AirspaceGenerator generateSystemRunways(Airports airports) {

        Element systemRunwaysElement = doc.createElement("SystemRunways");
        for (Airport airport : airports.values()) {
            Element airportElement = doc.createElement("Airport");
            airportElement.setAttribute("Name", airport.identifier());
            for (Airport.Runway runway : airport.runways().values()) {
                Element runwayElement = doc.createElement("Runway");
                runwayElement.setAttribute("Name", runway.identifier());
                runwayElement.setAttribute("DataRunway", runway.identifier());
                for (SID sid : airport.sids()) {
                    if (sid.associatedRunways().contains(runway.identifier())) {
                        Element sidElement = doc.createElement("SID");
                        sidElement.setAttribute("Name", sid.identifier());
                        runwayElement.appendChild(sidElement);
                    }
                }
                for (STAR star : airport.stars()) {
                    if (star.associatedRunways().contains(runway.identifier())) {
                        Element starElement = doc.createElement("STAR");
                        starElement.setAttribute("Name", star.identifier());
                        runwayElement.appendChild(starElement);
                    }
                }
                airportElement.appendChild(runwayElement);
            }
            systemRunwaysElement.appendChild(airportElement);
        }
        airspaceElement.appendChild(systemRunwaysElement);
        return this;
    }

    public AirspaceGenerator generateSIDSTARS(SIDS sids, STARS stars) {

        Element sidStarsElement = doc.createElement("SIDSTARs");

        StringBuilder runwaysString = new StringBuilder();

        for (SID sid : sids) {

            Element sidElement = doc.createElement("SID");
            sidElement.setAttribute("Name", sid.identifier());
            sidElement.setAttribute("Airport", sid.airportName());

            Iterator<String> runwaysIterator = sid.associatedRunways().listIterator();
            while (runwaysIterator.hasNext()) {
                String rwy = runwaysIterator.next();
                if (runwaysIterator.hasNext()) {
                    runwaysString.append(rwy).append(",");
                } else {
                    runwaysString.append(rwy);
                }
            }
            sidElement.setAttribute("Runways", runwaysString.toString());
            runwaysString.setLength(0);
            runwaysString.trimToSize();

            //add more

            sidStarsElement.appendChild(sidElement);

        }



        for (STAR star : stars) {

            Element starElement = doc.createElement("STAR");
            starElement.setAttribute("Name", star.identifier());
            starElement.setAttribute("Airport", star.airportName());

            Iterator<String> runwaysIterator = star.associatedRunways().listIterator();
            while (runwaysIterator.hasNext()) {
                String rwy = runwaysIterator.next();
                if (runwaysIterator.hasNext()) {
                    runwaysString.append(rwy).append(",");
                } else {
                    runwaysString.append(rwy);
                }
            }
            starElement.setAttribute("Runways", runwaysString.toString());
            runwaysString.setLength(0);
            runwaysString.trimToSize();

            //add more

            sidStarsElement.appendChild(starElement);

        }



        for (SID sid : sids.nonRelevantSids()) {

            Element sidElement = doc.createElement("SID");
            sidElement.setAttribute("Name", sid.identifier());
            sidElement.setAttribute("Airport", sid.airportName());

            Iterator<String> runwaysIterator = sid.associatedRunways().listIterator();
            while (runwaysIterator.hasNext()) {
                String rwy = runwaysIterator.next();
                if (runwaysIterator.hasNext()) {
                    runwaysString.append(rwy).append(",");
                } else {
                    runwaysString.append(rwy);
                }
            }
            sidElement.setAttribute("Runways", runwaysString.toString());
            runwaysString.setLength(0);
            runwaysString.trimToSize();

            //add more

            sidStarsElement.appendChild(sidElement);

        }

        for (STAR star : stars.nonRelevantStars()) {

            Element starElement = doc.createElement("STAR");
            starElement.setAttribute("Name", star.identifier());
            starElement.setAttribute("Airport", star.airportName());

            Iterator<String> runwaysIterator = star.associatedRunways().listIterator();
            while (runwaysIterator.hasNext()) {
                String rwy = runwaysIterator.next();
                if (runwaysIterator.hasNext()) {
                    runwaysString.append(rwy).append(",");
                } else {
                    runwaysString.append(rwy);
                }
            }
            starElement.setAttribute("Runways", runwaysString.toString());
            runwaysString.setLength(0);
            runwaysString.trimToSize();

            //add more

            sidStarsElement.appendChild(starElement);

        }

        airspaceElement.appendChild(sidStarsElement);
        log.info("Generated SIDSTARs XML.");
        return this;

    }


    public void generateXMLFile(String fileName, String outputDirectory) {
        try {
            File file = new File(outputDirectory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(fos);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(domSource, streamResult);
            fos.close();
            log.info("Generated XML Airspace.");
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static synchronized AirspaceGenerator instance() {
        if (instance == null) {
            instance = new AirspaceGenerator();
        }
        return instance;
    }

}
