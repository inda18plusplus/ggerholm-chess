package chess;

import chess.engine.pieces.Piece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

public class DrawablePiece {

  static final int SIZE = 80;

  private float drawX;
  private float drawY;
  private Image image;
  private Piece piece;

  /**
   * Creates the variables and methods required in order to render a piece.
   *
   * @param piece The piece to be rendered.
   */
  public DrawablePiece(Piece piece) {
    this.piece = piece;
    drawX = piece.col() * SIZE;
    drawY = piece.row() * SIZE;

    // TODO: Load images
  }

  void update(float dt) {
    float horizontalMove = drawX - piece.col() * SIZE;
    float verticalMove = drawY - piece.row() * SIZE;

    if (Math.abs(horizontalMove) > 1 || Math.abs(verticalMove) > 1) {
      float dir = (float) Math.atan2(verticalMove, horizontalMove);
      float speed = 5; // Pixels per frame.
      drawX += -Math.min(speed, Math.abs(horizontalMove)) * dt * Math.cos(dir);
      drawY += -Math.min(speed, Math.abs(verticalMove)) * dt * Math.sin(dir);
    }

  }

  void draw(Graphics2D g) {
    if (image == null) {
      g.setColor(piece.isTop() ? Color.BLUE : Color.RED);
      g.fillRect((int) drawX + SIZE / 4, (int) drawY + SIZE / 4, SIZE, SIZE);
      return;
    }

    g.drawImage(image, (int) drawX, (int) drawY, SIZE, SIZE, null);

  }

}
