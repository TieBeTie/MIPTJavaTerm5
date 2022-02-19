package edu.phystech;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.lang.Math.max;

/**
 * Provides toConsoleTable and toExcelTable methods that call getHeaders and getData only once
 */
public abstract class RequestResult {
    private String[] headers;
    private List<String[]> data;

    /**
     * @return headers name to tables
     */
    @NotNull
    protected abstract String[] getHeaders();

    @FunctionalInterface
    protected interface SaveResult {
        void saveResult(@NotNull ResultSet resultSet) throws SQLException;
    }

    /**
     * @param saveResult might be call of ResultSet that you will receive
     */
    protected abstract void getData(SaveResult saveResult) throws SQLException, IOException;

    private void SetHeadersIfNotAlready() {
        if (headers == null) {
            headers = getHeaders();
        }
    }

    @NotNull
    public final List<String[]> getDataCached() throws SQLException, IOException {
        SetDataIfNotAlready();
        return data;
    }

    private void SetDataIfNotAlready() throws SQLException, IOException {
        if (data == null) {
            getData((@NotNull ResultSet resultSet) -> {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int numberOfColumns = resultSetMetaData.getColumnCount();
                data = new ArrayList<>();

                while (resultSet.next()) {
                    var row = new String[numberOfColumns];
                    for (int column = 0; column < numberOfColumns; ++column) {
                        row[column] = resultSet.getString(column + 1);
                    }
                    data.add(row);
                }
            });
            if (data == null) {
                throw new IllegalCallerException();
            }
        }
    }

    /**
     * @return string of table for console in pretty view
     */
    @NotNull
    public final String toConsoleTable() throws SQLException, IOException {
        SetHeadersIfNotAlready();
        SetDataIfNotAlready();

        int columnNumber = headers.length;
        int[] maxStringSizeInColumns = getMaxStringSizeInColumns(columnNumber, headers, data);

        var delimiter = getDelimiter(maxStringSizeInColumns);

        var stringBuilder = new StringBuilder();
        addLineToStringBuilder(stringBuilder, headers, maxStringSizeInColumns);

        for (var line : data) {
            stringBuilder.append(delimiter);
            addLineToStringBuilder(stringBuilder, line, maxStringSizeInColumns);
        }

        stringBuilder.append(delimiter);

        return stringBuilder.toString();
    }

    private String getDelimiter(int[] maxStringSizeInColumns) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append('|');
        for (int i = 0; i < maxStringSizeInColumns.length; ++i) {
            stringBuilder.append("-".repeat(maxStringSizeInColumns[i]));
            if (i != maxStringSizeInColumns.length - 1) {
                stringBuilder.append('+');
            }
        }
        stringBuilder.append("|\n");
        return stringBuilder.toString();
    }

    private int[] getMaxStringSizeInColumns(int columnNumber, String[] headers, List<String[]> data) {
        var maxStringSizeInColumns = new int[columnNumber];

        for (int i = 0; i < columnNumber; ++i) {
            maxStringSizeInColumns[i] = headers[i].length();
        }

        for (var line : data) {
            for (int i = 0; i < columnNumber; ++i) {
                if (line[i] != null) {
                    maxStringSizeInColumns[i] = max(maxStringSizeInColumns[i], line[i].length());
                }
            }
        }
        return maxStringSizeInColumns;
    }

    private void addLineToStringBuilder(StringBuilder stringBuilder, String[] line, int[] maxStringSizeInColumns) {
        for (int i = 0; i < line.length; ++i) {
            stringBuilder.append('|');
            appendToStringBuilderWithSpaces(stringBuilder, line[i], maxStringSizeInColumns[i]);
        }
        stringBuilder.append("|\n");
    }

    private void appendToStringBuilderWithSpaces(StringBuilder stringBuilder, String word, int maxSize) {
        if (word != null) {
            stringBuilder.append(word);
            stringBuilder.append(" ".repeat(maxSize - word.length()));
        } else {
            stringBuilder.append(" ".repeat(maxSize));
        }
    }

    public final void toExcelTable(String reportFileName) throws SQLException, IOException {
        SetHeadersIfNotAlready();
        SetDataIfNotAlready();

        var reportWorkBook = new XSSFWorkbook();
        var reportSheet = reportWorkBook.createSheet();

        int rowNumber = 0;
        var headerRow = reportSheet.createRow(rowNumber++);
        fillHeader(headerRow);

        var dataRowStyle = createDataCellStyle(reportWorkBook);

        for (var rowData : data) {
            var row = reportSheet.createRow(rowNumber++);
            row.setRowStyle(dataRowStyle);
            fillRow(row, rowData);
        }
        for (int i = 0; i < headers.length; ++i) {
            reportSheet.autoSizeColumn(i);
        }
        reportSheet.createFreezePane(0, 1);

        var reportFilePath = Path.of(reportFileName);
        if (!Files.exists(reportFilePath)) {
            Files.createDirectories(reportFilePath.getParent());
            Files.createFile(reportFilePath);
        }
        reportWorkBook.write(Files.newOutputStream(reportFilePath));
    }

    private XSSFCellStyle createDataCellStyle(XSSFWorkbook workbook) {
        var style = workbook.createCellStyle();
        var font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
    }

    private void fillHeader(XSSFRow row) {
        var rowStyle = row.getSheet().getWorkbook().createCellStyle();
        var font = row.getSheet().getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        rowStyle.setFont(font);
        row.setRowStyle(rowStyle);
        fillRow(row, headers);
    }

    private void fillRow(XSSFRow row, String[] data) {
        int cellNumber = 0;
        for (var cellValue : data) {
            var cell = row.createCell(cellNumber++);
            cell.setCellValue(cellValue);
        }
    }
}
