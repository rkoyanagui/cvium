package com.rkoyanagui.img_recog;

import java.util.List;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

/**
 * Represents a screen element, located via image recognition techniques.
 */
public interface ImgRecogElement
{

  /**
   * Retrieves a list of what strategies were used to locate this element.
   *
   * @return this element's locators
   */
  List<ImgRecogBy> getLocators();

  /**
   * Retrives the last applied locator's 'score', or level of confidence. Depending on what kind of
   * locator it was, the score could be a number between 0 and 1, or between 0 and 100, or just any
   * positive integer. For this reason, it is better used not as an absolute value, but as a point
   * of comparison between two elements.
   *
   * @return the score, or level of confidence
   */
  Double getScore();

  /**
   * Retrieves the point at the centre of this element.
   *
   * @return this element's centre coordinates.
   */
  Point getCentre();

  /**
   * Retrieves the point at the upper left corner of this element, that is, its anchor.
   *
   * @return this element's anchor coordinates.
   */
  Point getLocation();

  /**
   * Retrieves this element's dimensions.
   *
   * @return this element's width and height.
   */
  Dimension getSize();

  /**
   * Retrieves a rectangle representing this element's screen location, anchored at the upper left
   * corner, plus width and height.
   *
   * @return this element's location, as a rectangular section of the screen.
   */
  Rectangle getRect();

  /**
   * Tells if the element could be ascribed to a visible location on the screen, or not.
   *
   * @return {@link Boolean#TRUE} if the element is visible. {@link Boolean#FALSE} otherwise.
   */
  boolean isDisplayed();

  /**
   * Taps at the centre of this element.
   */
  void click();

  /**
   * If this is a mobile element, then presses on it long enough (2s) to generally pop a context
   * window. Else if this is a web element, then right-clicks it.
   */
  void contextClick();

  /**
   * Drags the cursor/finger from this element to another, that is, presses on <i>(x<sub>0</sub>,
   * y<sub>0</sub>)</i>, this element's centre coordinates, then slides across the screen up to
   * <i>(x<sub>1</sub>, y<sub>1</sub>)</i>, the other element's centre coordinates, then releases.
   *
   * @param other an element whose centre becomes the terminal point of the drag-and-drop action
   */
  void dragTo(ImgRecogElement other);

  /**
   * Taps at the centre of the element, so as to pop the device's native keyboard up, then uses it
   * to press the backspace-key repeatedly, in the hope of clearing any text from the element.
   */
  void clear();

  /**
   * Uses character recognition to check that an element contains nothing textual.
   *
   * @return {@link Boolean#TRUE} if the element contains no text. {@link Boolean#FALSE} otherwise.
   */
  boolean isTextEmpty();

  /**
   * Taps at the centre of the element, so as to pop the device's native keyboard up, then uses it
   * to type some text.
   *
   * @param text text to be typed
   */
  void sendKeys(String text);

}
