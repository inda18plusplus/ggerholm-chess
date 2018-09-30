package chess.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionManager {

  private static final int port = 12712;

  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  private final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

  void connectToHost(String targetAddress) throws IOException {
    logger.debug("Connecting to host (IP = {}).", targetAddress);
    socket = new Socket(targetAddress, port);
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    logger.debug("Connection successful.");
  }

  void listenForConnections() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    logger.debug("Waiting for connection at port = {}", port);
    socket = serverSocket.accept();
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    logger.debug("Connection successful.");
  }

  void send(String data) throws IOException {
    if (data == null || data.isEmpty() || outputStream == null || !isConnected()) {
      return;
    }

    outputStream.writeUTF(data);
    logger.debug("Data sent.");
  }

  String receive() throws IOException {
    if (inputStream == null || !isConnected()) {
      return null;
    }

    logger.debug("Waiting for packet.");
    return inputStream.readUTF();
  }

  void disconnect() throws IOException {
    logger.debug("Disconnecting.");
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }

    inputStream = null;
    outputStream = null;
    logger.debug("Disconnection successful.");
  }

  boolean isConnected() {
    return socket != null && socket.isConnected() && inputStream != null && outputStream != null;
  }

}
