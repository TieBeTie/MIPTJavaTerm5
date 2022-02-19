package ru.inponomarev;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException() {
        super("invalid move");
    }
}
