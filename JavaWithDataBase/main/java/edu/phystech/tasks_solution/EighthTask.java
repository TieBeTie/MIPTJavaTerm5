package edu.phystech.tasks_solution;

import edu.phystech.RequestResult;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static edu.phystech.Utils.*;
import static edu.phystech.Utils.readFile;

public class EighthTask extends RequestResult {

    private final String ticketNumber;
    private final String bookRef;
    private final String passengerId;
    private final String passengerName;
    private final String contactData;
    private final int flight;
    private final String fareCondition;
    private final Float amount;
    private final String seat;

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 10) {
            throw new IllegalArgumentException();
        }
        var task = new EighthTask(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        System.out.println(task.toConsoleTable());
        task.toExcelTable(args[9]);
    }

    public EighthTask(String ticketNumber, String bookRef, String passengerId, String passengerName, String contactData,
                      String flight, String fareCondition, String amount, String seat) {
        this.ticketNumber = ticketNumber;
        this.bookRef = bookRef;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.contactData = contactData;
        this.flight = Integer.parseInt(flight);
        this.fareCondition = fareCondition;
        this.amount = Float.parseFloat(amount);
        this.seat = seat;
    }

    @Override
    protected @NotNull String[] getHeaders() {
        return new String[]{"Результат добавления"};
    }

    @Override
    protected void getData(SaveResult saveResult) throws SQLException, IOException {
        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(
                    "EighthTask_checkFlight.sql")))) {
                statement.setInt(1, flight);
                if (isResultEmpty(statement.executeQuery())) {
                    Finish(saveResult, connection, "Flight with input number does not exist");
                    connection.rollback();
                    return;
                }
            }
            try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(
                    "EighthTask_checkSeat.sql")))) {
                statement.setInt(1, flight);
                statement.setString(2, seat);
                if (isResultEmpty(statement.executeQuery())) {
                    Finish(saveResult, connection, "Input seat does not exist");
                    return;
                }
            }
            try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(
                    "EighthTask_checkTicketNumber.sql")))) {
                statement.setString(1, ticketNumber);
                if (!isResultEmpty(statement.executeQuery())) {
                    Finish(saveResult, connection, "ticket with this number already exist");
                    return;
                }
            }
            InsertTicket(connection);

            try (var statement = connection.createStatement()) {
                saveResult.saveResult(statement.executeQuery("Select 'Accept'"));
            }
            connection.commit();
        }
    }

    private boolean isResultEmpty(ResultSet result) throws SQLException {
        return !result.next();
    }

    private void Finish(SaveResult saveResult, Connection connection, String error) throws SQLException {
        try (var statement = connection.createStatement()) {
            saveResult.saveResult(statement.executeQuery(String.format("Select 'Reject: %s'", error)));
        }
    }

    private void InsertTicket(Connection connection) throws SQLException, IOException {
        try (var statement = connection.prepareStatement(readFile(this.getClass().getResourceAsStream(
                "EighthTask_insertData.sql")))) {
            statement.setString(1, ticketNumber);
            statement.setString(2, bookRef);
            statement.setString(3, passengerId);
            statement.setString(4, passengerName);
            var jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(contactData);
            statement.setObject(5, jsonObject);

            statement.setString(6, ticketNumber);
            statement.setInt(7, flight);
            statement.setString(8, fareCondition);
            statement.setFloat(9, amount);
            statement.execute();
        }
    }
}
