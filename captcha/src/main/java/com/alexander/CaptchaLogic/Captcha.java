package com.alexander.CaptchaLogic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;

public class Captcha {
  private final long id; // unique id
  private final String answer; // captcha answer
  private boolean avalible = true; // avalible
  private final Date date = new Date(); // created date
  private byte[] image; // picture
  public final static long TIMEOUT = 25000L; // timeout

  protected Captcha(long id, int width, int height, int length) {
    this.id = id;
    this.answer = generateRandomString(length);
    this.image = drawCaptchaImage(width, height);
  }

  private String generateRandomString(int length) {
    String charList =
        "1234567890abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    String randomString = "";
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      int randomNumber = random.nextInt(charList.length());
      char randromChar = charList.charAt(randomNumber);
      randomString += randromChar;
    }
    return randomString;
  }

  private byte[] drawCaptchaImage(int width, int height) {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    drawBackground(bufferedImage);
    drawString(bufferedImage);
    drawLines(bufferedImage);
    return generateImage(bufferedImage);
  }

  private void drawBackground(BufferedImage bufferedImage) {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    Graphics2D background = bufferedImage.createGraphics();
    background.setColor(Color.WHITE);
    background.fillRect(0, 0, width, height);
    background.dispose();
  }

  private void drawString(BufferedImage bufferedImage) {
    Random random = new Random();
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    int step = 0;
    int fontSize = width / 5;
    for (int i = 0; i < this.answer.length(); i++) {
      Color newColor = getRandomColor();
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(newColor);
      graphics.setFont(new Font("Serif", Font.CENTER_BASELINE, fontSize));
      double randomBetween = 0.6 * random.nextDouble() - 0.3;
      AffineTransform scalingTransform =
          AffineTransform.getRotateInstance(randomBetween, step, height / 2);
      graphics.transform(scalingTransform);
      graphics.drawString(String.valueOf(this.answer.charAt(i)), step, fontSize);
      graphics.dispose();
      step += width / this.answer.length();
    }
  }

  private Color getRandomColor() {
    // random color for each character
    Random random = new Random();
    switch (random.nextInt(9)) {
      case 0:
        return Color.BLUE;
      case 1:
        return Color.CYAN;
      case 2:
        return Color.GRAY;
      case 3:
        return Color.GREEN;
      case 4:
        return Color.ORANGE;
      case 5:
        return Color.PINK;
      case 6:
        return Color.YELLOW;
      case 7:
        return Color.MAGENTA;
      case 8:
        return Color.LIGHT_GRAY;
      default:
        return Color.BLACK;
    }
  }

  private void drawLines(BufferedImage bufferedImage) {
    Random random = new Random();
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    Graphics2D lines = bufferedImage.createGraphics();
    lines.setColor(Color.BLACK);
    lines.drawLine(0, random.nextInt(height), width, random.nextInt(height));
    lines.drawLine(0, random.nextInt(height), width, random.nextInt(height));
    lines.drawLine(random.nextInt(width), 0, random.nextInt(width), height);
    lines.dispose();
  }

  private byte[] generateImage(BufferedImage bufferedImage) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ImageIO.write(bufferedImage, "png", out);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toByteArray();
  }

  public void setAvalible(boolean avalible) {
    this.avalible = avalible;
  }

  public byte[] getImage() {
    return this.image;
  }

  public String getAnswer() {
    return this.answer;
  }

  public long getId() {
    return this.id;
  }

  public boolean isAvalible() {
    return this.avalible;
  }

  public Date getDate() {
    return this.date;
  }

  @Override
  public String toString() {
    return "[id: " + getId() + ", answer: " + getAnswer() + ", isAvalible: " + isAvalible()
        + ", date: " + getDate() + "]";
  }
}
