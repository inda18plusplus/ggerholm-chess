package chess.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ResourceManager {

  private static ResourceManager instance = new ResourceManager();

  private Map<String, BufferedImage> images = new HashMap<>();

  private ResourceManager() {
    String[] names = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};
    for (String name : names) {
      loadImage("black" + name, "/images/black" + name + ".png");
      loadImage("white" + name, "/images/white" + name + ".png");
    }

    loadImage("moveSqr", "/images/greenSquare.png");
    loadImage("attackSqr", "/images/redSquare.png");
  }

  public static ResourceManager getInstance() {
    return instance;
  }

  private void loadImage(String key, String path) {
    try {
      BufferedImage image = ImageIO.read(getClass().getResource(path));
      images.put(key, image);
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public BufferedImage getImage(String key) {
    return images.get(key);
  }

}
