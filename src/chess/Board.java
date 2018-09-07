package chess;

import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

  public static final int GAME_WIDTH = 8;
  public static final int GAME_HEIGHT = 8;

  private int turn;
  private Piece selected;
  private List<Piece> pieces = new ArrayList<>();
  private List<Action> history = new ArrayList<>();

  @SuppressWarnings("ConstantConditions")
  public void setupStandardBoard() {
    for (int i = 0; i < GAME_WIDTH * 2; i++) {
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
          pieces.add(new Pawn(6, i, true));
          break;
        case 8:
        case 15:
          break;
        case 9:
        case 14:
          break;
        case 10:
        case 13:
          break;
        case 11:
          pieces.add(new Queen(0, i % 8, true));
          pieces.add(new Queen(7, i % 8, true));
          break;
        case 12:
          break;
      }

    }

  }

  private void actionTaken(Action action) {
    history.add(action);
    turn++;
  }

  public List<Action> getHistory() {
    return Collections.unmodifiableList(history);
  }

  public boolean isTopTurn() {
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
    if (selected != null && selected.canMoveTo(row, col)) {
      Action action = new Action(selected, row, col, Action.Type.Move);
      if (selected.notAllowed(this, action)) {
        return false;
      }

      selected.moveTo(row, col);
      actionTaken(action);
      return true;
    }

    return false;
  }

  public boolean killAt(int row, int col) {
    if (selected == null || !selected.canAttackAt(row, col)) {
      return false;
    }

    Action action = new Action(selected, row, col, Action.Type.Attack);
    if (selected.notAllowed(this, action)) {
      return false;
    }

    if (pieces.removeIf(m -> m.isTop() != isTopTurn() && m.isAt(row, col))) {
      selected.moveTo(row, col);
      actionTaken(action);
      return true;
    }

    return false;
  }

  public Piece getAt(int row, int col) {
    return pieces.stream().filter(m -> m.isAt(row, col)).findAny().orElse(null);
  }

}
