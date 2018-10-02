package chess.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.json.JSONObject;

class Utils {

  static String hash(String input) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashedBytes = digest.digest(input.getBytes());

    StringBuilder builder = new StringBuilder();

    for (byte hashedByte : hashedBytes) {
      builder.append(String.format("%02X", hashedByte));
    }

    return builder.toString();
  }

  static String createSeed(int length) {
    StringBuilder seed = new StringBuilder();

    SecureRandom random = new SecureRandom();
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

  static boolean isNotResponse(JSONObject jsonObj) {
    return incorrectType(jsonObj, "response");
  }

  static boolean isNotMove(JSONObject jsonObj) {
    return incorrectType(jsonObj, "move");
  }

  static boolean isNotInitialization(JSONObject jsonObj) {
    return incorrectType(jsonObj, "init");
  }

  private static boolean incorrectType(JSONObject jsonObj, String correctType) {
    if (jsonObj == null || !jsonObj.has("type")) {
      return true;
    }

    return !jsonObj.get("type").toString().equalsIgnoreCase(correctType);
  }

}
