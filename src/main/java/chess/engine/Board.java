package chess.engine;

import chess.engine.pieces.Bishop;
import chess.engine.pieces.King;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Rook;
import chess.engine.pieces.Square;
import chess.engine.rules.Rule;
import chess.game.drawables.DrawablePiece;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Board implements BoardInterface {

  public static final int BOARD_LENGTH = 8;

  private static BoardInterface instance = new Board();

  private boolean promoteAfterAction;
  private int promotionIndex = -1;
  private int turn;

  private Piece selected;
  private State gameState;
  private GameType gameType;

  private List<Piece> pieces = new ArrayList<>();
  private List<Action> history = new ArrayList<>();

  private Board() {
  }

  public static BoardInterface getInstance() {
    return instance;
  }

  @Override
  public Board getEngine() {
    return this;
  }

  @Override
  public void setupStandardBoard(boolean topFirst) {
    setupEmptyBoard(topFirst);
    gameType = GameType.Standard;

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
  public void setupFischerBoard(boolean topFirst) {
    setupEmptyBoard(topFirst);
    gameType = GameType.Fischer;

    Random rand = new Random();

    int firstBishop = rand.nextInt(BOARD_LENGTH);
    int secondBishop = rand.nextInt(BOARD_LENGTH);
    while (secondBishop % 2 == firstBishop % 2) {
      secondBishop = rand.nextInt(BOARD_LENGTH);
    }

    pieces.add(new Bishop(7, firstBishop, false));
    pieces.add(new Bishop(7, secondBishop, false));

    Set<Integer> setup = new HashSet<>();
    setup.add(firstBishop);
    setup.add(secondBishop);

    Supplier<Integer> get = () -> {
      int i = rand.nextInt(BOARD_LENGTH);
      while (setup.contains(i)) {
        i = rand.nextInt(BOARD_LENGTH);
      }
      return i;
    };

    int queen = get.get();
    pieces.add(new Queen(7, queen, false));
    setup.add(queen);

    int knight = get.get();
    pieces.add(new Knight(7, knight, false));
    setup.add(knight);

    knight = get.get();
    pieces.add(new Knight(7, knight, false));
    setup.add(knight);

    int[] a = IntStream.range(0, 8).filter(m -> !setup.contains(m)).sorted().toArray();

    pieces.add(new Rook(7, a[0], false));
    pieces.add(new Rook(7, a[2], false));
    pieces.add(new King(7, a[1], false));

    for (int i = 0; i < BOARD_LENGTH; i++) {
      Piece piece = pieces.get(i).getDeepCopy();
      piece.setTeam(true);
      piece.moveTo(0, piece.col());
      pieces.add(piece);

      pieces.add(new Pawn(1, i, true));
      pieces.add(new Pawn(6, i, false));
    }


  }

  @Override
  public void setupEmptyBoard(boolean topFirst) {
    pieces.clear();
    history.clear();
    turn = topFirst ? 0 : 1;
    selected = null;
    gameType = GameType.Standard;
  }

  @Override
  public GameType getGameType() {
    return gameType;
  }

  @Override
  public boolean selectPieceAt(int row, int col) {
    if (isPromoting()) {
      return false;
    }

    clearSelected();
    selected = getAt(row, col);

    if (selected != null && isTopTurn() != selected.isTop()) {
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
  public boolean hasSelected() {
    return selected != null;
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

    List<Piece> attackers = getEnemyPieces(isTop)
        .stream()
        .filter(m -> m.isAllowed(this, new Action(m, king.pos(), Action.Type.Attack)))
        .collect(Collectors.toList());

    if (attackers
        .stream()
        .allMatch(m -> isSquareUnderAttack(m.row(), m.col(), m.isTop(), m instanceof Pawn))) {
      return false;
    }

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
  public boolean isGameADraw() {
    if (gameType != GameType.Standard) {
      return false;
    }

    if (pieces.stream().allMatch(m -> m instanceof King)) {
      return true;
    }

    if (pieces.size() == 3) {
      if (pieces.stream().filter(m -> m instanceof King).count() == 2
          && pieces.stream().anyMatch(m -> m instanceof Bishop || m instanceof Knight)) {
        return true;
      }
    }

    if (pieces.size() == 4) {
      if (getEnemyPieces(true)
          .stream()
          .filter(m -> m instanceof King || m instanceof Bishop)
          .anyMatch(m -> m instanceof Bishop)) {
        if (getFriendlyPieces(true)
            .stream()
            .filter(m -> m instanceof King || m instanceof Bishop)
            .anyMatch(m -> m instanceof Bishop)) {

          return pieces
              .stream()
              .filter(m -> m instanceof Bishop)
              .allMatch(m -> m.row() % 2 == m.col() % 2)
              || pieces
              .stream()
              .filter(m -> m instanceof Bishop)
              .allMatch(m -> m.row() % 2 != m.col() % 2);
        }
      }
    }

    return false;
  }

  @Override
  public State getGameState() {
    if (gameState != null) {
      if (gameState.turn == turn) {
        return gameState;
      }
    }

    if (isGameADraw()) {
      gameState = State.Draw;
    } else if (isKingInCheck(isTopTurn())) {
      if (isTeamInCheckmate(isTopTurn())) {
        gameState = State.Checkmate;
      } else {
        gameState = State.Check;
      }
    } else if (isTeamInStalemate(isTopTurn())) {
      gameState = State.Stalemate;
    } else {
      gameState = State.Normal;
    }

    gameState.turn = turn;
    return gameState;
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
      promoted.setState(Piece.State.Alive);

      pieces.remove(promotionIndex);
      pieces.add(promotionIndex, promoted);
      promotionIndex = -1;

    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      e.printStackTrace();
      System.exit(1);
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

  @Override
  public List<DrawablePiece> getDrawables() {
    return pieces.stream().map(DrawablePiece::new).collect(Collectors.toList());
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
   * @param row The targeted row.
   * @param col The targeted column.
   */
  public void forceKill(Piece attacker, int row, int col) {

    if (capturePiecesIf(m -> m.isAt(row, col))) {
      takeAction(new Action(attacker, row, col, Action.Type.Attack), true, 0);
    }

  }

  /**
   * Forces the unit (if any) on the provided square to be moved and does not increase the turn.
   * Also removes any unit on the targeted square.
   *
   * @param fromRow The original row.
   * @param fromCol The original column.
   * @param toRow The new row.
   * @param toCol The new column.
   */
  public void forceMove(int fromRow, int fromCol, int toRow, int toCol) {
    pieces.stream().filter(m -> m.isAt(fromRow, fromCol)).findAny().ifPresent(m -> {

      capturePiecesIf(n -> n.isAt(toRow, toCol));

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

  private boolean moveTo(int row, int col) {
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
    Action action = new Action(selected, row, col, Action.Type.Attack);
    action.insertAct(true,
        () -> capturePiecesIf(m -> m.isTop() != isTopTurn() && m.isAt(row, col)));
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
   * @param row The row number.
   * @param col The column number.
   * @param isTop Assumes the square has a unit from this team (top if true, bottom if false).
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
          promoteAfterAction = false;
          break;
        }
      }
    }

    return true;
  }

  /**
   * Returns all previously executed actions.
   *
   * @return An unmodifiable list of Action-objects.
   */
  public List<Action> getHistory() {
    return Collections.unmodifiableList(history);
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

  public Piece getRook(boolean isTop, boolean left) {
    return pieces
        .stream()
        .filter(m -> m.isTop() == isTop && m instanceof Rook)
        .sorted(Comparator.comparingInt(Piece::col))
        .skip(left ? 0 : 1)
        .findFirst()
        .orElse(null);
  }

  private List<Piece> getEnemyPieces(boolean isTop) {
    return pieces.stream().filter(m -> m.isTop() != isTop).collect(Collectors.toList());
  }

  private List<Piece> getFriendlyPieces(boolean isTop) {
    return getEnemyPieces(!isTop);
  }

  private Piece getKingOfTeam(boolean isTop) {
    return pieces.stream()
        .filter(m -> m.isTop() == isTop && m instanceof King)
        .findFirst().orElse(null);
  }

  private boolean capturePiecesIf(Predicate<Piece> condition) {
    return pieces.removeIf(m -> {
      if (condition.test(m)) {
        m.setState(Piece.State.Captured);
        return true;
      }
      return false;
    });
  }

  /**
   * Creates a deep copy of the current board. Changes executed to the deep copy will not interfere
   * with the original instance.
   *
   * @return A deep copy of the board.
   */
  public Board getDeepCopy() {
    Board copy = new Board();
    copy.pieces = new ArrayList<>(pieces)
        .stream()
        .map(Piece::getDeepCopy)
        .collect(Collectors.toList());
    copy.history = new ArrayList<>(history);
    copy.turn = turn;
    copy.promoteAfterAction = promoteAfterAction;
    copy.promotionIndex = promotionIndex;
    copy.selected = selected;
    return copy;
  }

  @Override
  public String toString() {
    char[] board = new char[BOARD_LENGTH * BOARD_LENGTH];
    Arrays.fill(board, '.');
    pieces.forEach(m -> board[m.row() * BOARD_LENGTH + m.col()] = m.toChar());
    return new String(board);
  }

}
