package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static edu.phystech.Utils.execute;

public class FirstTask extends RequestResult {

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        var task = new FirstTask();
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[0]);
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Город", "Список аэропортов"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        execute(this.getClass().getResourceAsStream("FirstTask.sql"),
                statement -> saveResult.saveResult(statement.executeQuery()));
    }
}
