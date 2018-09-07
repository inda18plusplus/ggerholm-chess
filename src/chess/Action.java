package chess;

import chess.pieces.Piece;

public final class Action {

  public enum Type {
    Move, Attack
  }

  private Piece piece;
  private int row, col;
  private Type type;

  public Action(Piece piece, int row, int col, Type type) {
    this.piece = piece;
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

}
