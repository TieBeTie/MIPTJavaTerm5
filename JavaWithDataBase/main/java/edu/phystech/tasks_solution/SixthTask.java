package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static edu.phystech.Utils.getConnection;
import static edu.phystech.Utils.readFile;

public class SixthTask extends RequestResult {

    private final String aircraftModel;

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        var task = new SixthTask(args[0]);
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[1]);
    }

    public SixthTask(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Удаленные полеты"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        try (var connection = getConnection()) {
            for (var fileName : new String[]{"SixthTask_1.sql", "SixthTask_2.sql"}) {
                try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(fileName)))) {
                    statement.setString(1, aircraftModel);
                    statement.execute();
                }
            }
            try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(
                    "SixthTask_3.sql")))) {
                statement.setString(1, aircraftModel);
                saveResult.saveResult(statement.executeQuery());
            }
            connection.commit();
        }
    }
}
