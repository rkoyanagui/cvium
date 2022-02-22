package com.rkoyanagui.img_recog.impl;

import static com.rkoyanagui.img_recog.impl.OpenCvImgUtils.toBufferedImage;
import static com.rkoyanagui.utils.WaitUtils.await;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.ImgRecogBy;
import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogEngine;
import com.rkoyanagui.img_recog.ImgUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HasOnScreenKeyboard;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import java.awt.image.BufferedImage;
import java.util.List;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

/**
 * Represents an element in a mobile app's screen, located via image recognition techniques.
 */
public class ImgRecogMobileElement implements ImgRecogElement
{

  protected static final CharSequence _10_BACK_SPACES = Keys.chord(
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE,
      Keys.BACK_SPACE);

  protected final Rectangle rect;
  protected final Point centre;
  protected final List<ImgRecogBy> locators;
  protected final Double score;
  protected final AppiumDriver<?> driver;
  protected final ImgRecogEngine imgRecogEngine;

  public ImgRecogMobileElement(final Rectangle rect,
                               final List<ImgRecogBy> locators,
                               final Double score,
                               final AppiumDriver<?> driver,
                               final ImgRecogEngine imgRecogEngine)
  {
    this.rect = rect;
    this.centre = ImgUtils.getCentre(rect);
    this.locators = locators;
    this.score = score;
    this.driver = driver;
    this.imgRecogEngine = imgRecogEngine;
  }

  @Override
  public List<ImgRecogBy> getLocators()
  {
    return locators;
  }

  @Override
  public Double getScore()
  {
    return score;
  }

  @Override
  public Point getCentre()
  {
    return centre;
  }

  @Override
  public Point getLocation()
  {
    return rect.getPoint();
  }

  @Override
  public Dimension getSize()
  {
    return rect.getDimension();
  }

  @Override
  public Rectangle getRect()
  {
    return rect;
  }

  @Override
  public boolean isDisplayed()
  {
    return nonNull(this.rect);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void click()
  {
    new TouchAction(driver)
        .tap(PointOption.point(centre))
        .perform();
  }

  @Override
  public void contextClick()
  {
    new TouchAction<>(driver)
        .press(PointOption.point(centre))
        .waitAction(WaitOptions.waitOptions(ofSeconds(2L)))
        .release()
        .perform();
  }

  @Override
  public void dragTo(final ImgRecogElement other)
  {
    final Point otherCentre = other.getCentre();
    new TouchAction<>(driver)
        .press(PointOption.point(this.centre))
        .waitAction(WaitOptions.waitOptions(ofSeconds(1L)))
        .moveTo(PointOption.point(otherCentre))
        .waitAction(WaitOptions.waitOptions(ofSeconds(1L)))
        .release()
        .perform();
    await(5);
  }

  @Override
  public void clear()
  {
    // Taps the element, in order to activate the keyboard.
    click();

    // Waits until the keyboard is shown.
    await()
        .initialDelay(2_000)
        .until(() -> ((HasOnScreenKeyboard) driver).isKeyboardShown())
        .perform();

    // For as long as the element contains a non-empty string, tries to clear it by pressing the
    // backspace key 10 times.
    await()
        .maxNumOfAttempts(3)
        .until(() -> {
          final byte[] img = driver.getScreenshotAs(OutputType.BYTES);
          final BufferedImage bi = toBufferedImage(img);
          return imgRecogEngine.recognise(bi, rect)
              .map(s -> s.isEmpty())
              .orElse(false);
        })
        .orElseDo(() -> driver.getKeyboard().pressKey(_10_BACK_SPACES))
        .perform();
  }

  @Override
  public boolean isTextEmpty()
  {
    final byte[] img = driver.getScreenshotAs(OutputType.BYTES);
    final BufferedImage bi = toBufferedImage(img);
    return imgRecogEngine.recognise(bi, rect)
        .map(s -> s.isEmpty())
        .orElse(false);
  }

  @Override
  public void sendKeys(String text)
  {
    click();
    driver.getKeyboard().pressKey(text);
  }

}
