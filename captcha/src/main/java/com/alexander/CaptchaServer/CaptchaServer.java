package com.alexander.CaptchaServer;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class CaptchaServer {
  private static final RequestHandler requestHandler = RequestHandler.getInstance();

  public static void main(String[] args) throws Exception {
    SpringApplication.run(CaptchaServer.class, args);
  }

  @RequestMapping(value = "/getcaptcha", method = RequestMethod.GET,
      produces = MediaType.IMAGE_PNG_VALUE)
  @ResponseBody
  ResponseEntity<byte[]> doGet() throws IOException {
    return requestHandler.getCaptcha();
  }

  @RequestMapping(value = "/sendcaptcha", method = RequestMethod.POST)
  @ResponseBody
  ResponseEntity<HttpStatus> doPost(@RequestParam("id") String id,
      @RequestParam("answer") String answer) {
    return requestHandler.sendCaptcha(id, answer);
  }
}
