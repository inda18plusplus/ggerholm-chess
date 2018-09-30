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

    logger.info("Client connected (host = {}).", isHost ? "this" : "other");

    decideTurns();

    Thread networkThread = new Thread(this);
    networkThread.start();
  }

  private void decideTurns() throws IOException, NoSuchAlgorithmException {
    int myChoice = new Random().nextInt(2);
    int opponentChoice;

    if (isHost) {

      logger.debug("Creating seed and hash.");

      String seed = Utils.createSeed(10);
      String hash = myChoice + Utils.hash(seed);

      JSONObject init = new JSONObject();
      init.put("type", "init");
      init.put("hash", hash);
      init.put("seed", "");
      init.put("choice", "");

      connectionMgr.send(init.toString());

      logger.debug("Initial packet sent.");
      logger.debug("Waiting for reply.");

      init = new JSONObject(connectionMgr.receive());
      opponentChoice = Integer.parseInt(init.get("choice").toString());

      logger.debug("Choice-packet received.");

      init.put("type", "init");
      init.put("hash", hash);
      init.put("seed", seed);
      init.put("choice", String.valueOf(myChoice));

      connectionMgr.send(init.toString());
      logger.debug("Seed-packet sent.");

      isTopTeam = myChoice == opponentChoice;
    } else {

      logger.debug("Waiting for init-packet.");

      JSONObject init = new JSONObject(connectionMgr.receive());

      logger.debug("Packet received.");

      String type = init.get("type").toString();
      if (!type.equalsIgnoreCase("init")) {
        logger.debug("Incorrect packet-type ({}).", type);
        connectionMgr.disconnect();
      }

      init.put("choice", String.valueOf(myChoice));
      connectionMgr.send(init.toString());
      logger.debug("Choice sent.");
      logger.debug("Waiting for reply.");

      init = new JSONObject(connectionMgr.receive());
      String seed = init.get("seed").toString();
      opponentChoice = Integer.parseInt(init.get("choice").toString());

      logger.debug("Validating choices.");

      String hash = opponentChoice + Utils.hash(seed);
      if (!hash.equals(init.get("hash").toString())) {
        logger.debug("Packet invalid.");
        connectionMgr.disconnect();
      }

      isTopTeam = myChoice != opponentChoice;
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
          logger.debug("Waiting for opponent's move.");

          String data = connectionMgr.receive();
          if (data == null) {
            logger.debug("No data received.");
            continue;
          }

          String response = "ok";
          switch (parseAndExecuteMove(data)) {
            case Invalid:
              response = "invalid";
              logger.debug("Move is invalid.");
              break;
            default:
              logger.debug("Move is valid and executed.");
              break;
          }

          JSONObject json = Utils.createJson("response");
          json.put("response", response);
          connectionMgr.send(json.toString());
          logger.debug("Response sent.");
        }
        firstMove = false;

        logger.debug("Creating board backup.");
        Board backup = board.getDeepCopy();
        do {
          if (board.isTopTurn() == isTopTeam) {
            logger.debug("Waiting for move.");
            wait();

            connectionMgr.send(activeJsonBatch);
            activeJsonBatch = "";
            logger.debug("Move sent.");
          }

          JSONObject jsonObj = new JSONObject(connectionMgr.receive());
          if (!jsonObj.get("type").toString().equalsIgnoreCase("response")) {
            board.reset(backup);
            logger.warn("No response-packet received. Board restored.");
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
          logger.warn("Response not ok. Board restored.");
        } while (true);

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

  private String actionToJson(Action action, char promotion) {
    promotion = Character.toLowerCase(promotion);
    boolean validPromotion =
        promotion == 'q' || promotion == 'b' || promotion == 'r' || promotion == 'n';

    JSONObject obj = Utils.createJson("move");
    obj.put("from", action.sourceSquare().toString());
    obj.put("to", action.targetSquare().toString());
    obj.put("promotion", validPromotion ? promotion : "");

    return obj.toString();
  }

  private ParseResult parseAndExecuteMove(String data) {
    Board copy = board.getDeepCopy();
    logger.debug("Board backup created.");

    Square src;
    Square target;
    Promotion promType = null;
    boolean isCastling = false;

    try {
      JSONObject jsonObj = new JSONObject(data);
      if (!jsonObj.get("type").toString().equalsIgnoreCase("move")) {
        logger.debug("Invalid packet-type.");
        return ParseResult.Invalid;
      }

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
