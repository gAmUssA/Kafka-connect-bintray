package io.gamov.connect.bintray.util;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * My HttpClient interface
 * Contains base implementation of credential generator based on user and apiKey
 */
public interface HttpClient {

  default String credentials(String user, String apiKey) {
    String userPassword = user + ":" + apiKey;
    return "Basic " + new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
  }

  void openHttpsConnection(String urlStr, String user, String apiKey,
                           Consumer<String> handler)
      throws
      IOException;

  void closeConnection();
}
