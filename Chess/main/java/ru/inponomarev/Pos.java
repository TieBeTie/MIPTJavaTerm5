package ru.inponomarev;

import java.util.Objects;

public class Pos {
    private int x;
    private int y;

    public Pos() {
    }

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return dd
     */
    public int getX() {
        return x;
    }

    /**
     * @param x aa
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @param o объект
     * @return равен ли
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pos)) return false;
        Pos pos = (Pos) o;
        return x == pos.x && y == pos.y;
    }

    /**
     *
     * @return хеш значение
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @return aa
     */
    public int getY() {
        return y;
    }

    /**
     * @param y aa
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @param other ad
     */
    public void set(Pos other) {
        this.x = other.getX();
        this.y = other.getY();
    }
}
