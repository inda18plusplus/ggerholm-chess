package chess.engine.pieces;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.rules.Rule;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Piece {

  final Set<Square> possiblePositions = new HashSet<>();
  final Set<Square> possibleAttacks = new HashSet<>();
  final List<Rule> rules = new ArrayList<>();
  private State state;
  private Square position;
  private boolean isTop;
  private boolean hasMoved;

  /**
   * The abstract piece class, inherited by all different pieces of the game.
   *
   * @param row The row of the piece.
   * @param col The column of the piece.
   * @param isTop Whether the piece belongs to the top or bottom team.
   */
  Piece(int row, int col, boolean isTop) {
    this.isTop = isTop;
    position = new Square(row, col);
    state = State.Alive;

    rules.add(Rule.MOVEMENT);
    rules.add(Rule.NO_OVERLAP);
    rules.add(Rule.ATTACK);
    rules.add(Rule.NO_CHANGE);
    rules.add(Rule.NO_CHECK);
    rules.add(Rule.KING_INVULNERABILITY);

    redoPositions();
  }

  private void redoPositions() {
    possiblePositions.clear();
    possibleAttacks.clear();
    calculatePossiblePositions();
  }

  public Set<Square> getPossiblePositions() {
    return new HashSet<>(possiblePositions);
  }

  public Set<Square> getPossibleAttackPositions() {
    return new HashSet<>(possibleAttacks);
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public boolean hasMoved() {
    return hasMoved;
  }

  public void resetTo(int row, int col) {
    this.position = Square.of(row, col);
    hasMoved = false;
  }

  /**
   * Moves the piece to the provided square. Also recalculates possible next moves.
   *
   * @param row The new row.
   * @param col The new column.
   */
  public void moveTo(int row, int col) {
    if (!isAt(row, col)) {
      position = Square.of(row, col);
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
    return position.isAt(row, col);
  }

  abstract void calculatePossiblePositions();

  public abstract char toChar();

  public Square pos() {
    return position;
  }

  /**
   * Returns the row of the piece.
   *
   * @return An integer within the range of the board.
   */
  public int row() {
    return position.row();
  }

  /**
   * Returns the column of the piece.
   *
   * @return An integer within the range of the board.
   */
  public int col() {
    return position.col();
  }

  /**
   * Returns whether the piece belongs to the top or bottom team.
   *
   * @return True if the piece belongs to the top team, otherwise false.
   */
  public boolean isTop() {
    return isTop;
  }

  public void setTeam(boolean isTop) {
    this.isTop = isTop;
  }

  /**
   * Returns whether or not an action is allowed to be executed.
   *
   * @param board The game board.
   * @param action The action to be executed.
   * @return True if a special action triggered or all conditions for a normal move were passed.
   */
  public final boolean isAllowed(Board board, Action action) {
    if (rules.stream().anyMatch(m -> m.isSuperior()
        && m.isActionAllowed(board, action).equals(Rule.Result.Passed))) {
      return true;
    }

    return rules
        .stream()
        .filter(m -> !m.isSuperior())
        .noneMatch(m -> m.isActionAllowed(board, action).equals(Rule.Result.NotPassed));
  }

  /**
   * Creates a deep copy of the piece. Changes made to the deep copy will not interfere with the
   * original.
   *
   * @return A piece object if a deep copy was successfully created, otherwise null.
   */
  public Piece getDeepCopy() {
    Piece copy;

    try {
      copy = this.getClass()
          .getConstructor(int.class, int.class, boolean.class)
          .newInstance(position.row(), position.col(), isTop);
      copy.state = state;
      copy.hasMoved = hasMoved;
      copy.redoPositions();
      return copy;
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  /**
   * Converts the piece into a readable String.
   *
   * @return A string in the format "PieceType Row Column".
   */
  public String toString() {
    return String.format("%s %d %d",
        getClass().getSimpleName(),
        row(),
        col());
  }

  public enum State {
    Selected, Captured, Alive, Promoted
  }

}
