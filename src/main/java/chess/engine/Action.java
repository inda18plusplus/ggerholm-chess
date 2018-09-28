package chess.engine;

import chess.engine.pieces.Piece;
import chess.engine.pieces.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Action {

  public enum Type {
    Move, Attack, Castling
  }

  private String note;
  private final Piece piece;
  private final int row;
  private final int col;
  private final Type type;
  private final List<Runnable> acts = new ArrayList<>();

  /**
   * Creates an action-object to describe a move by one Piece.
   *
   * @param piece The piece which is executing the move.
   * @param row The targeted row.
   * @param col The targeted column.
   * @param type The type of the move, either Attack or Move.
   */
  public Action(Piece piece, int row, int col, Type type) {
    this.piece = piece.getDeepCopy();
    this.row = row;
    this.col = col;
    this.type = type;
  }

  public Action(Piece piece, Square target, Type type) {
    this(piece, target.row(), target.col(), type);
  }

  /**
   * Executes all acts in the action.
   *
   * @return Returns the amount of acts executed.
   */
  int execute() {
    acts.forEach(Runnable::run);
    return acts.size();
  }

  /**
   * Inserts an act into this action. All acts are executed in the order they appear in the list.
   *
   * @param first Whether to add this act first or last in the list.
   * @param act The act itself.
   */
  public void insertAct(boolean first, Runnable act) {
    acts.add(first ? 0 : acts.size(), act);
  }

  public void clearActs() {
    acts.clear();
  }

  public void setNote(String note) {
    this.note = note;
  }

  /**
   * The targeted row of this action.
   *
   * @return An integer within the range of the board.
   */
  public int row() {
    return row;
  }

  /**
   * The targeted column of this action.
   *
   * @return An integer within the range of the board.
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
   * @return A string in the format "Piece Type From(row, column) To(row, column) Note(if any)".
   */
  public String toString() {
    return String.format("%s %s (%d, %d) (%d, %d) %s",
        piece.getClass().getSimpleName(),
        type.toString(),
        piece.row(),
        piece.col(),
        row,
        col,
        Optional.ofNullable(note).orElse("")).trim();
  }

  public String getSourceSquareNotation() {
    char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    return "" + chars[piece.col()] + (8 - piece.row());
  }

  public String getDestinationSquareNotation() {
    char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    return "" + chars[col] + (8 - row);
  }

}
