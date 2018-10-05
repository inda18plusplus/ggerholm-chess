package chess.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionManager {

  private static final int port = 6606;

  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  private final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

  void connectToHost(String targetAddress, int port) throws IOException {
    if (port <= 0) {
      port = ConnectionManager.port;
    }

    logger.debug("Connecting to host (IP = {}, port = {}).", targetAddress, port);
    socket = new Socket(targetAddress, port);
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    logger.info("Connection successful.");
  }

  void listenForConnections(int port) throws IOException {
    if (port <= 0) {
      port = ConnectionManager.port;
    }

    ServerSocket serverSocket = new ServerSocket(port);
    logger.debug("Waiting for connection at port = {}", port);
    socket = serverSocket.accept();
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    logger.info("Connection successful.");
  }

  void send(String data) throws IOException {
    if (data == null || data.isEmpty() || outputStream == null || !isConnected()) {
      return;
    }

    outputStream.writeUTF(data);
    logger.info("Data sent: {}", data);
  }

  private String receive() throws IOException {
    if (inputStream == null || !isConnected()) {
      return null;
    }

    String data = inputStream.readUTF();
    logger.info("Data received: {}", data);
    return data;
  }

  JSONObject receiveResponse() throws IOException {
    logger.debug("Waiting for response.");
    JSONObject jsonObj = new JSONObject(Objects.requireNonNull(receive()));
    if (Utils.isNotResponse(jsonObj)) {
      return null;
    }

    return jsonObj;
  }

  JSONObject receiveMove() throws IOException {
    logger.debug("Waiting for move.");
    JSONObject jsonObj = new JSONObject(Objects.requireNonNull(receive()));
    if (Utils.isNotMove(jsonObj)) {
      return null;
    }

    return jsonObj;
  }

  JSONObject receiveInitMessage() throws IOException {
    logger.debug("Waiting for init-message.");
    JSONObject jsonObj = new JSONObject(Objects.requireNonNull(receive()));
    if (Utils.isNotInitialization(jsonObj)) {
      return null;
    }

    return jsonObj;
  }

  void disconnect() throws IOException {
    logger.debug("Disconnecting.");
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }

    inputStream = null;
    outputStream = null;
    logger.info("Disconnection successful.");
  }

  boolean isConnected() {
    return socket != null && socket.isConnected() && inputStream != null && outputStream != null;
  }

}
