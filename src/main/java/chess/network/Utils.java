package chess.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.json.JSONObject;

class Utils {

  static String hash(String input) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashedBytes = digest.digest(input.getBytes());

    StringBuilder builder = new StringBuilder();

    for (byte hashedByte : hashedBytes) {
      int unsignedByte = 0xFF & hashedByte;
      builder.append(Integer.toHexString(unsignedByte));
    }

    return builder.toString();
  }

  static String createSeed(int length) {
    StringBuilder seed = new StringBuilder();

    Random random = new Random();
    for (int i = 0; i < length; i++) {
      seed.append((char) (65 + random.nextInt(26)));
    }

    return seed.toString();
  }

  static JSONObject createJson(String type) {
    JSONObject jsonObj = new JSONObject();
    jsonObj.put("type", type);
    return jsonObj;
  }

}
