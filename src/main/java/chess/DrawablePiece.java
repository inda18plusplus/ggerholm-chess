package chess;

import chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

public class DrawablePiece {

  private int size = Game.SQUARE_SIZE / 2;

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
    drawX = piece.col() * Game.SQUARE_SIZE;
    drawY = piece.row() * Game.SQUARE_SIZE;

    loadImage();
  }

  void update(float dt) {
    float horizontalMove = drawX - piece.col() * Game.SQUARE_SIZE;
    float verticalMove = drawY - piece.row() * Game.SQUARE_SIZE;

    if (Math.abs(horizontalMove) > 1 || Math.abs(verticalMove) > 1) {
      float dir = (float) Math.atan2(verticalMove, horizontalMove);
      float speed = 5; // Pixels per frame.
      drawX += -Math.min(speed, Math.abs(horizontalMove)) * dt * Math.cos(dir);
      drawY += -Math.min(speed, Math.abs(verticalMove)) * dt * Math.sin(dir);
    }

  }

  void draw(Graphics2D g) {
    if (piece.getState() == Piece.State.Captured) {
      return;
    }

    if (image == null) {
      g.setColor(piece.isTop() ? Color.ORANGE : Color.PINK);
      g.fillRect((int) drawX + size / 2, (int) drawY + size / 2, size, size);
    } else {
      g.drawImage(image, (int) drawX, (int) drawY, size, size, null);
    }

  }

  void drawPositions(Graphics2D g) {
    if (piece.getState() != Piece.State.Selected) {
      return;
    }

    g.setColor(Color.GREEN);
    piece.getPossiblePositions().forEach(m -> g.fillOval(
        m.col() * Game.SQUARE_SIZE + Game.SQUARE_SIZE * 3 / 8,
        m.row() * Game.SQUARE_SIZE + Game.SQUARE_SIZE * 3 / 8,
        Game.SQUARE_SIZE / 4,
        Game.SQUARE_SIZE / 4
    ));

    g.setColor(Color.RED);
    piece.getPossibleAttackPositions().forEach(m -> g.fillRect(
        m.col() * Game.SQUARE_SIZE + Game.SQUARE_SIZE * 3 / 8,
        m.row() * Game.SQUARE_SIZE + Game.SQUARE_SIZE * 3 / 8,
        Game.SQUARE_SIZE / 4,
        Game.SQUARE_SIZE / 4
    ));
  }

  boolean isSelected() {
    return piece != null && piece.getState() == Piece.State.Selected;
  }

  boolean requiresReload() {
    return piece != null && piece.getState() == Piece.State.Promoted;
  }

  private void loadImage() {
    if (piece == null) {
      return;
    }

    String name;
    switch (piece.toChar()) {
      case 'P':
      case 'p':
        name = "pawn";
        break;
      case 'R':
      case 'r':
        name = "rook";
        break;
      case 'Q':
      case 'q':
        name = "queen";
        break;
      case 'K':
      case 'k':
        name = "king";
        break;
      case 'H':
      case 'h':
        name = "knight";
        break;
      case 'B':
      case 'b':
        name = "bishop";
        break;
      default:
        return;
    }

    try {
      image = ImageIO.read(getClass().getResource(name + ".png"));
    } catch (IOException | IllegalArgumentException ignored) {
      // TODO: Log
    }

  }

}
