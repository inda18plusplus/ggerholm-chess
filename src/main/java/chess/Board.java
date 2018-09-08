package chess;

import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import chess.rules.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

  public static final int GAME_SIZE = 8;

  private int turn;
  private Piece selected;
  private List<Piece> pieces = new ArrayList<>();
  private List<Action> history = new ArrayList<>();

  @SuppressWarnings("ConstantConditions")
  public void setupStandardBoard() {
    pieces.clear();
    history.clear();
    turn = 0;
    selected = null;

    for (int i = 0; i < GAME_SIZE * 2; i++) {
      switch (i) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          pieces.add(new Pawn(1, i, true));
          pieces.add(new Pawn(6, i, false));
          break;
        case 8:
        case 15:
          pieces.add(new Rook(0, i % 8, true));
          pieces.add(new Rook(7, i % 8, false));
          break;
        case 9:
        case 14:
          pieces.add(new Knight(0, i % 8, true));
          pieces.add(new Knight(7, i % 8, false));
          break;
        case 10:
        case 13:
          pieces.add(new Bishop(0, i % 8, true));
          pieces.add(new Bishop(7, i % 8, false));
          break;
        case 11:
          pieces.add(new Queen(0, i % 8, true));
          pieces.add(new Queen(7, i % 8, false));
          break;
        case 12:
          pieces.add(new King(0, i % 8, true));
          pieces.add(new King(7, i % 8, false));
          break;
      }

    }

  }

  private void actionTaken(Action action, boolean skipTurn) {
    history.add(action);

    if (!skipTurn) {
      turn++;
    }
  }

  public int getTurn() {
    return turn;
  }

  public List<Action> getHistory() {
    return Collections.unmodifiableList(history);
  }

  private boolean isTopTurn() {
    return turn % 2 == 0;
  }

  public boolean selectPieceAt(int row, int col) {
    selected = getAt(row, col);

    if (selected != null && (isTopTurn() != selected.isTop())) {
      selected = null;
      return false;
    }

    return selected != null;
  }

  public boolean moveTo(int row, int col) {
    if (selected == null) {
      return false;
    }

    Action action = new Action(selected, row, col, Action.Type.Move);
    if (selected.notAllowed(this, action)) {
      return false;
    }

    selected.moveTo(row, col);
    actionTaken(action, false);
    return true;
  }

  public boolean killAt(int row, int col) {
    if (selected == null) {
      return false;
    }

    Action action = new Action(selected, row, col, Action.Type.Attack);
    if (selected.notAllowed(this, action)) {
      return false;
    }

    if (pieces.removeIf(m -> m.isTop() != isTopTurn() && m.isAt(row, col))) {
      selected.moveTo(row, col);
      actionTaken(action, false);
      return true;
    }

    return false;
  }

  public boolean isSquareUnderAttack(int row, int col) {
    if (pieces
            .stream()
            .filter(m -> m instanceof Pawn)
            .anyMatch(m ->
                    Rule.EN_PASSANT
                            .isActionAllowed(this,
                                    new Action(m, row + (m.row() < row ? -1 : 1), col, Action.Type.Move)
                            )
            )
    ) {
      return true;
    }

    return pieces.stream().anyMatch(m -> m.getPossibleAttackPositions()[row][col] == 1);
  }

  public void forceKill(Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      pieces.removeIf(m -> m.isAt(action.row(), action.col()));
      actionTaken(new Action(action.getPiece(), action.row(), action.col(), Action.Type.Attack), true);
      return;
    }

    pieces.removeIf(m -> m.isAt(action.row(), action.col()));
    actionTaken(action, true);
  }

  public void forceMove(int fromRow, int fromCol, int toRow, int toCol) {
    pieces.stream().filter(m -> m.isAt(fromRow, fromCol)).findAny().ifPresent(m -> {

      Action moveAction = new Action(m, toRow, toCol, Action.Type.Move);
      m.moveTo(toRow, toCol);
      actionTaken(moveAction, true);

    });
  }

  public Piece getAt(int row, int col) {
    return pieces.stream().filter(m -> m.isAt(row, col)).findAny().orElse(null);
  }

}
