package chess.pieces;

import chess.Action;
import chess.Board;
import chess.rules.Rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements Serializable {

  private int row, col;
  private boolean isTop;
  boolean hasMoved;

  int[][] positions, attackPositions;
  final List<Rule> rules = new ArrayList<>();

  Piece(int row, int col, boolean isTop) {
    this.row = row;
    this.col = col;
    this.isTop = isTop;

    rules.add(Rule.MOVEMENT);
    rules.add(Rule.NO_OVERLAP);
    rules.add(Rule.ATTACK_MOVE);
    rules.add(Rule.NO_CHANGE);

    redoPositions();
  }

  private void redoPositions() {
    positions = new int[Board.GAME_SIZE][Board.GAME_SIZE];
    attackPositions = new int[Board.GAME_SIZE][Board.GAME_SIZE];
    calculatePossiblePositions();
  }

  public int[][] getPossiblePositions() {
    return positions;
  }
  public int[][] getPossibleAttackPositions() {
    return attackPositions;
  }

  public boolean hasMoved() {
    return hasMoved;
  }

  public void moveTo(int row, int col) {
    if (this.row != row || this.col != col) {
      this.row = row;
      this.col = col;
      hasMoved = true;

      redoPositions();
    }
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

  public final boolean notAllowed(Board board, Action action) {
    if (rules.stream().anyMatch(m -> m.isSuperior() && m.isActionAllowed(board, action))) {
      return false;
    }

    return !rules.stream().filter(m -> !m.isSuperior()).allMatch(m -> m.isActionAllowed(board, action));
  }

}
