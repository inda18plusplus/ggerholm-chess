package chess;

import chess.pieces.Piece;
import org.apache.commons.lang3.SerializationUtils;

public final class Action {

  public enum Type {
    Move, Attack
  }

  private Piece piece;
  private int row, col;
  private Type type;

  public Action(Piece piece, int row, int col, Type type) {
    this.piece = SerializationUtils.clone(piece);
    this.row = row;
    this.col = col;
    this.type = type;
  }

  public int row() {
    return row;
  }

  public int col() {
    return col;
  }

  public Piece getPiece() {
    return piece;
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    return String.format("%s %s (%d, %d) (%d, %d)", type.toString(), piece.getClass().getSimpleName(), piece.row(), piece.col(), row, col);
  }

}
