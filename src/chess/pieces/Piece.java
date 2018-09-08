package chess.pieces;

import chess.Action;
import chess.Board;
import chess.rules.Rule;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

  private int row, col;
  private boolean isTop;

  int[][] positions;
  final List<Rule> rules = new ArrayList<>();

  Piece(int row, int col, boolean isTop) {
    this.row = row;
    this.col = col;
    this.isTop = isTop;

    rules.add(Rule.MOVEMENT);
    rules.add(Rule.NO_OVERLAP);
    rules.add(Rule.ATTACK_MOVE);

    redoPositions();
  }

  private void redoPositions() {
    positions = new int[Board.GAME_HEIGHT][Board.GAME_WIDTH];
    calculatePossiblePositions();
  }

  public int[][] getPossiblePositions() {
    return positions;
  }

  public void moveTo(int row, int col) {
    this.row = row;
    this.col = col;
    redoPositions();
  }

  public boolean isAt(int row, int col) {
    return this.row == row && this.col == col;
  }

  abstract void calculatePossiblePositions();

  public int row() {
    return row;
  }

  public int col() {
    return col;
  }

  public boolean isTop() {
    return isTop;
  }

  public boolean notAllowed(Board board, Action action) {
    return !rules.stream().allMatch(m -> m.isActionAllowed(board, action));
  }

}
