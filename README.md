# vatsys-airspacexml-generator
A work in progress tool for converting VatSys NavData DFD to Airspace.xml file.

Java 16 or above is required to be installed on your machine for this tool to run.

This tool interprets & converts the navigraph DFD export plain text files located in \vatSys Files\NavData into an Airspace.xml file that VatSys can read.
You no longer need to worry about your controllers having a navigraph subscribtion to access the navdata, nor you need to worry about manually having to update 
the Airspace.xml file during AIRAC changes.

The tool generates a 25-30 MB Airspace.xml file containing the converted worldwide navigation data. However, the worldwide navdata contains lots of duplicates, such as
fixes, waypoints, navaids, airways.
This will cause a problem when you try to draw Maps by referring to point names defined in the Airspace.xml file, as VatSys will attempt to parse the first occurance of
the duplicate in the file, and this may lead to the incorrect waypoint being rendered on the Map.

Fortunately I have found a workaround for this.
A user is able to provide a "filter boundary" via a text file containing the border coordinates of the filter area in standard latlong decimal degrees format 
as the following example:

[bounds.txt](https://github.com/Nestirium/vatsys-airspacexml-generator/files/10983140/bounds.txt)

The tool reads this file. Then, all the navigation entities that have coordinates within the specified boundary are sorted to be on top of the entities that are outside
the boundary in the Airspace.xml file. 
This way, in case of duplicates, vatsys will always be rendering the correct one specified in the Map, since it is the first occurance in the Airspace.xml, at the same time
data that is irrelevant to your airspace is preserved. 

Please note that the filtering algorithm may be inaccurate in some cases, specially with waypoints falling exactly on the border or slightly deviated in numbers. 
I have fixed this by adding some buffer to the filter in the code, but I'm not sure of the reliability of this.
If you found it unreliable (e.g missing waypoints), try adding some buffer to your FIR border before inserting the data into the tool.

Finally, the <SIDSTARS> section is not yet supported because I couldn't properly interpret the sids.txt and stars.txt for the <Route> elements properly.
So for now, this is left to be done manually.
