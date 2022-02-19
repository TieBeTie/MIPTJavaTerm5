package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static edu.phystech.Utils.execute;

public class SecondTask extends RequestResult {
    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        var task = new SecondTask();
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[0]);
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Город", "Кол-во отмененных рейсов"};
    }

    @Override
    protected void getData(RequestResult.SaveResult saveResult) throws SQLException, IOException {
        execute(this.getClass().getResourceAsStream("SecondTask.sql"),
                statement -> saveResult.saveResult(statement.executeQuery()));
    }
}
