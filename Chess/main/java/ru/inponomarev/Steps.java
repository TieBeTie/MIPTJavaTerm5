package ru.inponomarev;

import java.util.ArrayList;
import java.util.Scanner;

public class Steps {
    private ArrayList<Pos> posList;
    private boolean attacked;

    public Steps(ArrayList<Pos> posList, boolean attacked) {
        this.posList = posList;
        this.attacked = attacked;
    }

    public static ArrayList<Steps> initList(Scanner scan) {
        ArrayList<Steps> result = new ArrayList<>();
        Steps steps = new Steps(new ArrayList<Pos>(), false);
        Pos pos = new Pos();

        while (scan.hasNextLine()) {
            int state = State.READ_SYM;
            String line = scan.nextLine();
            for (char c : line.toCharArray()) {
                switch (state) {
                    case State.READ_SYM:
                        pos.setX(c - (Character.isUpperCase(c) ? 1 : 0) * ('A' - 'a') - 'a');
                        state = State.READ_NUM;
                        break;
                    case State.READ_NUM:
                        pos.setY(c - '1');
                        state = State.PARSE;
                        break;
                    case State.PARSE:
                        steps.posList.add(pos);
                        pos = new Pos();

                        if (c == ':') {
                            steps.setAttacked(true);
                        } else if (c == '-') {
                            steps.setAttacked(false);
                        } else {
                            result.add(steps);
                            steps = new Steps(new ArrayList<Pos>(), false);
                        }

                        state = State.READ_SYM;
                        break;
                }
            }
            steps.posList.add(pos);
            pos = new Pos();
            result.add(steps);
            steps = new Steps(new ArrayList<Pos>(), false);
        }
        return result;
    }

    /**
     * @return все позиции
     */
    public ArrayList<Pos> getPosList() {
        return posList;
    }

    /**
     * @param posList назначить позиции
     */
    public void setPosList(ArrayList<Pos> posList) {
        this.posList = posList;
    }

    /**
     * @return атакующий ли ход?
     */
    public boolean isAttacked() {
        return attacked;
    }

    /**
     * @param isAttacked сделать ход атакующим
     */
    public void setAttacked(boolean isAttacked) {
        this.attacked = isAttacked;
    }
}
