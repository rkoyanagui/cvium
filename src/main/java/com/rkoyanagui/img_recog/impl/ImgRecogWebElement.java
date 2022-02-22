package com.rkoyanagui.img_recog.impl;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.ImgRecogBy;
import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogEngine;
import com.rkoyanagui.img_recog.ImgUtils;
import java.util.List;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class ImgRecogWebElement implements ImgRecogElement
{

  protected final Rectangle rect;
  protected final Point centre;
  protected final List<ImgRecogBy> locators;
  protected final Double score;
  protected final WebDriver driver;
  protected final ImgRecogEngine imgRecogEngine;

  public ImgRecogWebElement(final Rectangle rect,
                            final List<ImgRecogBy> locators,
                            final Double score,
                            final WebDriver driver,
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
  public void click()
  {
    new Actions(driver)
        .moveByOffset(centre.x, centre.y)
        .click()
        .perform();
  }

  @Override
  public void contextClick()
  {
    new Actions(driver)
        .moveByOffset(centre.x, centre.y)
        .contextClick()
        .perform();
  }

  @Override
  public void dragTo(final ImgRecogElement other)
  {
    final Point otherCentre = other.getCentre();
    new Actions(driver)
        .moveByOffset(this.centre.x, this.centre.y)
        .clickAndHold()
        .pause(ofSeconds(1L))
        .moveByOffset(otherCentre.x, otherCentre.y)
        .pause(ofSeconds(1L))
        .release()
        .perform();
  }

  // TODO Either find a way to implement these methods, preferably in a way so Selenium could run in
  //  headless mode, or remove web element support entirely.

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isTextEmpty()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendKeys(String text)
  {
    throw new UnsupportedOperationException();
  }

}
