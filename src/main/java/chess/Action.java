package chess;

import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public final class Action {

  public enum Type {
    Move, Attack
  }

  private Piece piece;
  private int row;
  private int col;
  private Type type;
  private List<Runnable> acts = new ArrayList<>();

  /**
   * Creates an action-object to describe a move by one Piece.
   *
   * @param piece The piece which is executing the move.
   * @param row   The targeted row.
   * @param col   The targeted column.
   * @param type  The type of the move, either Attack or Move.
   */
  public Action(Piece piece, int row, int col, Type type) {
    this.piece = piece.getShallowCopy();
    this.row = row;
    this.col = col;
    this.type = type;
  }

  /**
   * Executes all acts in the action.
   */
  void execute() {
    acts.forEach(Runnable::run);
  }

  /**
   * Inserts an act into this action.
   * All acts are executed in the order they appear in the list.
   *
   * @param first Whether to add this act first or last in the list.
   * @param act   The act itself.
   */
  public void insertAct(boolean first, Runnable act) {
    acts.add(first ? 0 : acts.size(), act);
  }

  /**
   * The targeted row of this action.
   *
   * @return The row number between 0 - 7.
   */
  public int row() {
    return row;
  }

  /**
   * The targeted column of this action.
   *
   * @return The column number between 0 - 7.
   */
  public int col() {
    return col;
  }

  /**
   * The piece executing this action.
   *
   * @return A piece object.
   */
  public Piece getPiece() {
    return piece;
  }

  /**
   * The type of this action.
   *
   * @return Either Attack or Move, of type enum (Action.Type).
   */
  public Type getType() {
    return type;
  }

  /**
   * Converts the action into a readable String.
   *
   * @return A string in the format "Type Piece From(row, column) To(row, column)".
   */
  public String toString() {
    return String.format("%s %s (%d, %d) (%d, %d)",
            type.toString(),
            piece.getClass().getSimpleName(),
            piece.row(),
            piece.col(),
            row,
            col);
  }

}
