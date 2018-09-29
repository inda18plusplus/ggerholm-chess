package chess.network;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.BoardInterface.Promotion;
import chess.engine.pieces.King;
import chess.engine.pieces.Square;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.json.JSONObject;

public class ConnectedGame implements Runnable {

  enum ParseResult {
    Correct, Invalid
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
  public void connect(String targetAddress) throws IOException, NoSuchAlgorithmException {
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

  private void decideTurns() throws IOException, NoSuchAlgorithmException {
    int myChoice = new Random().nextInt(2);
    int opponentChoice;

    if (isHost) {

      String seed = Utils.createSeed(10);
      String hash = myChoice + Utils.hash(seed);

      JSONObject init = new JSONObject();
      init.put("type", "init");
      init.put("hash", hash);
      init.put("seed", "");
      init.put("choice", "");

      connectionMgr.send(init.toString());

      init = new JSONObject(connectionMgr.receive());
      opponentChoice = Integer.parseInt(init.get("choice").toString());

      init.put("type", "init");
      init.put("hash", hash);
      init.put("seed", seed);
      init.put("choice", String.valueOf(myChoice));

      connectionMgr.send(init.toString());

      isTopTeam = myChoice == opponentChoice;
    } else {

      JSONObject init = new JSONObject(connectionMgr.receive());
      String type = init.get("type").toString();
      if (!type.equalsIgnoreCase("init")) {
        connectionMgr.disconnect();
      }
      init.put("choice", String.valueOf(myChoice));

      connectionMgr.send(init.toString());

      init = new JSONObject(connectionMgr.receive());
      String seed = init.get("seed").toString();
      opponentChoice = Integer.parseInt(init.get("choice").toString());

      String hash = opponentChoice + Utils.hash(seed);
      if (!hash.equals(init.get("hash").toString())) {
        connectionMgr.disconnect();
      }

      isTopTeam = myChoice != opponentChoice;
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

        if (!firstMove) {

          String data = connectionMgr.receive();
          if (data == null) {
            continue;
          }

          String response = "ok";
          switch (parseAndExecuteMove(data)) {
            case Invalid:
              response = "invalid";
              break;
            default:
              break;
          }

          JSONObject json = Utils.createJson("response");
          json.put("response", response);
          connectionMgr.send(json.toString());
        }
        firstMove = false;

        Board backup = board.getDeepCopy();
        do {
          if (board.isTopTurn() == isTopTeam) {
            wait();

            connectionMgr.send(activeJsonBatch);
            activeJsonBatch = "";
          }

          JSONObject jsonObj = new JSONObject(connectionMgr.receive());
          if (!jsonObj.get("type").toString().equalsIgnoreCase("response")) {
            board.reset(backup);
            continue;
          }

          String response = jsonObj.get("response").toString();
          if (response != null) {

            if (response.equalsIgnoreCase("ok")) {
              break;
            }
          }

          board.reset(backup);
        } while (true);

      } catch (InterruptedException | IOException ignored) {
        try {
          connectionMgr.disconnect();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      }

    }
  }

  private String actionToJson(Action action, char promotion) {
    JSONObject obj = Utils.createJson("move");
    obj.put("from", action.sourceSquare().toString());
    obj.put("to", action.targetSquare().toString());
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
      JSONObject jsonObj = new JSONObject(data);
      if (!jsonObj.get("type").toString().equalsIgnoreCase("move")) {
        return ParseResult.Invalid;
      }

      src = Square.of(jsonObj.getString("from"));
      target = Square.of(jsonObj.getString("to"));

      if (src == null || target == null || !copy.selectPieceAt(src)) {
        return ParseResult.Invalid;
      }

      if (copy.getAt(src.row(), src.col()) instanceof King
          && Math.abs(src.col() - target.col()) == 2) {

        if (!copy.doCastling(src.col() > target.col())) {
          return ParseResult.Invalid;
        }

        isCastling = true;

      } else if (!copy.tryGoTo(target)) {
        return ParseResult.Invalid;
      }

      if (copy.isPromoting()) {
        Object promotion = jsonObj.get("promotion");

        promType = Promotion.fromChar(promotion.toString().charAt(0));
        if (!copy.promoteTo(promType)) {
          return ParseResult.Invalid;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ParseResult.Invalid;
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
