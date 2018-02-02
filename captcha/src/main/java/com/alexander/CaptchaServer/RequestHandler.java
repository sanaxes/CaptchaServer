package com.alexander.CaptchaServer;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.alexander.CaptchaLogic.Captcha;
import com.alexander.CaptchaLogic.CaptchaRepository;

public class RequestHandler {

  private static RequestHandler ourInstance = new RequestHandler();

  public static RequestHandler getInstance() {
    return ourInstance;
  }

  private RequestHandler() {}

  private final CaptchaRepository repository = CaptchaRepository.getInstance();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ResponseEntity<byte[]> getCaptcha() {
    logger.info("received get request");
    Captcha captcha = repository.createCaptcha(420, 140, 6);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("id", Long.toString(captcha.getId()));
    responseHeaders.add("answer", captcha.getAnswer());
    logger.info("sending the response");
    ResponseEntity<byte[]> responseEntity =
        new ResponseEntity<byte[]>(captcha.getImage(), responseHeaders, HttpStatus.OK);
    return responseEntity;
  }

  public ResponseEntity<HttpStatus> sendCaptcha(String id, String answer) {
    logger.info("received post request");
    long parsedId;
    try {
      logger.info("try to parse id [String to Long]");
      parsedId = Long.parseLong(id);
    } catch (NumberFormatException e) {
      logger.error("can't parse id [" + id + "]");
      return new ResponseEntity<HttpStatus>(HttpStatus.NOT_ACCEPTABLE);
    }
    Captcha captcha = repository.findById(parsedId);
    if (captcha == null) {
      return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
    }
    if (!captcha.isAvalible()) {
      logger.info("answer already entered");
      return new ResponseEntity<HttpStatus>(HttpStatus.ALREADY_REPORTED);
    }
    captcha.setAvalible(false);
    long dateDifference = new Date().getTime() - captcha.getDate().getTime();
    if (dateDifference > Captcha.TIMEOUT) {
      logger.info("timeout expired");
      return new ResponseEntity<HttpStatus>(HttpStatus.REQUEST_TIMEOUT);
    }
    if (!answer.equalsIgnoreCase(captcha.getAnswer())) {
      logger.info("response entered incorrectly");
      return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
    }
    logger.info("response entered correctly");
    return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
  }
}
