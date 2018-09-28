package chess.engine;

import chess.engine.pieces.Square;

public final class Utils {

  private static final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

  public static String getSquareNotation(int row, int col) {
    return "" + chars[row] + (8 - col);
  }

  public static String getSourceSquareNotation(Action action) {
    return getSquareNotation(action.row(), action.col());
  }

  public static String getTargetSquareNotation(Action action) {
    return getSquareNotation(action.getPiece().row(), action.getPiece().col());
  }

  /**
   * Creates a square object from chess position notation.
   *
   * @param notation The notation to be converted, i.e "e5".
   * @return A Square object matching the given position, or null if invalid.
   */
  public static Square getSquareFromNotation(String notation) {
    if (notation.length() != 2) {
      return null;
    }

    int row = chars.length - Integer.parseInt("" + notation.charAt(1));
    int col = notation.charAt(0) - 'a';
    return new Square(row, col);
  }

}
