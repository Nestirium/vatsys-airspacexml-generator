package dev.nest.vatsystools.generators;

import ch.qos.logback.classic.Logger;
import dev.nest.vatsystools.collections.Airports;
import dev.nest.vatsystools.collections.Airways;
import dev.nest.vatsystools.collections.Fixes;
import dev.nest.vatsystools.collections.Navaids;
import dev.nest.vatsystools.objects.*;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class MapsGenerator {


    private static MapsGenerator instance;

    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory tf = TransformerFactory.newInstance();
    private final DocumentBuilder db;
    private final Transformer t;
    private static final Logger log = (Logger) LoggerFactory.getLogger(MapsGenerator.class);
    private final File airportsDir;
    private final File airwayFixesDir;
    private final File airwaysDir;
    private final File navaidsDir;

    private MapsGenerator() {
        try {
            t = tf.newTransformer();
            db = dbf.newDocumentBuilder();
            File mapsDirectory = Files.createDirectories(Paths.get(System.getProperty("user.dir") + "\\Maps")).toFile();
            airportsDir = Files.createDirectories(Paths.get(mapsDirectory.getPath() + "\\AIRPORTS")).toFile();
            airwaysDir = Files.createDirectories(Paths.get(mapsDirectory.getPath() + "\\AIRWAYS")).toFile();
            airwayFixesDir = Files.createDirectories(Paths.get(mapsDirectory.getPath() + "\\FIXES")).toFile();
            navaidsDir = Files.createDirectories(Paths.get(mapsDirectory.getPath() + "\\NAVAIDS")).toFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void generateAirports(Airports airports, String fileName) {

        Document doc = db.newDocument();
        Element mapsElement = doc.createElement("Maps");
        Element symbolMapEl = doc.createElement("Map");
        symbolMapEl.setAttribute("Type", "System");
        symbolMapEl.setAttribute("Name", "SYMBOLS");
        symbolMapEl.setAttribute("Priority", "0");
        Element symbolElement = doc.createElement("Symbol");
        symbolElement.setAttribute("Type", "HollowSquare");
        for (Airport airport : airports.values()) {
            Element pointElement = doc.createElement("Point");
            pointElement.setTextContent(airport.identifier());
            symbolElement.appendChild(pointElement);
        }
        symbolMapEl.appendChild(symbolElement);
        mapsElement.appendChild(symbolMapEl);

        Element labelMapEl = doc.createElement("Map");
        labelMapEl.setAttribute("Type", "System");
        labelMapEl.setAttribute("Name", "LABELS");
        labelMapEl.setAttribute("Priority", "0");
        Element labelElement = doc.createElement("Label");
        labelElement.setAttribute("hasLeader", "true");
        for (Airport airport : airports.values()) {
            Element pointElement = doc.createElement("Point");
            pointElement.setAttribute("Name", airport.identifier());
            pointElement.setTextContent(airport.identifier());
            labelElement.appendChild(pointElement);
        }
        labelMapEl.appendChild(labelElement);
        mapsElement.appendChild(labelMapEl);
        doc.appendChild(mapsElement);

        try {
            File file = new File(this.airportsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(fos);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(domSource, streamResult);
            fos.close();
            log.info("Generated Map for Airports.");
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void generateNavaids(Navaids navaids, String fileName) {
        Document doc = db.newDocument();
        Element mapsElement = doc.createElement("Maps");
        Element symbolMapEl = doc.createElement("Map");
        symbolMapEl.setAttribute("Type", "System");
        symbolMapEl.setAttribute("Name", "VOR_SYMBOLS");
        symbolMapEl.setAttribute("Priority", "0");
        Element symbolElement = doc.createElement("Symbol");
        symbolElement.setAttribute("Type", "Hexagon");
        for (Navaid navaid : navaids.values()) {
            if (navaid.navAidType() != Navaid.NavAidType.VOR) {
                continue;
            }
            Element pointElement = doc.createElement("Point");
            pointElement.setTextContent(navaid.identifier());
            symbolElement.appendChild(pointElement);
        }
        symbolMapEl.appendChild(symbolElement);
        mapsElement.appendChild(symbolMapEl);

        Element labelMapElVOR = doc.createElement("Map");
        labelMapElVOR.setAttribute("Type", "System");
        labelMapElVOR.setAttribute("Name", "VOR_LABELS");
        labelMapElVOR.setAttribute("Priority", "0");
        Element labelElement = doc.createElement("Label");
        labelElement.setAttribute("hasLeader", "true");
        for (Navaid navaid : navaids.values()) {
            if (navaid.navAidType() != Navaid.NavAidType.VOR) {
                continue;
            }
            Element pointElement = doc.createElement("Point");
            pointElement.setAttribute("Name", navaid.identifier());
            pointElement.setTextContent(navaid.identifier());
            labelElement.appendChild(pointElement);
        }
        labelMapElVOR.appendChild(labelElement);
        mapsElement.appendChild(labelMapElVOR);


        Element symbolMapElNDB = doc.createElement("Map");
        symbolMapElNDB.setAttribute("Type", "System");
        symbolMapElNDB.setAttribute("Name", "NDB_SYMBOLS");
        symbolMapElNDB.setAttribute("Priority", "0");
        Element symbolElementNDB = doc.createElement("Symbol");
        symbolElementNDB.setAttribute("Type", "DotFillCircle");
        for (Navaid navaid : navaids.values()) {
            if (navaid.navAidType() != Navaid.NavAidType.NDB) {
                continue;
            }
            Element pointElement = doc.createElement("Point");
            pointElement.setTextContent(navaid.identifier());
            symbolElementNDB.appendChild(pointElement);
        }
        symbolMapElNDB.appendChild(symbolElementNDB);
        mapsElement.appendChild(symbolMapElNDB);


        Element labelMapElNDB = doc.createElement("Map");
        labelMapElNDB.setAttribute("Type", "System");
        labelMapElNDB.setAttribute("Name", "NDB_LABELS");
        labelMapElNDB.setAttribute("Priority", "0");
        Element labelElementNDB = doc.createElement("Label");
        labelElementNDB.setAttribute("hasLeader", "true");
        for (Navaid navaid : navaids.values()) {
            if (navaid.navAidType() != Navaid.NavAidType.NDB) {
                continue;
            }
            Element pointElement = doc.createElement("Point");
            pointElement.setAttribute("Name", navaid.identifier());
            pointElement.setTextContent(navaid.identifier());
            labelElementNDB.appendChild(pointElement);
        }
        labelMapElNDB.appendChild(labelElementNDB);
        mapsElement.appendChild(labelMapElNDB);
        doc.appendChild(mapsElement);

        try {
            File file = new File(this.navaidsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(fos);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(domSource, streamResult);
            fos.close();
            log.info("Generated Map for Navaids.");
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void generateAirways(Airways airways, String fileName) {
        Document doc = db.newDocument();
        Element mapsElement = doc.createElement("Maps");
        Element mapElement = doc.createElement("Map");
        mapElement.setAttribute("Type", "System");
        mapElement.setAttribute("Name", "ALL");
        mapElement.setAttribute("Priority", "1");
        StringBuilder airwayFormatter = new StringBuilder();
        for (Airway airway : airways) {
            int i = airway.waypoints().size();
            if (i < 2) {
                continue;
            }
            Element lineElement = doc.createElement("Line");
            lineElement.setAttribute("Name", airway.identifier());
            Waypoint waypoint1 = airway.waypoints().get(0);
            Waypoint waypoint2 = airway.waypoints().get(i-1);
            if (waypoint1 instanceof Navaid navaid) {
                airwayFormatter.append(waypoint1.identifier()).append(" ").append(navaid.navAidType().name());
            } else {
                airwayFormatter.append(waypoint1.identifier());
            }
            airwayFormatter.append(".").append(airway.identifier()).append(".");
            if (waypoint2 instanceof Navaid navaid) {
                airwayFormatter.append(waypoint2.identifier()).append(" ").append(navaid.navAidType().name());
            } else {
                airwayFormatter.append(waypoint2.identifier());
            }
            lineElement.setTextContent(airwayFormatter.toString());
            airwayFormatter.setLength(0);
            airwayFormatter.trimToSize();
            mapElement.appendChild(lineElement);
        }
        mapsElement.appendChild(mapElement);
        doc.appendChild(mapsElement);

        try {
            File file = new File(this.airwaysDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(fos);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(domSource, streamResult);
            fos.close();
            log.info("Generated Map for airways.");
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void generateAirwayFixes(Airways airways, String fileName) {
        Document doc = db.newDocument();
        Element mapsElement = doc.createElement("Maps");
        Element symbolMapEl = doc.createElement("Map");
        symbolMapEl.setAttribute("Type", "System");
        symbolMapEl.setAttribute("Name", "SYMBOLS");
        symbolMapEl.setAttribute("Priority", "0");
        Element symbolElement = doc.createElement("Symbol");
        symbolElement.setAttribute("Type", "HollowTriangle");
        List<String> drawnFixesList = new ArrayList<>();
        for (Airway airway : airways) {
            for (Waypoint waypoint : airway.waypoints()) {
                if (drawnFixesList.contains(waypoint.identifier())) {
                    continue;
                }
                if (waypoint instanceof Navaid) {
                    continue;
                }
                drawnFixesList.add(waypoint.identifier());
                Element pointElement = doc.createElement("Point");
                pointElement.setTextContent(waypoint.identifier());
                symbolElement.appendChild(pointElement);
            }
        }
        symbolMapEl.appendChild(symbolElement);
        mapsElement.appendChild(symbolMapEl);
        drawnFixesList.clear();

        Element labelMapEl = doc.createElement("Map");
        labelMapEl.setAttribute("Type", "System");
        labelMapEl.setAttribute("Name", "LABELS");
        labelMapEl.setAttribute("Priority", "0");
        Element labelElement = doc.createElement("Label");
        labelElement.setAttribute("hasLeader", "true");
        for (Airway airway : airways) {
            for (Waypoint waypoint : airway.waypoints()) {
                if (drawnFixesList.contains(waypoint.identifier())) {
                    continue;
                }
                if (waypoint instanceof Navaid) {
                    continue;
                }
                drawnFixesList.add(waypoint.identifier());
                Element pointElement = doc.createElement("Point");
                pointElement.setAttribute("Name", waypoint.identifier());
                pointElement.setTextContent(waypoint.identifier());
                labelElement.appendChild(pointElement);
            }
        }
        labelMapEl.appendChild(labelElement);
        mapsElement.appendChild(labelMapEl);
        doc.appendChild(mapsElement);
        drawnFixesList.clear();
        try {
            File file = new File(this.airwayFixesDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(fos);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(domSource, streamResult);
            fos.close();
            log.info("Generated Map for fixes.");
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }


    public static synchronized MapsGenerator instance() {
        if (instance == null) {
            instance = new MapsGenerator();
        }
        return instance;
    }


}
