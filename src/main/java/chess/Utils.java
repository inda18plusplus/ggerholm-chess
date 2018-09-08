package chess;

import java.util.function.Consumer;

public final class Utils {

  /**
   * Tries to execute the given consumer with the provided value.
   * The execution is surrounded by a try/catch block and will capture any potential
   * ArrayIndexOutOfBounds exception. The exception will be logged and then ignored.
   *
   * @param consumer The consumer to be executed.
   * @param index    The value to be passed to the consumer.
   */
  public static void tryPutAt(Consumer<Integer> consumer, int index) {
    try {
      consumer.accept(index);
    } catch (ArrayIndexOutOfBoundsException ignored) {
      // TODO: Log
    }
  }

}
