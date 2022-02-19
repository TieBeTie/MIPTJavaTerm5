package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static edu.phystech.Utils.execute;

public class SeventhTask extends RequestResult {

    private final String cancelDateFrom;
    private final String cancelDateTo;

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 4) {
            throw new IllegalArgumentException();
        }
        var task = new SeventhTask(args[0], args[1]);
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[2]);
        task.buildHistogram(args[3]);
    }

    public SeventhTask(String cancelDateFrom, String cancelDateTo) {
        this.cancelDateFrom = cancelDateFrom;
        this.cancelDateTo = cancelDateTo;
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Дата", "Потери"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        execute(this.getClass().getResourceAsStream("SeventhTask.sql"),
                statement -> {
                    statement.setString(1, cancelDateFrom);
                    statement.setString(2, cancelDateTo);
                    saveResult.saveResult(statement.executeQuery());
                });
    }

    private void buildHistogram(String chartFileName) throws SQLException, IOException {
        ChartBuilder.buildHistogram(chartFileName, getDataCached(), "Убытки по дням", "убытки");
    }
}