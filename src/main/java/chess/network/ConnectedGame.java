package chess.network;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.BoardInterface.Promotion;
import chess.engine.Utils;
import chess.engine.pieces.King;
import chess.engine.pieces.Square;
import java.io.IOException;
import org.json.JSONObject;

public class ConnectedGame implements Runnable {

  enum ParseResult {
    Correct, InvalidMove
  }

  private ConnectionManager connectionMgr;
  private volatile Board board;

  private volatile String activeJsonBatch;
  private boolean isTopTeam;
  private boolean isHost;

  public ConnectedGame(Board board) {
    this.board = board;
    connectionMgr = new ConnectionManager();
  }

  /**
   * Connect to the given IPv4 address.
   *
   * @param targetAddress The address given in IPv4 format.
   * @throws IOException If the connection failed.
   */
  public void connect(String targetAddress) throws IOException {
    if (targetAddress == null) {
      connectionMgr.listenForConnections();
      isHost = true;
    } else {
      connectionMgr.connectToHost(targetAddress);
      isHost = false;
    }

    decideTurns();

    Thread networkThread = new Thread(this);
    networkThread.start();
  }

  private void decideTurns() throws IOException {
    // TODO: Implement correctly
    if (isHost) {
      String acceptGame = connectionMgr.receive();
      if (!acceptGame.equalsIgnoreCase("yes")) {
        connectionMgr.listenForConnections();
        return;
      }

      connectionMgr.send("0");
      isTopTeam = false;
    } else {
      connectionMgr.send("yes");

      String hash = connectionMgr.receive();
      if (hash.equalsIgnoreCase("0")) {
        isTopTeam = true;
      }
    }
  }

  /**
   * Returns whether or not it's this client's turn to make a move.
   *
   * @return True or false.
   */
  public boolean isOurTurn() {
    if (!connectionMgr.isConnected()) {
      return true;
    }

    return board.isTopTurn() == isTopTeam;
  }

  /**
   * Call when a move has been made to has to be transmitted to the opponent.
   *
   * @param action The action that was executed.
   * @param promotion The promotion that took place, if any.
   */
  public synchronized void moveMade(Action action, char promotion) {
    activeJsonBatch = actionToJson(action, promotion);
    notifyAll();
  }

  public synchronized void moveMade(Action action) {
    moveMade(action, (char) 0);
  }

  /**
   * Runs the network utilities.
   */
  public synchronized void run() {
    boolean firstMove = isOurTurn();

    while (connectionMgr.isConnected()) {

      try {
        String response = "ok";

        if (!firstMove) {

          String data = connectionMgr.receive();
          if (data == null) {
            continue;
          }

          switch (parseAndExecuteMove(data)) {
            case InvalidMove:
              response = "invalid";
              break;
            default:
              break;
          }

          JSONObject json = new JSONObject();
          json.put("response", response);
          connectionMgr.send(json.toString());
        }
        firstMove = false;

        do {
          if (board.isTopTurn() == isTopTeam) {
            wait();

            connectionMgr.send(activeJsonBatch);
            activeJsonBatch = "";

          }

          String in = connectionMgr.receive();
          response = new JSONObject(in).get("response").toString();
          if (response != null) {

            if (response.equalsIgnoreCase("ok")) {
              break;
            }

          }

        } while (response == null || !response.equalsIgnoreCase("ok"));

      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }

    }
  }

  private String actionToJson(Action action, char promotion) {
    JSONObject obj = new JSONObject();
    obj.put("from", Utils.getSourceSquareNotation(action));
    obj.put("to", Utils.getTargetSquareNotation(action));
    obj.put("promotion", promotion > 0 ? promotion : "");

    return obj.toString();
  }

  private ParseResult parseAndExecuteMove(String data) {
    Board copy = board.getDeepCopy();

    Square src;
    Square target;
    Promotion promType = null;
    boolean isCastling = false;
    try {
      JSONObject json = new JSONObject(data);

      src = Utils.getSquareFromNotation(json.getString("from"));
      target = Utils.getSquareFromNotation(json.getString("to"));

      if (src == null || target == null || !copy.selectPieceAt(src)) {
        return ParseResult.InvalidMove;
      }

      if (copy.getAt(src.row(), src.col()) instanceof King
          && Math.abs(src.col() - target.col()) == 2) {

        if (!copy.doCastling(src.col() > target.col())) {
          return ParseResult.InvalidMove;
        }

        isCastling = true;

      } else if (!copy.tryGoTo(target)) {
        return ParseResult.InvalidMove;
      }

      if (copy.isPromoting()) {
        Object promotion = json.get("promotion");

        promType = Promotion.fromChar(promotion.toString().charAt(0));
        if (!copy.promoteTo(promType)) {
          return ParseResult.InvalidMove;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ParseResult.InvalidMove;
    }

    board.selectPieceAt(src);
    if (isCastling) {
      board.doCastling(src.col() > target.col());
    } else {
      board.tryGoTo(target);
    }

    if (board.isPromoting()) {
      board.promoteTo(promType);
    }

    return ParseResult.Correct;
  }

}
