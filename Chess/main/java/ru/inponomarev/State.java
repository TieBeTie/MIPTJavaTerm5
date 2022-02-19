package ru.inponomarev;

public final class State {
    public static final int READ_SYM = 0;
    public static final int READ_NUM = 1;
    public static final int PARSE = 2;

    private State() {
    }
}
