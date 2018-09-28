package chess.network;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.BoardInterface.Promotion;
import chess.engine.Utils;
import chess.engine.pieces.Square;
import java.io.IOException;
import org.json.JSONObject;

public class ConnectedGame implements Runnable {

  enum ParseResult {
    Correct, MissingInfo, InvalidMove
  }

  private ConnectionManager connectionMgr;
  private volatile Board board;
  private Thread networkThread;

  private volatile String activeJsonBatch;
  private boolean isTopTeam;

  private ConnectedGame(Board board) {
    this.board = board;
  }

  public void connect(String targetAddress) throws IOException {
    connectionMgr = new ConnectionManager();
    connectionMgr.connect(targetAddress);

    decideTurns();

    networkThread = new Thread(this);
    networkThread.start();
  }

  private void decideTurns() {
    isTopTeam = false;
  }

  public void moveMade(Action action, char promotion) {
    activeJsonBatch = actionToJson(action, promotion);
    networkThread.notify();
  }

  public void run() {

    // TODO: Testing

    while (connectionMgr.isConnected()) {

      try {
        String response = "ok";
        String data = connectionMgr.receive();

        System.out.println(data);

        if (data == null) {
          continue;
        }

        switch (parseAndExecuteMove(data)) {
          case Correct:
            break;
          case MissingInfo:
          case InvalidMove:
            response = "invalid";
            break;
          default:
            break;
        }

        JSONObject json = new JSONObject();
        json.append("response", response);
        connectionMgr.send(json.toString());

        do {
          if (board.isTopTurn() == isTopTeam) {
            wait();

            connectionMgr.send(activeJsonBatch);
            activeJsonBatch = "";
          }

          response = connectionMgr.receive();
          if (response != null) {

            if (response.equalsIgnoreCase("ok")) {
              break;
            }

            System.out.println("Invalid");

          }

        } while (response == null || !response.equalsIgnoreCase("ok"));

      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }

    }
  }

  private String actionToJson(Action action, char promotion) {
    JSONObject obj = new JSONObject();
    obj.append("from", Utils.getSourceSquareNotation(action));
    obj.append("to", Utils.getTargetSquareNotation(action));
    obj.append("promotion", promotion);

    return obj.toString();
  }

  private ParseResult parseAndExecuteMove(String json) {
    Board copy = board.getDeepCopy();

    JSONObject obj = new JSONObject(json);
    if (!obj.has("from") || !obj.has("to")) {
      return ParseResult.MissingInfo;
    }

    Square src = Utils.getSquareFromNotation(obj.getString("from"));
    if (src == null || !copy.selectPieceAt(src)) {
      return ParseResult.InvalidMove;
    }

    Square target = Utils.getSquareFromNotation(obj.getString("to"));
    if (target == null || !copy.tryGoTo(target)) {
      return ParseResult.InvalidMove;
    }

    Promotion promType = null;
    if (copy.isPromoting()) {
      Object promotion = obj.get("promotion");
      if (promotion == null || promotion.toString().length() != 1) {
        return ParseResult.InvalidMove;
      }

      promType = Promotion.fromChar(promotion.toString().charAt(0));
      if (!copy.promoteTo(promType)) {
        return ParseResult.InvalidMove;
      }
    }

    board.selectPieceAt(src);
    board.tryGoTo(target);
    if (board.isPromoting()) {
      board.promoteTo(promType);
    }

    return ParseResult.Correct;
  }

}
