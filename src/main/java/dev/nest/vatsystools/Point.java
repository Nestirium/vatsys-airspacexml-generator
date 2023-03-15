package dev.nest.vatsystools;


import java.text.DecimalFormat;

public record Point(Latitude latitude, Longitude longitude) {

    @Override
    public boolean equals(Object anotherPoint) {
        if (anotherPoint instanceof Point point) {
            return latitude.asProvided.equals(point.latitude.asProvided) && longitude.asProvided.equals(point.longitude.asProvided);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (latitude == null ? 0 : latitude.hashCode());
        result = 31 * result + (longitude == null ? 0 : longitude.hashCode());
        return result;
    }

    public enum PointType {
        Euroscope,
        VatSys,
        DecimalDegrees

    }

    public record Latitude(String asProvided, PointType pointType) {

        public String asVatSys() {

            switch (pointType) {
                case Euroscope -> {
                    StringBuilder string = new StringBuilder(asProvided);
                    char cardinal = string.charAt(0);
                    string.deleteCharAt(0);
                    String[] numbers = string.toString().split("\\.");
                    int degree = Integer.parseInt(numbers[0]);
                    double minutes = Integer.parseInt(numbers[1]);
                    double seconds = Double.parseDouble(String.format("%s.%s", numbers[2], numbers[3]));
                    double conversionResult = degree + minutes / 60 + seconds / 3600;
                    String formatDecimal = String.format("%.6f", conversionResult);
                    String vatSysFormat;
                    if (cardinal == 'N') {
                        vatSysFormat = String.format("+%02d.%s", Integer.parseInt(formatDecimal.split("\\.")[0]), formatDecimal.split("\\.")[1]);
                    } else {
                        vatSysFormat = String.format("-%02d.%s", Integer.parseInt(formatDecimal.split("\\.")[0]), formatDecimal.split("\\.")[1]);
                    }
                    return vatSysFormat;
                }
                case DecimalDegrees -> {

                    double latitude = Double.parseDouble(asProvided);
                    DecimalFormat decimalFormat = new DecimalFormat("00.000000");

                    return (latitude < 0 ? "-" : "+") + decimalFormat.format(Math.abs(latitude));

                }
                default -> {
                    return asProvided;
                }
            }
        }

        public String asDecimalDegrees() {
                if (pointType == PointType.Euroscope) {
                        StringBuilder string = new StringBuilder(asProvided);
                        char cardinal = string.charAt(0);
                        string.deleteCharAt(0);
                        String[] numbers = string.toString().split("\\.");
                        int degree = Integer.parseInt(numbers[0]);
                        double minutes = Integer.parseInt(numbers[1]);
                        double seconds = Double.parseDouble(String.format("%s.%s", numbers[2], numbers[3]));
                        double conversionResult = degree + minutes / 60 + seconds / 3600;
                        return (cardinal == 'S') ? String.format("-%.6f", conversionResult) : String.format("%.6f", conversionResult);
                }
                return asProvided;
        }
    }

    public record Longitude(String asProvided, PointType pointType) {

        public String asVatSys() {

            switch (pointType) {

                case Euroscope -> {
                    StringBuilder string = new StringBuilder(asProvided);
                    char cardinal = string.charAt(0);
                    string.deleteCharAt(0);
                    String[] numbers = string.toString().split("\\.");
                    int degree = Integer.parseInt(numbers[0]);
                    double minutes = Integer.parseInt(numbers[1]);
                    double seconds = Double.parseDouble(String.format("%s.%s", numbers[2], numbers[3]));
                    double conversionResult = degree + minutes / 60 + seconds / 3600;
                    String formatDecimal = String.format("%.6f", conversionResult);
                    String vatSysFormat;
                    if (cardinal == 'E') {
                        vatSysFormat = String.format("+%03d.%s", Integer.parseInt(formatDecimal.split("\\.")[0]), formatDecimal.split("\\.")[1]);
                    } else {
                        vatSysFormat = String.format("-%03d.%s", Integer.parseInt(formatDecimal.split("\\.")[0]), formatDecimal.split("\\.")[1]);
                    }
                    return vatSysFormat;
                }
                case DecimalDegrees -> {

                    double longitude = Double.parseDouble(asProvided);
                    DecimalFormat decimalFormat = new DecimalFormat("000.000000");

                    return (longitude < 0 ? "-" : "+") + decimalFormat.format(Math.abs(longitude));

                }
                default -> {
                    return asProvided;
                }

            }
        }

        public String asDecimalDegrees() {
                if (pointType == PointType.Euroscope) {
                        StringBuilder string = new StringBuilder(asProvided);
                        char cardinal = string.charAt(0);
                        string.deleteCharAt(0);
                        String[] numbers = string.toString().split("\\.");
                        int degree = Integer.parseInt(numbers[0]);
                        double minutes = Integer.parseInt(numbers[1]);
                        double seconds = Double.parseDouble(String.format("%s.%s", numbers[2], numbers[3]));
                        double conversionResult = degree + minutes / 60 + seconds / 3600;
                        return (cardinal == 'W') ? String.format("-%.6f", conversionResult) : String.format("%.6f", conversionResult);
                }
                return asProvided;

        }

    }


}
