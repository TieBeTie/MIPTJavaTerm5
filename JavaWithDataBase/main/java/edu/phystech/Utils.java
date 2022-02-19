package edu.phystech;

import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class Utils {
    static private PooledConnection pooledConnection;
    static private boolean hasInit = false;

    public static Connection getConnection() throws SQLException {
        if (!hasInit) {
            hasInit = true;
            var pgDataSource = new PGConnectionPoolDataSource();
            pgDataSource.setDatabaseName("javahw3");
            pgDataSource.setPortNumbers(new int[]{5432});
            pgDataSource.setServerNames(new String[]{"docker"});
            pgDataSource.setUser("javahw3_user");
            pgDataSource.setPassword("javahw3_password");
            pgDataSource.setDefaultAutoCommit(false);
            System.out.printf("Connecting to %s%n", pgDataSource.getUrl());
            pooledConnection = pgDataSource.getPooledConnection();
            System.out.println("Connected");
        }
        return pooledConnection.getConnection();
    }

    @FunctionalInterface
    public interface ExecutePreparedStatement {
        void execute(PreparedStatement preparedStatement) throws SQLException;
    }

    public static String readFile(InputStream sqlQueryFile) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(sqlQueryFile, StandardCharsets.UTF_8))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void execute(InputStream sqlQueryFile, ExecutePreparedStatement executePreparedStatement) throws SQLException, IOException {
        String sqlQuery = readFile(sqlQueryFile);
        try (var connection = getConnection(); var statement = connection.prepareStatement(sqlQuery)) {
            executePreparedStatement.execute(statement);
            connection.commit();
        }
    }
}
