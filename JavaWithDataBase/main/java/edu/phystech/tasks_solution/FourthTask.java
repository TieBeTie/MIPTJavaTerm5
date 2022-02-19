package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static edu.phystech.Utils.execute;

public class FourthTask extends RequestResult {

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        var task = new FourthTask();
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[0]);
        task.buildPieChart(args[1]);
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Месяц", "Кол-во отмен"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        execute(this.getClass().getResourceAsStream("FourthTask.sql"),
                statement -> saveResult.saveResult(statement.executeQuery()));
    }

    private void buildPieChart(String chartFileName) throws SQLException, IOException {
        ChartBuilder.buildPieChart(chartFileName, getDataCached(), "Количество отмен рейсов по месяцам", 0, 1);
    }
}
