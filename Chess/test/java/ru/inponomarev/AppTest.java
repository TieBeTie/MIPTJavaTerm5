package ru.inponomarev;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Scanner;

public class AppTest {
    @Test
    void isEmptyTest() {
        Board b = new Board(new Scanner(
                "a3 c3 b2\n"
                        + "b6 d6")
        );
        ArrayList<Steps> s = Steps.initList(
                new Scanner("a3-b4 b6-c5\n" +
                        "b2-a3 d6-e5\n" +
                        "b4:d6:f4"));
        GameModel.run(b, s);
        Assertions.assertThat(b.getBlacks().isEmpty()).isTrue();
    }

    @Test
    void invalidMoveTest() {
        try {
            Board b = new Board(new Scanner(
                    "a3 c3 b2\n"
                            + "b6 d6")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("a3-b4 b6-c5\n" +
                            "b2-a3 d6-e5\n" +
                            "b4:d6"));
            GameModel.run(b, s);
        } catch (InvalidMoveException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(InvalidMoveException.class)
                    .hasMessage("invalid move");
        }
    }

    @Test
    void whiteCellTest() {
        try {
            Board b = new Board(new Scanner(
                    "a3 c3 b2\n"
                            + "b6 d6")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("a3-b4 b6-c5\n" +
                            "b2-a4 d6-e5\n" +
                            "b4:d6"));
            GameModel.run(b, s);
        } catch (WhiteCellException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(WhiteCellException.class)
                    .hasMessage("white cell");
        }
    }

    @Test
    void busyCellTest() {
        try {
            Board b = new Board(new Scanner(
                    "a3 c3 b2\n"
                            + "b6 d6")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("a3-b2 b6-c5\n" +
                            "b2-a4 d6-e5\n" +
                            "b4:d6"));
            GameModel.run(b, s);
        } catch (BusyCellException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(BusyCellException.class)
                    .hasMessage("busy cell");
        }
    }

    @Test
    void badMoveTest() {
        try {
            Board b = new Board(new Scanner(
                    "a1\n\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("a1-c3"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }

    @Test
    void missEnemyTest() {
        try {
            Board b = new Board(new Scanner(
                    "a1\n\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("a1:c3"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }

    @Test
    void toManyEnemiesTest() {
        try {
            Board b = new Board(new Scanner(
                    "A1\nb2 c3\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("A1:D4"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }
    @Test
    void toManySteps() {
        try {
            Board b = new Board(new Scanner(
                    "A1\n\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("A1-B2-C3"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }
    @Test
    void strangeMove() {
        try {
            Board b = new Board(new Scanner(
                    "A1\n\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("A1-C1"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }
    @Test
    void friendKill() {
        try {
            Board b = new Board(new Scanner(
                    "A1\nb2\n")
            );
            ArrayList<Steps> s = Steps.initList(
                    new Scanner("A1:C3"));
            GameModel.run(b, s);
        } catch (ErrorException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(ErrorException.class)
                    .hasMessage("error exception");
        }
    }
}
