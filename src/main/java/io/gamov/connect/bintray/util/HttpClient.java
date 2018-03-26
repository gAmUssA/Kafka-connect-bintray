package io.gamov.connect.bintray.util;

import java.io.IOException;
import java.util.function.Consumer;

public interface HttpClient {

  String credentials(String user, String apiKey);

  void openHttpsConnection(String urlStr, String user, String apiKey,
                           Consumer<String> handler)
      throws
      IOException;

  void closeConnection();
}
