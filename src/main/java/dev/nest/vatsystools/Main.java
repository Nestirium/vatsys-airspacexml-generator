package dev.nest.vatsystools;


import ch.qos.logback.classic.Logger;
import dev.nest.vatsystools.collections.*;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Main {

    private static final Logger log = (Logger) LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        log.info("Welcome to VatSys Airspace Generator made by Ahmad Ayoub (1610046).");
        log.info("Please enter the full path to the NavData directory you want to convert.");

        String pathToNavData = input.nextLine();

        NavDataParser navDataParser = new NavDataParser(Paths.get(pathToNavData).toFile());

        Airports airports = navDataParser.parseAirports();
        Fixes fixes = navDataParser.parseInts();
        Navaids navaids = navDataParser.parseNavs();
        Airways airways = navDataParser.parseAirways(fixes, navaids);
        SIDS sids = navDataParser.parseSIDS(airports);
        STARS stars = navDataParser.parseSTARS(airports);

        log.info("""
                
                Would you like to filter the navigation data to a specific area of the map ?
                The converter will sort the navigation points that are within these boundaries to come first in the Airspace.xml file.
                This way VatSys will parse them first, and the correct points will display in your Maps.
                The remaining duplicates will no longer be an issue.
                
                Reply with (true/false)..."""
        );


        boolean filter = input.nextBoolean();
        input.nextLine();

        if (filter) {

            log.info("Enter the text file path containing the polygon boundary coordinates of the filter area.");

            log.info("""
                
                Expected format: Standard Decimal Degrees.
                
                Latitude, Longitude
                Latitude, Longitude
                Latitude, Longitude
                ...
                """
            );

            String pathToBounds = input.nextLine();

            File boundsFile = Paths.get(pathToBounds).toFile();

            BufferedReader boundsFileReader = new BufferedReader(new FileReader(boundsFile));
            Iterator<String> iterator = boundsFileReader.lines().iterator();

            ArrayList<double[]> coordinates = new ArrayList<>();

            while (iterator.hasNext()) {
                String line = iterator.next().trim();
                String[] parts = line.split(",");
                double[] coords = new double[2];
                coords[0] = Double.parseDouble(parts[0]);
                coords[1] = Double.parseDouble(parts[1]);
                coordinates.add(coords);
            }

            boundsFileReader.close();

            sids = sids.applyFilter(airports, coordinates);
            stars = stars.applyFilter(airports, coordinates);
            airports = airports.applyFilter(coordinates);
            fixes = fixes.applyFilter(coordinates);
            navaids = navaids.applyFilter(coordinates);
            airways = airways.applyFilter(coordinates);

        }

        AirspaceGenerator airspaceGenerator = AirspaceGenerator.instance()
                .generateAirports(airports)
                .generateIntersections(fixes, navaids)
                .generateSystemRunways(airports)
                .generateAirways(airways)
                .generateSIDSTARS(sids, stars);

        //todo - generate SIDSTARS
        //C:\Users\ahman\Desktop\bounds.txt

        airspaceGenerator.generateXMLFile("Airspace.xml",
                System.getProperty("user.dir"));

    }

}
