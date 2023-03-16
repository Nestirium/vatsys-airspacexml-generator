package dev.nest.vatsystools.objects;

import java.util.List;

public record SID(String identifier,
                  String airportName,
                  List<String> associatedRunways) {}
