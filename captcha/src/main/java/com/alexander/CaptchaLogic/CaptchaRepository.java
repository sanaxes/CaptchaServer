package com.alexander.CaptchaLogic;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("captchaRepository")
public class CaptchaRepository {
  private static final AtomicLong counter = new AtomicLong();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final CopyOnWriteArrayList<Captcha> listOfCaptcha = new CopyOnWriteArrayList<Captcha>();

  private static CaptchaRepository ourInstance = new CaptchaRepository();

  public static CaptchaRepository getInstance() {
    return ourInstance;
  }

  private CaptchaRepository() {}

  public Captcha findById(long id) {
    logger.info("find captcha by id into repository");
    List<Long> idList =
        this.listOfCaptcha.parallelStream().map(Captcha::getId).collect(Collectors.toList());
    int result = Collections.binarySearch(idList, id);
    if (result >= 0) {
      Captcha captcha = this.listOfCaptcha.get(result);
      logger.info("found captcha: " + captcha.toString());
      return captcha;
    }
    logger.info("captcha with this id does not exist [" + id + "]");
    return null;
  }

  public Captcha createCaptcha(int width, int height, int length) {
    logger.info("create new captcha");
    Captcha captcha = new Captcha(counter.incrementAndGet(), width, height, length);
    listOfCaptcha.add(captcha);
    logger.info("save captcha into repository");
    logger.info("Captcha: " + captcha.toString());
    return captcha;
  }

  public boolean isCaptchaExist(Captcha captcha) {
    logger.info("check if there is a captcha in the repository");
    return findById(captcha.getId()) != null;
  }

}
