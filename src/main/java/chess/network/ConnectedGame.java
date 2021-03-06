package chess.network;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.BoardInterface.Promotion;
import chess.engine.pieces.King;
import chess.engine.pieces.Square;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectedGame implements Runnable {

  enum ParseResult {
    Correct, Invalid
  }

  private ConnectionManager connectionMgr;
  private volatile Board board;

  private volatile String activeJsonBatch;
  private boolean isTopTeam;
  private boolean isHost;

  private final Logger logger = LoggerFactory.getLogger(ConnectedGame.class);

  public ConnectedGame(Board board) {
    this.board = board;
    connectionMgr = new ConnectionManager();
  }

  /**
   * Connect to the given address and port or listen at the provided port if the address provided is
   * null.
   *
   * @param ip The address given in IPv4 format or null to listen for connections instead.
   * @param port The port to connect to or listen at.
   * @throws IOException If the connection failed.
   */
  public void connect(String ip, int port) throws IOException, NoSuchAlgorithmException {
    if (ip == null) {
      connectionMgr.listenForConnections(port);
      isHost = true;
    } else {
      connectionMgr.connectToHost(ip, port);
      isHost = false;
    }

    logger.info("Client connected (host = {}).", isHost ? "this" : "other");

    decideTurns();

    Thread networkThread = new Thread(this);
    networkThread.start();
  }

  private void decideTurns() throws IOException, NoSuchAlgorithmException {
    int myChoice = new SecureRandom().nextInt(2);
    int opponentChoice;

    if (isHost) {

      logger.debug("Creating seed and hash.");

      String seed = myChoice + Utils.createSeed(10);
      String hash = Utils.hash(seed);

      JSONObject init = Utils.createJson("init");
      init.put("hash", hash);
      init.put("seed", "");
      init.put("choice", "");
      connectionMgr.send(init.toString());

      logger.debug("Initial packet sent.");

      init = connectionMgr.receiveInitMessage();
      logger.debug("Packet received.");
      if (init == null) {
        logger.debug("No init-packet received.");
        connectionMgr.disconnect();
        return;
      }

      opponentChoice = Integer.parseInt(init.get("choice").toString());
      if (opponentChoice != 0 && opponentChoice != 1) {
        logger.debug("Opponent cheated during initialization.");
        connectionMgr.disconnect();
      }

      init.put("type", "init");
      init.put("hash", hash);
      init.put("seed", seed);
      init.put("choice", myChoice);
      connectionMgr.send(init.toString());
      logger.debug("Seed-packet sent.");

      isTopTeam = myChoice != opponentChoice;
    } else {
      JSONObject init = connectionMgr.receiveInitMessage();
      logger.debug("Packet received.");
      if (init == null) {
        logger.debug("No init-packet received.");
        connectionMgr.disconnect();
        return;
      }

      final String originalHash = init.get("hash").toString();
      init.put("choice", myChoice);
      connectionMgr.send(init.toString());
      logger.debug("Choice sent.");
      logger.debug("Waiting for reply.");

      init = connectionMgr.receiveInitMessage();
      if (init == null) {
        logger.debug("No init-packet received.");
        connectionMgr.disconnect();
        return;
      }

      opponentChoice = Integer.parseInt(init.get("choice").toString());

      if (opponentChoice != 0 && opponentChoice != 1) {
        logger.debug("Opponent cheated during initialization.");
        connectionMgr.disconnect();
      }

      logger.debug("Validating choices.");
      String seed = init.get("seed").toString();
      String hash = Utils.hash(opponentChoice + seed.substring(1));
      if (!hash.equals(originalHash)) {
        logger.debug("Opponent cheated during initialization.");
        connectionMgr.disconnect();
      }

      isTopTeam = myChoice == opponentChoice;
    }

    logger.debug("Team decided (isTopTeam = {}).", isTopTeam);
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
    if (!connectionMgr.isConnected()) {
      return;
    }

    logger.debug("Converting move to JSON.");
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

    logger.debug("Network thread started.");

    while (connectionMgr.isConnected()) {
      try {
        if (!firstMove) {
          if (!receiveMove()) {
            continue;
          }
        }
        firstMove = false;
        makeMoveAndSend();

      } catch (InterruptedException | IOException e) {
        try {
          logger.warn("Exception occurred: {}", e.getLocalizedMessage());
          connectionMgr.disconnect();
        } catch (IOException disconnectFail) {
          logger.error("Disconnection failed: {}", disconnectFail.getLocalizedMessage());
        }
        break;
      }
    }
  }

  private boolean receiveMove() throws IOException {
    JSONObject jsonObj = connectionMgr.receiveMove();
    if (jsonObj == null) {
      logger.debug("No move received.");
      return false;
    }

    String response = "ok";
    switch (applyMove(jsonObj)) {
      case Invalid:
        response = "invalid";
        logger.debug("Move is invalid.");
        break;
      default:
        logger.debug("Move is valid and executed.");
        break;
    }

    jsonObj = Utils.createJson("response");
    jsonObj.put("response", response);
    connectionMgr.send(jsonObj.toString());
    return true;
  }

  private void makeMoveAndSend() throws InterruptedException, IOException {
    logger.debug("Creating board backup.");
    Board backup = board.getDeepCopy();
    do {
      if (board.isTopTurn() == isTopTeam) {
        logger.debug("Waiting for our move.");
        wait();

        connectionMgr.send(activeJsonBatch);
        activeJsonBatch = "";
      }

      JSONObject jsonObj = connectionMgr.receiveResponse();
      if (jsonObj == null) {
        board.reset(backup);
        logger.warn("Incorrect packet-type. Board restored.");
        continue;
      }

      String response = jsonObj.get("response").toString();
      if (response != null) {
        if (response.equalsIgnoreCase("ok")) {
          logger.info("Move successfully sent.");
          break;
        }
      }

      board.reset(backup);
      logger.warn("Move was deemed invalid by opponent. Board restored.");
    } while (true);
  }

  private String actionToJson(Action action, char promotion) {
    promotion = Character.toLowerCase(promotion);
    boolean validPromotion =
        promotion == 'q' || promotion == 'b' || promotion == 'r' || promotion == 'n';

    JSONObject obj = Utils.createJson("move");
    obj.put("from", action.sourceSquare().toString());
    obj.put("to", action.targetSquare().toString());
    obj.put("promotion", validPromotion ? String.valueOf(promotion).toUpperCase() : "");

    return obj.toString();
  }

  private ParseResult applyMove(JSONObject jsonObj) {
    Board copy = board.getDeepCopy();
    logger.debug("Board backup created.");

    Square src;
    Square target;
    Promotion promType = null;
    boolean isCastling = false;

    // Parse and apply the move to a temporary board.

    try {
      src = Square.of(jsonObj.getString("from"));
      target = Square.of(jsonObj.getString("to"));

      if (src == null || target == null) {
        logger.debug("Invalid parameters detected (to, from).");
        return ParseResult.Invalid;
      }

      if (!copy.selectPieceAt(src)) {
        logger.debug("Piece selection failed.");
        return ParseResult.Invalid;
      }

      if (copy.getAt(src.row(), src.col()) instanceof King
          && Math.abs(src.col() - target.col()) == 2) {

        logger.debug("Castling detected.");

        if (!copy.doCastling(src.col() > target.col())) {
          logger.warn("Castling move failed.");
          return ParseResult.Invalid;
        }

        isCastling = true;

      } else if (!copy.tryGoTo(target)) {
        logger.debug("Move failed.");
        return ParseResult.Invalid;
      }

      if (copy.isPromoting()) {
        logger.debug("Promotion detected.");
        Object promotion = jsonObj.get("promotion");

        promType = Promotion.fromChar(promotion.toString().charAt(0));
        if (!copy.promoteTo(promType)) {
          logger.warn("Promotion failed");
          return ParseResult.Invalid;
        }
      }
    } catch (Exception e) {
      logger.warn("Exception occurred during JSON-parsing: {}", e.getLocalizedMessage());
      return ParseResult.Invalid;
    }

    // Apply the move to the main board if it was successful.

    board.selectPieceAt(src);
    if (isCastling) {
      board.doCastling(src.col() > target.col());
    } else {
      board.tryGoTo(target);
    }

    if (board.isPromoting()) {
      board.promoteTo(promType);
    }

    logger.debug("Move executed successfully.");

    return ParseResult.Correct;
  }

}
