package chess.game;

import chess.engine.Board;
import chess.engine.BoardInterface;
import chess.game.drawables.DrawablePiece;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFrame;

public class Game extends JFrame implements Runnable {

  public static int SQUARE_SIZE = 80;
  private int windowWidth = 960;
  private int windowHeight = 720;

  private int marginX;
  private int marginY;

  private BoardInterface board;
  private List<DrawablePiece> pieces;

  private Game() {
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }

  private void start() {
    createFrame();

    board = Board.getInstance();
    board.setupStandardBoard(false);
    pieces = board.getPieces().stream().map(DrawablePiece::new).collect(Collectors.toList());

    setupInput();

    Thread thread = new Thread(this);
    thread.start();
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
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        int row = (e.getY() - marginY) / SQUARE_SIZE;
        int col = (e.getX() - marginX) / SQUARE_SIZE;

        if (row < 0 || col < 0) {
          return;
        }

        if (row >= Board.BOARD_LENGTH || col >= Board.BOARD_LENGTH) {
          return;
        }

        if (board.hasSelected()) {
          if (board.tryGoTo(row, col)) {
            if (board.isPromoting()) {
              this.promote();
            }

            return;
          }
        }

        board.selectPieceAt(row, col);

      }

      private void promote() {
        BoardInterface.Promotion promotion = PromotionDialog.queryPiece(rootPane);

        board.promoteTo(promotion);
      }
    });

    // TODO: Use the castling method on the board
  }

  private void update(float dt) {

    if (pieces.stream().anyMatch(DrawablePiece::requiresReload)) {
      pieces = board.getPieces().stream().map(DrawablePiece::new).collect(Collectors.toList());
    }

    pieces.forEach(m -> m.update(dt));

  }

  private Color light = new Color(255, 178, 127);
  private Color dark = new Color(183, 126, 91);

  private void render(Graphics2D g) {
    g.setColor(board.isTopTurn() ? Color.BLACK : Color.WHITE);
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
