package chess.engine.pieces;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.rules.Rule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Piece {

  public enum State {
    Selected, Captured, Alive, Promoted
  }

  private State state;
  private Square position;
  private boolean isTop;
  private boolean hasMoved;

  final Set<Square> possiblePositions = new HashSet<>();
  final Set<Square> possibleAttacks = new HashSet<>();
  final List<Rule> rules = new ArrayList<>();

  /**
   * The abstract piece class, inherited by all different pieces of the game.
   *
   * @param row   The row of the piece.
   * @param col   The column of the piece.
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
    return Collections.unmodifiableSet(possiblePositions);
  }

  public Set<Square> getPossibleAttackPositions() {
    return possibleAttacks;
  }

  public void setState(State state) {
    this.state = state;
  }

  public State getState() {
    return state;
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
    if (!isAt(row, col)) {
      position.set(row, col);
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
   * @return An integer in the range 0 - 7.
   */
  public int row() {
    return position.row();
  }

  /**
   * Returns the column of the piece.
   *
   * @return An integer in the range 0 - 7.
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

  /**
   * Returns whether or not an action is allowed to be executed.
   *
   * @param board  The game board.
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
          .newInstance(position.row(), position.col(), isTop);
      shallow.hasMoved = hasMoved;
      shallow.redoPositions();
      return shallow;
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException ignored) {
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

}
