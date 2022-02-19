package ru.inponomarev;

public class BusyCellException extends RuntimeException {
    public BusyCellException() {
        super("busy cell");
    }
}
