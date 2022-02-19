package ru.inponomarev;

import java.util.ArrayList;
import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Board board = new Board(scan);
        ArrayList<Steps> stepsList = Steps.initList(scan);

        try {
            GameModel.run(board, stepsList);
        } catch (WhiteCellException e) {
            System.out.println("white cell");
            return;
        } catch (BusyCellException e) {
            System.out.println("busy cell");
            return;
        } catch (InvalidMoveException e) {
            System.out.println("invalid move");
            return;
        } catch (ErrorException e) {
            System.out.println("error");
            return;
        }

        board.print();
    }
}
