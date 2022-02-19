package ru.inponomarev;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Board {
    private static final int BOARD_SIZE = 8;
    private static final int ATTACKED_RADIUS = 2;
    private static final int[] SYM_TURN = {-1, 1};
    private static final int[] NUM_TURN = {-1, 1};
    private static Scanner scan = null;

    private final Cell[][] cells;
    private final Set<Pos> whites;
    private final Set<Pos> blacks;

    public Board(Scanner scan) {
        Board.scan = scan;
        cells = new Cell[BOARD_SIZE][BOARD_SIZE];
        whites = new HashSet<>();
        blacks = new HashSet<>();

        for (int i = 0; i < BOARD_SIZE; ++i) {
            cells[i] = new Cell[BOARD_SIZE];
            for (int j = 0; j < BOARD_SIZE; ++j) {
                cells[i][j] = new Cell();
            }
        }

        initBoard(getScan().nextLine(), false);
        initBoard(getScan().nextLine(), true);
    }

    /**
     * @param line    строка шашек
     * @param isBlack белые или чёрные
     */
    private void initBoard(String line, boolean isBlack) {

        int state = State.READ_SYM;

        boolean isKing = false;
        Pos pos = new Pos();

        for (char c : line.toCharArray()) {
            switch (state) {
                case State.READ_SYM:
                    isKing = Character.isUpperCase(c);
                    pos.setX(c - (isKing ? 1 : 0) * ('A' - 'a') - 'a');
                    state = State.READ_NUM;
                    break;

                case State.READ_NUM:
                    pos.setY(c - '1');
                    state = State.PARSE;
                    break;

                case State.PARSE:
                    cells[pos.getX()][pos.getY()].setKing(isKing);
                    cells[pos.getX()][pos.getY()].setColor(isBlack ? Color.BLACK : Color.WHITE);

                    if (isBlack) {
                        blacks.add(pos);
                    } else {
                        whites.add(pos);
                    }

                    pos = new Pos();
                    state = State.READ_SYM;
                    break;

            }
        }
        cells[pos.getX()][pos.getY()].setKing(isKing);
        cells[pos.getX()][pos.getY()].setColor(isBlack ? Color.BLACK : Color.WHITE);
        if (isBlack) {
            blacks.add(pos);
        } else {
            whites.add(pos);
        }
    }

    /**
     * @return размеры доски
     */
    int getSize() {
        return BOARD_SIZE;
    }

    /**
     * @return возвращает множество позиций белых шашек
     */
    public Set<Pos> getWhites() {
        return whites;
    }

    /**
     * @return возвращает множество позиций чёрных шашек
     */
    public Set<Pos> getBlacks() {
        return blacks;
    }

    /**
     * @param from позиция шашки
     * @param to   куда её передвинуть
     */
    public void move(Pos from, Pos to, boolean isAttacked) {
        int lim = getCell(from).isKing() ? BOARD_SIZE : ATTACKED_RADIUS;
        if (!getCell(from).isKing() && !isAttacked) {
            lim = 1;
        }

        if (!isExist(from) || isEmpty(from) || dist(from, to) > lim
                || Math.abs(from.getX() - to.getX()) != Math.abs(from.getY() - to.getY())) {
            throw new ErrorException();
        }

        if (getCell(from).getColor() == Color.WHITE) {
            whites.add(to);
            whites.remove(from);
        } else {
            blacks.add(to);
            blacks.remove(from);
        }

        Cell tmp = cells[from.getX()][from.getY()];
        cells[from.getX()][from.getY()] = cells[to.getX()][to.getY()];
        cells[to.getX()][to.getY()] = tmp;
    }

    /**
     * Удаление шашек по диагонали, между шашками в позициях А и Б
     *
     * @param from А
     * @param to   Б
     */
    public boolean deleteCheckerOn(Pos from, Pos to) {
        int symTurn = (int) Math.signum(to.getX() - from.getX());
        int numTurn = (int) Math.signum(to.getY() - from.getY());
        int deleted = 0;

        for (int dist = 1; dist < Math.abs(to.getX() - from.getX()); ++dist) {
            Pos pos = new Pos(
                    from.getX() + symTurn * dist,
                    from.getY() + numTurn * dist);
            if (!isEnemy(to, pos) && !isEmpty(pos)) {
                return false;
            }
            if (deleteChecker(pos)) {
                ++deleted;
            }
        }
        return deleted == 1;
    }

    /**
     * @param pos позиция клетки
     */
    private boolean deleteChecker(Pos pos) {
        boolean exist = getCell(pos).getColor() != Color.EMPTY;
        if (exist) {
            blacks.remove(pos);
            whites.remove(pos);

            getCell(pos).setKing(false);
            getCell(pos).setColor(Color.EMPTY);
        }
        return exist;
    }

    /**
     * @param pos позиция клетки
     * @return клетка
     */
    public Cell getCell(Pos pos) {
        return cells[pos.getX()][pos.getY()];
    }

    /**
     * @return adf
     */
    public static Scanner getScan() {
        return scan;
    }

    /**
     * Печать дамок белых шашек, потом простых, и аналогично чёрных.
     */
    public void print() {
        for (char color : new char[]{Color.WHITE, Color.BLACK}) {
            for (boolean king : new boolean[]{true, false}) {
                for (int i = 0; i < getSize(); ++i) {
                    for (int j = 0; j < getSize(); ++j) {
                        Cell cell = cells[i][j];
                        if (cell.getColor() == color && cell.isKing() == king) {
                            System.out.print((char) (i + 'a' + ('A' - 'a') * (cell.isKing() ? 1 : 0)));
                            System.out.print(j + 1);
                            System.out.print(' ');
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * @param pos А
     * @return Может ли шашка в позиции А атаковать какую либо шашку?
     */
    public boolean checkKill(Pos pos) {

        int lim = getCell(pos).isKing() ? BOARD_SIZE : ATTACKED_RADIUS;

        for (int symTurn : SYM_TURN) {
            for (int numTurn : NUM_TURN) {
                for (int dist = 1; dist < lim; ++dist) {

                    Pos otherPos = new Pos(
                            pos.getX() + symTurn * dist,
                            pos.getY() + numTurn * dist);

                    Pos nextCellPos = nextCell(pos, otherPos);
                    if (!isExist(nextCellPos)) {
                        break;
                    }
                    if (isEnemy(pos, otherPos) && isEmpty(nextCellPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param pos позиция клетки
     * @return пуста ли она?
     */
    public boolean isEmpty(Pos pos) {
        return getCell(pos).getColor() == Color.EMPTY;
    }

    /**
     * @param pos      А
     * @param otherPos Б
     * @return следующая клетка после Б, по направлению А->Б
     */
    private Pos nextCell(Pos pos, Pos otherPos) {
        int symTurn = (int) Math.signum(otherPos.getX() - pos.getX());
        int numTurn = (int) Math.signum(otherPos.getY() - pos.getY());

        return new Pos(otherPos.getX() + symTurn, otherPos.getY() + numTurn);
    }

    /**
     * @param pos      А
     * @param otherPos Б
     * @return Враг ли шашке А шашка Б
     */
    public boolean isEnemy(Pos pos, Pos otherPos) {
        return getCell(pos).getColor() == Color.WHITE && getCell(otherPos).getColor() == Color.BLACK
                || getCell(pos).getColor() == Color.BLACK && getCell(otherPos).getColor() == Color.WHITE;

    }

    /**
     * @param pos позиция шашки
     * @return содержит ли доска её?
     */
    public boolean isExist(Pos pos) {
        return 0 <= pos.getX() && pos.getX() < BOARD_SIZE
                && 0 <= pos.getY() && pos.getY() < BOARD_SIZE;
    }

    /**
     * @param from А
     * @param to   Б
     * @return Происходит ли взятие шашкой в позиции А, которая идёт в Б?
     */
    public boolean isAttacked(Pos from, Pos to) {
        return dist(from, to) > 1;
    }

    /**
     * @param from А
     * @param to   Б
     * @return расстояние  между шашками в позициях А и Б
     */
    private int dist(Pos from, Pos to) {
        return Math.abs(to.getX() - from.getX());
    }
}
