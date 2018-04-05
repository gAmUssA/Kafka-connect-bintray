package io.gamov.connect.bintray.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;

public class HttpsURLConnectionClient implements HttpClient {

  private HttpsURLConnection httpConn;

  public HttpsURLConnectionClient() {
    httpConn = null;
  }

  @Override
  public void openHttpsConnection(String urlStr, String user, String apiKey,
                                  Consumer<String> handler)
      throws IOException {
    String line = null;
    try {
      URL url = new URL(urlStr);
      URLConnection urlConn = url.openConnection();
      if (!(urlConn instanceof HttpsURLConnection)) {
        throw new IOException("URL is not an Https URL");
      }
      httpConn = (HttpsURLConnection) urlConn;
      httpConn.setAllowUserInteraction(false);
      httpConn.setInstanceFollowRedirects(true);
      httpConn.setRequestMethod("GET");
      httpConn.setReadTimeout(50 * 1000);
      httpConn.setRequestProperty("Authorization", credentials(user, apiKey));

      BufferedReader is =
          new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

      while ((line = is.readLine()) != null) {
        //TODO: logging
        handler.accept(line);
      }
    } catch (IOException e) {

      e.printStackTrace();

      //BufferedInputStream in = new BufferedInputStream(httpConn.getErrorStream());

    } finally {
      httpConn.disconnect();
    }

  }

  @Override
  public void closeConnection() {
    if (httpConn != null) {

      this.httpConn.disconnect();
    }
  }


}
