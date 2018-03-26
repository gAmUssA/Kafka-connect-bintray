package io.gamov.connect.bintray.model;

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

  // user_agent
  private String userAgent;

  // content_length
  private int contentLengh;

  // ip_address
  private String ipAddress;
  private String subject;
  private String time;

}
