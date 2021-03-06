package chess.game.drawables;

import chess.engine.pieces.Piece;
import chess.game.Game;
import chess.game.ResourceManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

public class DrawablePiece {

  private int size = Game.SQUARE_SIZE;

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

  /**
   * Updates the visible information of the piece.
   *
   * @param dt The delta-time. Used to determine velocity.
   */
  public void update(float dt) {
    float horizontalMove = drawX - piece.col() * Game.SQUARE_SIZE;
    float verticalMove = drawY - piece.row() * Game.SQUARE_SIZE;

    float speed = 5; // Pixels per frame.
    if (Math.abs(horizontalMove) > speed * 2 || Math.abs(verticalMove) > speed * 2) {
      float dir = (float) Math.atan2(verticalMove, horizontalMove);
      drawX += -Math.min(speed, Math.abs(horizontalMove)) * dt * Math.cos(dir);
      drawY += -Math.min(speed, Math.abs(verticalMove)) * dt * Math.sin(dir);
    } else {
      drawX = piece.col() * Game.SQUARE_SIZE;
      drawY = piece.row() * Game.SQUARE_SIZE;
    }

  }

  /**
   * Draws the piece.
   *
   * @param g The graphic's object to draw onto.
   */
  public void draw(Graphics2D g) {
    if (piece.getState() == Piece.State.Captured) {
      return;
    }

    int margin = (Game.SQUARE_SIZE - size) / 2;

    if (image == null) {
      g.setColor(piece.isTop() ? Color.ORANGE : Color.PINK);
      g.fillRect((int) drawX + margin, (int) drawY + margin, size, size);
    } else {
      g.drawImage(image,
          (int) drawX + margin,
          (int) drawY + margin,
          size, size, null);
    }

  }

  /**
   * Draws the possible moves of the piece if it's currently selected.
   *
   * @param g The graphic's object to draw onto.
   */
  public void drawPositions(Graphics2D g) {
    if (piece.getState() != Piece.State.Selected) {
      return;
    }

    piece.getPossiblePositions().forEach(m ->
        g.drawImage(
            ResourceManager.getInstance().getImage("moveSqr"),
            m.col() * Game.SQUARE_SIZE,
            m.row() * Game.SQUARE_SIZE,
            Game.SQUARE_SIZE,
            Game.SQUARE_SIZE,
            null)
    );

    piece.getPossibleAttackPositions().forEach(m ->
        g.drawImage(
            ResourceManager.getInstance().getImage("attackSqr"),
            m.col() * Game.SQUARE_SIZE + Game.SQUARE_SIZE / 8,
            m.row() * Game.SQUARE_SIZE + Game.SQUARE_SIZE / 8,
            Game.SQUARE_SIZE * 6 / 8,
            Game.SQUARE_SIZE * 6 / 8,
            null)
    );

  }

  public boolean isSelected() {
    return piece != null && piece.getState() == Piece.State.Selected;
  }

  public boolean requiresReload() {
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
        name = "Pawn";
        break;
      case 'R':
      case 'r':
        name = "Rook";
        break;
      case 'Q':
      case 'q':
        name = "Queen";
        break;
      case 'K':
      case 'k':
        name = "King";
        break;
      case 'N':
      case 'n':
        name = "Knight";
        break;
      case 'B':
      case 'b':
        name = "Bishop";
        break;
      default:
        return;
    }

    image = ResourceManager.getInstance().getImage((piece.isTop() ? "black" : "white") + name);

  }

}
