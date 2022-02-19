package ru.inponomarev;

public class Cell {
    private int color = Color.EMPTY;
    private boolean isKing = false;

    public Cell(int color, boolean isKing) {
        this.color = color;
        this.isKing = isKing;
    }

    public Cell() {
    }

    /**
     * @return дамка ли?
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     * @param king назначить дамкой
     */
    public void setKing(boolean king) {
        isKing = king;
    }

    /**
     * @return цвет шашки
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color назначить цвет
     */
    public void setColor(int color) {
        this.color = color;
    }
}
