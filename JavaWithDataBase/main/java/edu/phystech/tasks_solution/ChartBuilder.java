package edu.phystech.tasks_solution;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ChartBuilder {
    private static final int CHART_WIDTH = 600;
    private static final int CHART_HEIGHT = 400;

    public static void buildPieChart(String chartFileName, List<String[]> data, String title, int indexInfo,
                                     int indexValue) throws IOException {
        var dataForChart = new DefaultPieDataset();
        for (var line : data) {
            dataForChart.setValue(line[indexInfo], Integer.parseInt(line[indexValue]));
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataForChart);

        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{1} ({2}) - {0}");
        ((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);
        saveChartTo(chart, chartFileName);
    }

    public static void buildHistogram(String chartFileName, List<String[]> data, String title, String valueAxisLabel) throws IOException {
        var dataForChart = new DefaultCategoryDataset();
        for (var line : data) {
            dataForChart.addValue(Double.parseDouble(line[1]), "", line[0]);
        }

        JFreeChart chart = ChartFactory.createBarChart(title, null, valueAxisLabel, dataForChart,
                PlotOrientation.VERTICAL, false, true, false);

        CategoryAxis axis = chart.getCategoryPlot().getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        saveChartTo(chart, chartFileName);
    }

    private static void saveChartTo(JFreeChart chart, String chartFileName) throws IOException {
        var chartFilePath = Path.of(chartFileName);
        if (!Files.exists(chartFilePath)) {
            Files.createDirectories(chartFilePath.getParent());
            Files.createFile(chartFilePath);
        }
        try (var out = Files.newOutputStream(chartFilePath)) {
            org.jfree.chart.ChartUtils.writeChartAsPNG(out, chart, CHART_WIDTH, CHART_HEIGHT);
        }
    }
}
