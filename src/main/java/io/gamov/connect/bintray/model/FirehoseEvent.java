package io.gamov.connect.bintray.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON response of bintray firehose event
 *
 * {
 *   "content_length": 4785,
 *   "ip_address": "xxx.xx.xxx.xxx",
 *   "path": "/russian-hackers/hacking-utils/semaphore-bank-hack-1.0.zip",
 *   "subject": "gamussa",
 *   "time": "2018-03-26T15:16:41.986Z",
 *   "type": "download",
 *   "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/604.5.6 (KHTML, like Gecko) Version/11.0.3 Safari/604.5.6"
 * }
 *
 */
public class FirehoseEvent {

  private String type;
  private String path;

  @JsonProperty("user_agent")
  private String userAgent;

  @JsonProperty("content_length")
  private int contentLength;

  @Override
  public String toString() {
    return "FirehoseEvent{" + "type='" + type + '\''
           + ", path='" + path + '\''
           + ", userAgent='" + userAgent + '\''
           + ", contentLength=" + contentLength
           + ", ipAddress='" + ipAddress + '\''
           + ", subject='" + subject + '\''
           + ", time='" + time + '\''
           + '}';
  }

  @JsonProperty("ip_address")
  private String ipAddress;
  private String subject;
  private String time;

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public int getContentLength() {
    return contentLength;
  }

  public void setContentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
