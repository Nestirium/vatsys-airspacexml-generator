package dev.nest.vatsystools;


import ch.qos.logback.classic.Logger;
import dev.nest.vatsystools.collections.*;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static final Logger log = (Logger) LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        log.info("Welcome to VatSys Airspace Generator made by Ahmad Ayoub (1610046).");
        log.info("Please enter the full path to the NavData directory you want to convert.");

        String path = "C:\\Users\\ahman\\Documents\\vatSys Files\\NavData";

        NavDataParser navDataParser = new NavDataParser(Paths.get(path).toFile());

        Airports airports = navDataParser.parseAirports();
        Fixes fixes = navDataParser.parseInts();
        Navaids navaids = navDataParser.parseNavs();
        Airways airways = navDataParser.parseAirways(fixes, navaids);
        SIDS sids = navDataParser.parseSIDS(airports);
        STARS stars = navDataParser.parseSTARS(airports);

        AirspaceGenerator airspaceGenerator = AirspaceGenerator.instance()
                .generateAirports(airports)
                .generateIntersections(fixes, navaids)
                .generateSystemRunways(airports)
                .generateAirways(airways);

        //todo - generate SIDSTARS

        airspaceGenerator.generateXMLFile("Airspace.xml", "C:\\Users\\ahman\\Desktop");

    }

}
