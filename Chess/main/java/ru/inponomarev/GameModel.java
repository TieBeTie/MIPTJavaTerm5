package ru.inponomarev;

import java.util.ArrayList;
import java.util.Set;

final class GameModel {
    private GameModel() {
    }

    public static void run(Board board, ArrayList<Steps> moves) {
        for (int moveInd = 0; moveInd < moves.size(); ++moveInd) {
            Steps steps = moves.get(moveInd);
            ArrayList<Pos> poses = steps.getPosList();

            for (int posInd = 1; posInd < poses.size(); ++posInd) {
                Pos curPos = poses.get(posInd - 1);
                Pos nextPos = poses.get(posInd);

                // клетка занята
                if (board.getCell(nextPos).getColor() != Color.EMPTY) {
                    throw new BusyCellException();
                }

                // номера клеток должны быть чётные или нечётные
                if (nextPos.getX() % 2 == 1 ^ nextPos.getY() % 2 == 1) {
                    throw new WhiteCellException();
                }

                // есть 3 варианта побить шашку, если он его не использует, а идёт на другую клетку.
                // то invalid move
                // I в начале
                if (posInd == 1) {
                    Set<Pos> set = moveInd % 2 == 0 ? board.getWhites() : board.getBlacks();
                    boolean dueKill = false;
                    for (Pos pos : set) {
                        dueKill |= board.checkKill(pos);
                    }
                    if (dueKill && !board.isAttacked(curPos, nextPos)) {
                        throw new InvalidMoveException();
                    }
                }

                // II во время хода
                if (posInd > 1 && board.checkKill(curPos) && !board.isAttacked(curPos, nextPos)) {
                    throw new InvalidMoveException();
                }

                board.move(curPos, nextPos, steps.isAttacked());

                if (steps.isAttacked()) {
                    if (!board.deleteCheckerOn(curPos, nextPos)) {
                        throw new ErrorException();
                    }
                }
                curPos = nextPos;


                // III в конце
                if (posInd == poses.size() - 1 && board.checkKill(curPos)
                        && steps.isAttacked()) {
                    throw new InvalidMoveException();
                }
            }
            Pos finalPos = poses.get(poses.size() - 1);
            // строка 8 для белых дамочная и наоборот
            if (finalPos.getY() == board.getSize() - 1
                    && board.getCell(finalPos).getColor() == Color.WHITE
                    || finalPos.getY() == 0
                    && board.getCell(finalPos).getColor() == Color.BLACK) {
                board.getCell(finalPos).setKing(true);
            }
        }
    }
}
