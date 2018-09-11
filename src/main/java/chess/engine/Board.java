package chess.engine;

import chess.DrawablePiece;
import chess.engine.pieces.Bishop;
import chess.engine.pieces.King;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Rook;
import chess.engine.pieces.Square;
import chess.engine.rules.Rule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class Board implements BoardInterface {

  public static final int BOARD_LENGTH = 8;

  private static Board instance = new Board();

  private boolean promoteAfterAction;
  private int promotionIndex = -1;
  private int turn;
  private Piece selected;
  private List<Piece> pieces = new ArrayList<>();
  private List<Action> history = new ArrayList<>();

  private Board() {
  }

  @Override
  public Board getEngine() {
    return this;
  }

  @Override
  public void setupStandardBoard() {
    setupEmptyBoard();

    for (int i = 0; i < BOARD_LENGTH * 2; i++) {
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
  public void setupFischerBoard() {
    setupEmptyBoard();

    List<Class<? extends Piece>> topTeam = new ArrayList<>();
    topTeam.add(King.class);
    topTeam.add(Queen.class);
    topTeam.add(Rook.class);
    topTeam.add(Rook.class);
    topTeam.add(Bishop.class);
    topTeam.add(Bishop.class);
    topTeam.add(Knight.class);
    topTeam.add(Knight.class);

    List<Class<? extends Piece>> bottomTeam = new ArrayList<>(topTeam);

    Random rand = new Random();

    for (int i = 0; i < BOARD_LENGTH * 2; i++) {
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
        default:
          int topChoice = rand.nextInt(topTeam.size());
          int bottomChoice = rand.nextInt(bottomTeam.size());
          try {
            Piece top = topTeam.get(topChoice)
                .getConstructor(int.class, int.class, boolean.class)
                .newInstance(0, i % 8, true);
            Piece bottom = bottomTeam.get(bottomChoice)
                .getConstructor(int.class, int.class, boolean.class)
                .newInstance(7, i % 8, false);

            pieces.add(top);
            pieces.add(bottom);
            topTeam.remove(topChoice);
            bottomTeam.remove(bottomChoice);


          } catch (InstantiationException
              | IllegalAccessException
              | InvocationTargetException
              | NoSuchMethodException ignored) {
            // TODO: Log
          }
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

  @Override
  public boolean selectPieceAt(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    clearSelected();
    selected = getAt(row, col);

    if (selected != null && (isTopTurn() != selected.isTop())) {
      selected = null;
      return false;
    }

    if (selected != null) {
      selected.setState(Piece.State.Selected);
      return true;
    }

    return false;
  }

  @Override
  public boolean goTo(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    if (selected == null) {
      return false;
    }

    if (getAt(row, col) == null) {
      return moveTo(row, col);
    } else {
      return captureAt(row, col);
    }
  }

  @Override
  public boolean isKingInCheck(boolean isTop) {
    Piece king = getKingOfTeam(isTop);

    if (king == null) {
      return false;
    }

    return isSquareUnderAttack(king.row(), king.col(), king.isTop(), false);
  }

  @Override
  public boolean isTeamInCheckmate(boolean isTop) {
    if (!isKingInCheck(isTop)) {
      return false;
    }

    Piece king = getKingOfTeam(isTop);

    if (king == null) {
      return false;
    }

    // --- Check if the attackers can be captured ---

    List<Piece> attackers = getEnemyPieces(isTop)
        .stream()
        .filter(m -> m.isAllowed(this, new Action(m, king.pos(), Action.Type.Attack)))
        .collect(Collectors.toList());

    if (attackers
        .stream()
        .allMatch(m -> isSquareUnderAttack(m.row(), m.col(), m.isTop(), m instanceof Pawn))) {
      return false;
    }

    // --- Check if any move by any friendly piece would block the attackers ---
    return getFriendlyPieces(isTop).stream().allMatch(m -> getValidPositions(m).isEmpty());
  }

  @Override
  public boolean isTeamInStalemate(boolean isTop) {
    if (isKingInCheck(isTop)) {
      return false;
    }

    return getFriendlyPieces(isTop)
        .stream()
        .allMatch(m -> getValidPositions(m).isEmpty() && getValidAttacks(m).isEmpty());
  }

  @Override
  public State getGameState() {
    if (isKingInCheck(isTopTurn())) {
      if (isTeamInCheckmate(isTopTurn())) {
        return State.Checkmate;
      } else {
        return State.Check;
      }
    } else if (isTeamInStalemate(isTopTurn())) {
      return State.Stalemate;
    }

    return State.Normal;
  }

  @Override
  public boolean isPromoting() {
    return promotionIndex >= 0;
  }

  @Override
  public boolean promoteTo(Promotion promotion) {
    if (!isPromoting()) {
      return false;
    }

    Piece piece = pieces.get(promotionIndex);
    try {
      Piece promoted = promotion.type.getConstructor(
          int.class,
          int.class,
          boolean.class)
          .newInstance(piece.row(), piece.col(), piece.isTop());

      pieces.remove(promotionIndex);
      pieces.add(promotionIndex, promoted);
      promotionIndex = -1;
      promoteAfterAction = false;

    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException ignored) {
      // TODO: Log
      return false;
    }

    piece.setState(Piece.State.Promoted);

    return true;
  }

  @Override
  public boolean isTopTurn() {
    return turn % 2 == 0;
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

  public void addPiece(Piece piece) {
    pieces.add(piece);
  }

  /**
   * Forces the unit (if any) on the provided square to be removed and does not increase the turn.
   *
   * @param attacker The attacker. This piece will only be logged to the history.
   * @param row      The targeted row.
   * @param col      The targeted column.
   */
  public void forceKill(Piece attacker, int row, int col) {

    if (pieces.removeIf(m -> {
      if (m.isAt(row, col)) {
        m.setState(Piece.State.Captured);
        return true;
      }
      return false;
    })) {
      takeAction(new Action(attacker, row, col, Action.Type.Attack), true, 0);
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
      moveAction.insertAct(true, () -> m.moveTo(toRow, toCol));
      takeAction(moveAction, true, 0);

    });
  }

  private void clearSelected() {
    if (selected == null) {
      return;
    }

    selected.setState(Piece.State.Alive);
    selected = null;
  }

  public boolean hasSelected() {
    return selected != null;
  }

  private boolean moveTo(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    if (selected == null) {
      return false;
    }

    Action action = new Action(selected, row, col, Action.Type.Move);
    action.insertAct(true, () -> selected.moveTo(row, col));
    if (!selected.isAllowed(this, action)) {
      return false;
    }

    if (takeAction(action, false, 1)) {
      clearSelected();
      return true;
    }

    return false;
  }

  private boolean captureAt(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    if (selected == null) {
      return false;
    }

    Action action = new Action(selected, row, col, Action.Type.Attack);
    action.insertAct(true,
        () -> pieces.removeIf(m -> {
          if (m.isTop() != isTopTurn() && m.isAt(row, col)) {
            m.setState(Piece.State.Captured);
            return true;
          }

          return false;
        }));
    action.insertAct(false,
        () -> selected.moveTo(row, col));

    if (!selected.isAllowed(this, action)) {
      return false;
    }

    if (takeAction(action, false, 2)) {
      clearSelected();
      return true;
    }

    return false;
  }

  /**
   * Returns whether or not a square is potentially under attack.
   *
   * @param row    The row number.
   * @param col    The column number.
   * @param isTop  Assumes the square has a unit from this team (top if true, bottom if false).
   * @param isPawn A pawn can also be attacked using its special move, unlike other pieces.
   * @return True if any unit in the enemy team could mount an attack towards this square.
   */
  public boolean isSquareUnderAttack(int row, int col, boolean isTop, boolean isPawn) {

    if (isPawn
        && pieces
        .stream()
        .filter(m -> (m instanceof Pawn) && m.isTop() != isTop)
        .anyMatch(m ->
            Rule.EN_PASSANT.isActionAllowed(this,
                new Action(
                    m,
                    row + (m.row() < row ? 1 : -1),
                    col,
                    Action.Type.Move)).equals(Rule.Result.Passed))) {
      return true;
    }

    return getEnemyPieces(isTop)
        .stream()
        .anyMatch(m -> m.isAllowed(this, new Action(m, row, col, Action.Type.Attack)));
  }

  private boolean takeAction(Action action, boolean skipTurn, int minActsExecuted) {
    if (action.execute() < minActsExecuted) {
      return false;
    }


    if (!skipTurn) {
      history.add(action);
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

    return true;
  }

  public List<DrawablePiece> getDrawables() {
    return pieces.stream().map(DrawablePiece::new).collect(Collectors.toList());
  }

  /**
   * Returns all previously executed actions.
   *
   * @return An unmodifiable list of Action-objects.
   */
  public List<Action> getHistory() {
    return Collections.unmodifiableList(history);
  }

  private List<Piece> getEnemyPieces(boolean isTop) {
    return pieces.stream().filter(m -> m.isTop() != isTop).collect(Collectors.toList());
  }

  private List<Piece> getFriendlyPieces(boolean isTop) {
    return getEnemyPieces(!isTop);
  }

  private Set<Square> getValidPositions(Piece piece) {
    return piece
        .getPossiblePositions()
        .stream()
        .filter(m -> piece.isAllowed(this, new Action(piece, m, Action.Type.Move)))
        .collect(Collectors.toSet());
  }

  private Set<Square> getValidAttacks(Piece piece) {
    return piece
        .getPossibleAttackPositions()
        .stream()
        .filter(m -> piece.isAllowed(this, new Action(piece, m, Action.Type.Attack)))
        .collect(Collectors.toSet());
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

  private Piece getKingOfTeam(boolean isTop) {
    return pieces.stream()
        .filter(m -> m.isTop() == isTop && m instanceof King)
        .findFirst().orElse(null);
  }

  /**
   * Creates a shallow copy of the current board.
   * Changes executed to the shallow copy will not interfere with the original instance.
   *
   * @return A shallow copy of the board.
   */
  public Board getShallowCopy() {
    Board shallow = new Board();
    shallow.pieces = new ArrayList<>(pieces)
        .stream()
        .map(Piece::getShallowCopy)
        .collect(Collectors.toList());
    shallow.history = new ArrayList<>(history);
    shallow.turn = turn;
    shallow.promoteAfterAction = promoteAfterAction;
    shallow.promotionIndex = promotionIndex;
    shallow.selected = selected;
    return shallow;
  }

  @Override
  public String toString() {
    char[] board = new char[BOARD_LENGTH * BOARD_LENGTH];
    Arrays.fill(board, '.');
    pieces.forEach(m -> board[m.row() * BOARD_LENGTH + m.col()] = m.toChar());
    return new String(board);
  }

  public static BoardInterface getInstance() {
    return instance;
  }

}
