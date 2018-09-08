package chess;

import java.util.function.Consumer;

public final class Utils {

  public static void tryPutAt(Consumer<Integer> consumer, int i) {
    try {
      consumer.accept(i);
    } catch (ArrayIndexOutOfBoundsException ignored) {
    }
  }

}
