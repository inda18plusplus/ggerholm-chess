package chess.game;

import chess.engine.Board;
import chess.engine.BoardInterface;
import chess.game.drawables.DrawablePiece;
import chess.network.ConnectedGame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game extends JFrame implements Runnable {

  public static int SQUARE_SIZE = 80;
  private int windowWidth = 960;
  private int windowHeight = 720;

  private int marginX;
  private int marginY;

  private BoardInterface board;
  private List<DrawablePiece> pieces;

  private Color light = new Color(255, 178, 127);
  private Color dark = new Color(183, 126, 91);

  private ConnectedGame multiPlayer;
  private final Logger logger = LoggerFactory.getLogger(Game.class);

  private Game() {
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }

  private void start() {
    createFrame();

    board = Board.getInstance();
    resetBoard();

    multiPlayer = new ConnectedGame(board.getEngine());

    setupInput();

    Thread thread = new Thread(this);
    thread.start();
  }

  private void resetBoard() {
    board.setupStandardBoard(false);
    pieces = board.getPieces().stream().map(DrawablePiece::new).collect(Collectors.toList());
  }

  private void createFrame() {
    setTitle("Chess");
    setSize(windowWidth, windowHeight);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void setupInput() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);

        if (!multiPlayer.isOurTurn()) {
          return;
        }

        int row = (event.getY() - marginY) / SQUARE_SIZE;
        int col = (event.getX() - marginX) / SQUARE_SIZE;

        if (row < 0 || col < 0) {
          return;
        }

        if (row >= Board.BOARD_LENGTH || col >= Board.BOARD_LENGTH) {
          return;
        }

        if (board.hasSelected()) {
          if (board.tryGoTo(row, col)) {
            char promotion = 0;
            if (board.isPromoting()) {
              promotion = promote();
            }

            multiPlayer.moveMade(board.getLastMove(), promotion);

            return;
          }
        }

        board.selectPieceAt(row, col);
      }

      private char promote() {
        BoardInterface.Promotion promotion = PromotionDialog.queryPiece(rootPane);
        board.promoteTo(promotion);
        return promotion.charCode();
      }
    });

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent event) {
        super.keyReleased(event);

        int key = event.getKeyCode();

        try {
          if (key == KeyEvent.VK_L) {
            String portInput = JOptionPane
                .showInputDialog("Enter a port to listen at or leave empty to use default:").trim();

            int port = -1;
            if (portInput.matches("\\d+")) {
              port = Integer.parseInt(portInput);
            }

            multiPlayer.connect(null, port);
            resetBoard();
          } else if (key == KeyEvent.VK_K) {
            String targetAddress = JOptionPane
                .showInputDialog("Enter a network-local IPv4 address (:port optional):").trim();

            if (targetAddress.isEmpty()) {
              multiPlayer.connect("localhost", -1);
            } else if (targetAddress
                .matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?")) {

              String ip = targetAddress;
              int port = -1;
              if (targetAddress.contains(":")) {
                ip = targetAddress.substring(0, targetAddress.indexOf(":"));
                port = Integer.parseInt(targetAddress.substring(targetAddress.indexOf(":") + 1));
              }

              multiPlayer.connect(ip, port);
            }

            resetBoard();
          }
        } catch (IOException | NoSuchAlgorithmException e) {
          logger.error("Connection failed: {}", e.getLocalizedMessage());
        }

        if (multiPlayer.isOurTurn()) {

          if (key == KeyEvent.VK_Q) {
            if (board.doCastling(true)) {
              multiPlayer.moveMade(board.getLastMove());
            }
          } else if (key == KeyEvent.VK_R) {
            if (board.doCastling(false)) {
              multiPlayer.moveMade(board.getLastMove());
            }
          }

        }

      }
    });

  }

  private void update(float dt) {

    if (pieces.stream().anyMatch(DrawablePiece::requiresReload)) {
      pieces = board.getPieces().stream().map(DrawablePiece::new).collect(Collectors.toList());
    }

    pieces.forEach(m -> m.update(dt));

  }

  private void render(Graphics2D g) {
    if (multiPlayer.isOurTurn()) {
      g.setColor(board.isTopTurn() ? Color.BLACK : Color.WHITE);
    } else {
      g.setColor(Color.GRAY);
    }
    g.fillRect(0, 0, windowWidth, windowHeight);

    int length = Board.BOARD_LENGTH;
    int boardSize = SQUARE_SIZE * length;
    marginX = (windowWidth - boardSize) / 2;
    marginY = (windowHeight - boardSize) / 2;
    g.translate(marginX, marginY);

    for (int i = 0; i < length * length; i++) {
      g.setColor(i % 2 == i / length % 2 ? light : dark);
      g.fillRect(i % length * SQUARE_SIZE, i / length * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    pieces.forEach(m -> m.draw(g));
    pieces.stream().filter(DrawablePiece::isSelected).forEach(m -> m.drawPositions(g));

    g.setColor(board.isTopTurn() ? Color.WHITE : Color.BLACK);
    g.drawRect(0, 0, boardSize, boardSize);

    String turn = board.isTopTurn() ? "Black's turn" : "White's turn";
    g.drawString(turn, -marginX * 0.75f, 50);
    g.drawString(board.getGameState().name(), -marginX * 0.75f, 75);

  }

  private void buffer() {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }

    Graphics2D g2d = null;
    do {
      try {
        g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
        render(g2d);
      } finally {

        if (g2d != null) {
          g2d.dispose();
        }

      }
      bufferStrategy.show();
    } while (bufferStrategy.contentsLost());

  }

  @Override
  public void run() {
    long lastLoopTime = System.nanoTime();
    int targetFps = 60;
    long optimalTime = 1000000000 / targetFps;

    while (isVisible()) {
      long now = System.nanoTime();
      long updateLength = now - lastLoopTime;
      lastLoopTime = now;
      float delta = updateLength / (float) optimalTime;

      update(delta);
      buffer();

      try {
        Thread.sleep(Math.max(0, lastLoopTime - System.nanoTime() + optimalTime) / 1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(1);
      }

    }

  }

}
