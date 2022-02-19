package edu.phystech.init_db;


import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class CSVFile {
    private final String name;
    private final ColumnParser[] columnParser;

    @FunctionalInterface
    interface ParseColumnFunction {
        void parse(String str) throws SQLException;
    }

    @FunctionalInterface
    interface ColumnParser {
        ParseColumnFunction getParser(Integer index, PreparedStatement statement) throws SQLException;
    }

    public CSVFile(String name, ColumnParser... columnParser) {
        this.name = name;
        this.columnParser = columnParser;
    }

    public String getName() {
        return name;
    }

    public ColumnParser[] getColumnParser() {
        return columnParser;
    }

    static ColumnParser getStringParser() {
        return (Integer index, PreparedStatement statement) -> (String str) -> statement.setString(index, str);
    }

    static ColumnParser getIntegerParser() {
        return (Integer index, PreparedStatement statement) -> (String str) -> statement.setInt(index, Integer.parseInt(str));
    }

    static ColumnParser getCustomParser(String type) {
        return (Integer index, PreparedStatement statement) -> (String str) -> {
            if (str.isBlank()) {
                str = null;
            }
            var pgObject = new PGobject();
            pgObject.setType(type);
            pgObject.setValue(str);
            statement.setObject(index, pgObject);
        };
    }


}
