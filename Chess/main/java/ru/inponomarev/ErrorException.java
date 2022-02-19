package ru.inponomarev;

public class ErrorException extends RuntimeException {
    public ErrorException() {
        super("error exception");
    }
}
