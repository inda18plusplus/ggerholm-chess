package chess;

import chess.engine.Board;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.List;

public class Game extends JFrame implements Runnable {

  static final int PIECE_SIZE = 80;

  private int windowWidth = 720;
  private int windowHeight = 720;

  private Board board;
  private List<DrawablePiece> pieces;

  private Game() {
  }

  private void start() {
    createFrame();

    board = Board.getInstance().getEngine();
    board.setupStandardBoard();
    pieces = board.getDrawables();

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

  private void update(float dt) {

    pieces.forEach(m -> m.update(dt));

  }

  private void render(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, windowWidth, windowHeight);

    int boardSize = PIECE_SIZE * Board.GAME_SIZE;
    g.translate((windowWidth - boardSize) / 2, (windowHeight - boardSize) / 2);

    int gs = Board.GAME_SIZE;
    for (int i = 0; i < gs * gs; i++) {
      g.setColor(i % 2 == i / gs % 2 ? Color.WHITE : Color.BLACK);
      g.fillRect(i % gs * PIECE_SIZE, i / gs * PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
    }

    pieces.forEach(m -> m.draw(g));

    g.setColor(Color.BLACK);
    g.drawRect(0, 0, boardSize, boardSize);

  }

  private void buffer() {
    BufferStrategy bs = getBufferStrategy();
    if (bs == null) {
      createBufferStrategy(3);
      return;
    }

    Graphics2D g2d = null;
    do {
      try {
        g2d = (Graphics2D) bs.getDrawGraphics();
        render(g2d);
      } finally {

        if (g2d != null) {
          g2d.dispose();
        }

      }
      bs.show();
    } while (bs.contentsLost());

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

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }

}
