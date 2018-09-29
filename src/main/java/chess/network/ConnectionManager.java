package chess.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ConnectionManager {

  private static final int port = 12712;

  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  void connectToHost(String targetAddress) throws IOException {
    socket = new Socket(targetAddress, port);
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
  }

  void listenForConnections() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    socket = serverSocket.accept();
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
  }

  void send(String data) throws IOException {
    if (data == null || data.isEmpty() || outputStream == null || !isConnected()) {
      return;
    }

    outputStream.writeUTF(data);
  }

  String receive() throws IOException {
    if (inputStream == null || !isConnected()) {
      return null;
    }

    return inputStream.readUTF();
  }

  void disconnect() throws IOException {
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }

    inputStream = null;
    outputStream = null;
  }

  boolean isConnected() {
    return socket != null && socket.isConnected() && inputStream != null && outputStream != null;
  }

}
