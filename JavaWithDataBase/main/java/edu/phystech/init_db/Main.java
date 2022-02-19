package edu.phystech.init_db;

import edu.phystech.Utils;

public class Main {
    static private final CSVFile[] tables = new CSVFile[]{
            new CSVFile("aircrafts", CSVFile.getStringParser(),
                    CSVFile.getCustomParser("json"), CSVFile.getIntegerParser()),
            new CSVFile("airports", CSVFile.getStringParser(), CSVFile.getCustomParser("json"),
                    CSVFile.getCustomParser("json"), CSVFile.getCustomParser("point"), CSVFile.getStringParser()),
            new CSVFile("boarding_passes", CSVFile.getStringParser(), CSVFile.getIntegerParser(),
                    CSVFile.getIntegerParser(), CSVFile.getStringParser()),
            new CSVFile("bookings", CSVFile.getStringParser(), CSVFile.getCustomParser("timestamptz"),
                    CSVFile.getCustomParser("numeric")),
            new CSVFile("flights", CSVFile.getIntegerParser(), CSVFile.getStringParser(), CSVFile.getCustomParser(
                    "timestamptz"), CSVFile.getCustomParser("timestamptz"), CSVFile.getStringParser(),
                    CSVFile.getStringParser(), CSVFile.getStringParser(), CSVFile.getStringParser(),
                    CSVFile.getCustomParser("timestamptz"), CSVFile.getCustomParser("timestamptz")),
            new CSVFile("tickets", CSVFile.getStringParser(), CSVFile.getStringParser(), CSVFile.getStringParser(),
                    CSVFile.getStringParser(), CSVFile.getCustomParser("json")),
            new CSVFile("ticket_flights", CSVFile.getStringParser(), CSVFile.getIntegerParser(),
                    CSVFile.getStringParser(), CSVFile.getCustomParser("numeric")),
            new CSVFile("seats", CSVFile.getStringParser(), CSVFile.getStringParser(), CSVFile.getStringParser())
    };

    static private final String url = "https://storage.yandexcloud.net/airtrans-small/%s.csv";

    public static void main(String[] args) throws Exception {
        try (var db = new InitDB(Utils.getConnection())) {
            db.createTables("dbcreate.sql");
            System.out.println("Tables created");
            for (var table : tables) {
                db.addCSVToDataBase(String.format(url, table.getName()), table.getName(), table.getColumnParser());
            }
        }
    }
}
