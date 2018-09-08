package chess.pieces;

public class Rook extends Piece {

  public Rook(int row, int col, boolean isTop) {
    super(row, col, isTop);
  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < positions.length; i++) {
      positions[i][col()] = 1;
      positions[row()][i] = 1;
    }

    positions[row()][col()] = 0;
    attackPositions = positions;
  }

}
