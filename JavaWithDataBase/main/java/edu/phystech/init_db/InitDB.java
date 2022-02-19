package edu.phystech.init_db;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

class InitDB implements AutoCloseable {
    private final Connection connection;
    public InitDB(@NotNull Connection connection) {
        this.connection = connection;
    }

    public void createTables(String fileWithSqlName) throws IOException, SQLException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream(fileWithSqlName), StandardCharsets.UTF_8));
             var statement = connection.createStatement()) {
            statement.execute(in.lines().collect(Collectors.joining("\n")));
            connection.commit();
        }
    }

    public void addCSVToDataBase(@NotNull String csvUrl, String tableName, CSVFile.ColumnParser[] columnParsersGetters)
            throws IOException, CsvValidationException, SQLException {
        try (var in = new CSVReader(new InputStreamReader(new URL(csvUrl).openStream()))) {
            String[] line;
            int numberOfColumn = columnParsersGetters.length;
            var sqlQuery = String.format("INSERT INTO %s VALUES (%s?);", tableName, "?,".repeat(Math.max(0, numberOfColumn - 1)));
            try (var statement = connection.prepareStatement(sqlQuery)) {
                CSVFile.ParseColumnFunction[] parsers = new CSVFile.ParseColumnFunction[numberOfColumn];

                for (int i = 0; i < numberOfColumn; ++i) {
                    parsers[i] = columnParsersGetters[i].getParser(i + 1, statement);
                }

                while ((line = in.readNext()) != null) {
                    for (int i = 0; i < numberOfColumn; ++i) {
                        parsers[i].parse(line[i]);
                    }
                    statement.execute();
                }
            }
            connection.commit();
        }
        System.out.println("Table \"" + tableName + "\" filled");
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
