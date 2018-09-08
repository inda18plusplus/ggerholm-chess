package chess;

import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import chess.rules.Rule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Board implements BoardInterface {

  public static final int GAME_SIZE = 8;

  private static Board instance = new Board();

  private boolean promoteAfterAction;
  private int promotionIndex = -1;
  private int turn;
  private Piece selected;
  private List<Piece> pieces = new ArrayList<>();
  private List<Action> history = new ArrayList<>();

  public static BoardInterface getInstance() {
    return instance;
  }

  private Board() {
  }

  @Override
  public Board getEngine() {
    return this;
  }

  @Override
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
        default:
          break;
      }

    }

  }

  @Override
  public void setupEmptyBoard() {
    pieces.clear();
    history.clear();
    turn = 0;
    selected = null;
  }

  private void actionTaken(Action action, boolean skipTurn) {
    history.add(action);

    if (!skipTurn) {
      turn++;
    }

    if (promoteAfterAction) {
      for (int i = 0; i < pieces.size(); i++) {
        if (pieces.get(i).isAt(action.row(), action.col())) {
          promotionIndex = i;
          break;
        }
      }
    }

  }

  @Override
  public int getTurn() {
    return turn;
  }

  /**
   * Makes it so that the next action done must be a promotion of a Pawn.
   */
  public void promoteAfterAction() {
    if (isPromoting()) {
      return;
    }

    promoteAfterAction = true;
  }

  @Override
  public boolean promoteTo(Class<? extends Piece> promotion) {
    if (!isPromoting()) {
      return false;
    }

    if (promotion.equals(Pawn.class)) {
      return false;
    }

    Piece p = pieces.get(promotionIndex);
    try {
      Piece promoted = promotion.getConstructor(
              int.class,
              int.class,
              boolean.class)
              .newInstance(p.row(), p.col(), p.isTop());

      pieces.remove(promotionIndex);
      pieces.add(promotionIndex, promoted);
      promotionIndex = -1;

    } catch (InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException ignored) {
      return false;
    }

    return true;
  }

  @Override
  public boolean isPromoting() {
    return promotionIndex >= 0;
  }

  /**
   * Returns all previously executed actions.
   *
   * @return An unmodifiable list of Action-objects.
   */
  public List<Action> getHistory() {
    return Collections.unmodifiableList(history);
  }

  @Override
  public boolean isTopTurn() {
    return turn % 2 == 0;
  }

  /**
   * Returns whether or not the king is currently threatened.
   *
   * @param isTop Whether to check the top or bottom king.
   * @return True or false.
   */
  public boolean isKingInCheck(boolean isTop) {
    Piece king = pieces
            .stream()
            .filter(m -> m.isTop() == isTop && (m instanceof King))
            .findAny()
            .orElse(null);

    if (king == null) {
      return false;
    }

    return isSquareUnderAttack(king.row(), king.col(), king.isTop());
  }

  @Override
  public boolean selectPieceAt(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    selected = getAt(row, col);

    if (selected != null && (isTopTurn() != selected.isTop())) {
      selected = null;
      return false;
    }

    return selected != null;
  }

  @Override
  public boolean moveTo(int row, int col) {
    if (isPromoting()) {
      return false;
    }

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

  @Override
  public boolean killAt(int row, int col) {
    if (isPromoting()) {
      return false;
    }

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

  /**
   * Returns whether or not a square is potentially under attack.
   *
   * @param row   The row number.
   * @param col   The column number.
   * @param isTop Assumes the square has a unit from this team.
   * @return True if any unit the enemy team could mount an attack towards this square.
   */
  public boolean isSquareUnderAttack(int row, int col, boolean isTop) {
    return isSquareUnderAttack(row, col, isTop, false);
  }

  @Override
  public boolean isSquareUnderAttack(int row, int col, boolean isTop, boolean isPawn) {

    if (isPawn
            && pieces
            .stream()
            .filter(m -> (m instanceof Pawn) && m.isTop() != isTop)
            .anyMatch(m ->
                    Rule.EN_PASSANT.isActionAllowed(this,
                            new Action(
                                    m,
                                    row + (m.row() < row ? -1 : 1),
                                    col,
                                    Action.Type.Move)))) {
      return true;
    }

    return pieces.stream().anyMatch(m -> m.isTop() != isTop && m.canAttackAt(this, row, col));
  }

  /**
   * Forces the action to be executed and does not increase the turn.
   *
   * @param action The attack action to be executed.
   */
  public void forceKill(Action action) {
    int row = action.row();
    int col = action.col();

    if (pieces.removeIf(m -> m.isAt(row, col))) {
      actionTaken(new Action(action.getPiece(), row, col, Action.Type.Attack), true);
    }

  }

  /**
   * Forces the unit (if any) on the provided square to be moved and does not increase the turn.
   * Also removes any unit on the targeted square.
   *
   * @param fromRow The original row.
   * @param fromCol The original column.
   * @param toRow   The new row.
   * @param toCol   The new column.
   */
  public void forceMove(int fromRow, int fromCol, int toRow, int toCol) {
    pieces.stream().filter(m -> m.isAt(fromRow, fromCol)).findAny().ifPresent(m -> {

      pieces.removeIf(p -> p.isAt(toRow, toCol));

      Action moveAction = new Action(m, toRow, toCol, Action.Type.Move);
      m.moveTo(toRow, toCol);
      actionTaken(moveAction, true);

    });
  }

  /**
   * Returns the piece currently occupying the provided square.
   *
   * @param row The row number.
   * @param col The column number.
   * @return The piece (if any) at the provided square. Passed by reference.
   */
  public Piece getAt(int row, int col) {
    return pieces.stream().filter(m -> m.isAt(row, col)).findAny().orElse(null);
  }

  /**
   * Creates a shallow copy of the current board.
   * Changes executed to the shallow copy will not interfere with the original instance.
   *
   * @return A shallow copy of the board.
   */
  public Board getShallowCopy() {
    Board shallow = new Board();
    shallow.pieces = new ArrayList<>(pieces);
    shallow.history = new ArrayList<>(history);
    shallow.turn = turn;
    shallow.promoteAfterAction = promoteAfterAction;
    shallow.promotionIndex = promotionIndex;
    shallow.selected = selected;
    return shallow;
  }

}
