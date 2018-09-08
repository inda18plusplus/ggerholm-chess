package chess.pieces;

import chess.Action;
import chess.Board;
import chess.rules.Rule;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements Serializable {

  private int row;
  private int col;
  private boolean isTop;

  boolean hasMoved;
  int[][] positions;
  int[][] attackPositions;
  final List<Rule> rules = new ArrayList<>();

  /**
   * The abstract piece class, inherited by all different pieces of the game.
   *
   * @param row   The row of the piece.
   * @param col   The col of the piece.
   * @param isTop Whether or not the piece belongs to the top or bottom team.
   */
  Piece(int row, int col, boolean isTop) {
    this.row = row;
    this.col = col;
    this.isTop = isTop;

    rules.add(Rule.MOVEMENT);
    rules.add(Rule.NO_OVERLAP);
    rules.add(Rule.ATTACK_MOVE);
    rules.add(Rule.NO_CHANGE);
    rules.add(Rule.NO_CHECK);

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

  /**
   * Moves the piece to the provided square.
   * Also recalculates possible next moves.
   *
   * @param row The new row.
   * @param col The new column.
   */
  public void moveTo(int row, int col) {
    if (this.row != row || this.col != col) {
      this.row = row;
      this.col = col;
      hasMoved = true;

      redoPositions();
    }
  }

  /**
   * Returns whether or not the piece is currently situated the the provided square.
   *
   * @param row The row number.
   * @param col The column number.
   * @return True or false.
   */
  public boolean isAt(int row, int col) {
    return this.row == row && this.col == col;
  }

  /**
   * Returns wether or not this piece can attack the provided square.
   * This does not include special moves like the Pawn's 'en passant'.
   * It also does not care if the square is empty or not.
   *
   * @param board The current board
   * @param row   The targeted row.
   * @param col   The targeted column.
   * @return True if an attack could be made to the provided square, otherwise false.
   */
  public boolean canAttackAt(Board board, int row, int col) {
    Action action = new Action(this, row, col, Action.Type.Attack);
    return attackPositions[row][col] == 1
            && rules
            .stream()
            .filter(m -> !m.isSuperior())
            .allMatch(m -> m.isActionAllowed(board, action));
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

  /**
   * Returns whether or not an action is allowed to be executed.
   * Also implements changes to the board if the action would trigger a special move,
   * like the pawn's 'en passant'.
   *
   * @param board  The game board.
   * @param action The action to be executed.
   * @return True if a special action triggered or all conditions for a normal move were passed.
   */
  public final boolean notAllowed(Board board, Action action) {
    if (rules.stream().anyMatch(m -> m.isSuperior() && m.isActionAllowed(board, action))) {
      return false;
    }

    return !rules
            .stream()
            .filter(m -> !m.isSuperior())
            .allMatch(m -> m.isActionAllowed(board, action));
  }

  /**
   * Creates a shallow copy of the piece.
   * Changes made to the shallow copy will not interfere with the original.
   *
   * @return A piece object if a shallow copy was successfully created, otherwise null.
   */
  public Piece getShallowCopy() {
    Piece shallow;

    try {
      shallow = this.getClass()
              .getConstructor(int.class, int.class, boolean.class)
              .newInstance(row, col, isTop);
      return shallow;
    } catch (InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException ignored) {
      return null;
    }
  }

}
