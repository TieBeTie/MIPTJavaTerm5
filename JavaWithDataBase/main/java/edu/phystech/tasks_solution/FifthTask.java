package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static edu.phystech.Utils.execute;

public class FifthTask extends RequestResult {

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException();
        }
        var task = new FifthTask();
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[0]);
        task.buildPieCharts(args[1], args[2]);
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Направление", "День недели", "Кол-во рейсов"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        execute(this.getClass().getResourceAsStream("FifthTask.sql"),
                statement -> saveResult.saveResult(statement.executeQuery()));
    }

    private void buildPieCharts(String fromChartFileName, String toChartFileName) throws SQLException, IOException {
        List<String[]> toData = new ArrayList<>();
        List<String[]> fromData = new ArrayList<>();
        for (var line : getDataCached()) {
            if (line[0].equals("В Москву")) {
                toData.add(line);
            } else {
                fromData.add(line);
            }
        }
        ChartBuilder.buildPieChart(fromChartFileName, fromData, "Количество рейсов из Москвы", 1, 2);
        ChartBuilder.buildPieChart(toChartFileName, toData, "Количество рейсов в Москву", 1, 2);
    }
}
