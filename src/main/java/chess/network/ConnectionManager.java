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
    if (data == null || data.length() == 0 || outputStream == null) {
      return;
    }

    byte[] bytes = data.getBytes();

    outputStream.writeInt(bytes.length);
    if (bytes.length > 0) {
      outputStream.write(bytes, 0, bytes.length);
    }
  }

  String receive() throws IOException {
    if (inputStream == null) {
      return null;
    }

    int length = inputStream.readInt();
    byte[] data = new byte[length];
    if (length > 0) {
      inputStream.readFully(data);
    }

    return new String(data);
  }

  void disconnect() throws IOException {
    socket.close();
  }

  boolean isConnected() {
    return socket != null && socket.isConnected();
  }

}
