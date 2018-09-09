package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;
import chess.pieces.Square;

import java.util.Set;

public final class RuleNoOverlap implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    int row = action.row();
    int col = action.col();

    int[] rowRange = new int[2];
    int[] colRange = new int[2];

    if (row > piece.row()) {
      rowRange[0] = piece.row() + 1;
      rowRange[1] = row;
    } else if (row < piece.row()) {
      rowRange[0] = row;
      rowRange[1] = piece.row() - 1;
    } else {
      rowRange[0] = row;
      rowRange[1] = row;
    }

    if (col > piece.col()) {
      colRange[0] = piece.col() + 1;
      colRange[1] = col;
    } else if (col < piece.col()) {
      colRange[0] = col;
      colRange[1] = piece.col() - 1;
    } else {
      colRange[0] = col;
      colRange[1] = col;
    }

    Set<Square> positions = piece.getPossiblePositions();
    for (int r = rowRange[0]; r <= rowRange[1]; r++) {
      for (int c = colRange[0]; c <= colRange[1]; c++) {
        if (r == row && c == col) {
          continue;
        }

        final int fR = r;
        final int fC = c;
        if (positions.stream().anyMatch(m -> m.isAt(fR, fC)) && board.getAt(r, c) != null) {
          return Result.NotPassed;
        }

      }

    }

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
