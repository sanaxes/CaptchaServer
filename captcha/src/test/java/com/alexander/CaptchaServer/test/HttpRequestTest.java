package com.alexander.CaptchaServer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.alexander.CaptchaLogic.Captcha;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HttpRequestTest {

  @Autowired
  private MockMvc mockMvc;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // simple get request
  @Test
  public void shouldReturnIdAndAnswerInHeader() throws Exception {
    logger.info("TEST #1 check headerNames on contains id and answer");
    Collection<String> headerNames =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse().getHeaderNames();
    assertTrue(headerNames.contains("id"));
    assertTrue(headerNames.contains("answer"));
  }

  // correct answer
  @Test
  public void shouldReturnCorrectlyRequest() throws Exception {
    logger.info("TEST #2 correct answer from /get");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = response.getHeader("id");
    String answer = response.getHeader("answer");
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.ACCEPTED.value(), request.getStatus());
  }

  // repeated request
  @Test
  public void shouldReturnAnswerAlreadyEntered() throws Exception {
    logger.info("TEST #3 repeat request");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = response.getHeader("id");
    String answer = response.getHeader("answer");
    this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer)).andReturn()
        .getResponse().getContentAsString();
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.ALREADY_REPORTED.value(), request.getStatus());
  }

  // can't parse ID
  @Test
  public void shouldReturnIncorrectlyEnteredId() throws Exception {
    logger.info("TEST #4 can't parse ID");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = "STRING";
    String answer = response.getHeader("answer");
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), request.getStatus());
  }

  // no id
  @Test
  public void shouldReturnNoId() throws Exception {
    logger.info("TEST #5 no ID");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = "10";
    String answer = response.getHeader("answer");
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.NOT_FOUND.value(), request.getStatus());
  }

  // Timeout
  @Test
  public void shouldReturnTimeout() throws Exception {
    logger.info("TEST #6 timeout");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = response.getHeader("id");
    String answer = response.getHeader("answer");
    Thread.sleep(Captcha.TIMEOUT + 1L);
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), request.getStatus());
  }

  // incorrectly answer
  @Test
  public void shouldReturnIncorrectlyEnteredAnswer() throws Exception {
    logger.info("TEST #7 incorrectly answer");
    MockHttpServletResponse response =
        this.mockMvc.perform(get("/getcaptcha")).andReturn().getResponse();
    String id = response.getHeader("id");
    String answer = "blah";
    MockHttpServletResponse request =
        this.mockMvc.perform(post("/sendcaptcha").param("id", id).param("answer", answer))
            .andReturn().getResponse();
    assertEquals(HttpStatus.BAD_REQUEST.value(), request.getStatus());
  }
}
