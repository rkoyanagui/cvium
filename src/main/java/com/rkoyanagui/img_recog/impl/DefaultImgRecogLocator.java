package com.rkoyanagui.img_recog.impl;

import static com.rkoyanagui.utils.MobilePlatform.ANDROID;
import static com.rkoyanagui.utils.MobilePlatform.IOS;
import static java.util.Objects.nonNull;

import com.google.common.collect.ImmutableList;
import com.rkoyanagui.img_recog.ImageTemplateFinder;
import com.rkoyanagui.img_recog.ImgRecogBy;
import com.rkoyanagui.img_recog.ImgRecogBy.ImgRecogByBuilderFromAnnotation;
import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogEngine;
import com.rkoyanagui.img_recog.ImgRecogException;
import com.rkoyanagui.img_recog.ImgRecogLocator;
import io.appium.java_client.HasSessionDetails;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class DefaultImgRecogLocator implements ImgRecogLocator
{

  private static final String NO_LOCATOR_FOR_PLATFORM = "Could not find image recognition element "
      + "locator for platform: ";
  private static final String UNSUPPORTED_PLATFORM = "Unsupported platform: ";
  private static final String COULD_NOT_MATCH = "Could not match this element to"
      + " any location on the screen. Locators: ";
  private static final String COULD_NOT_MATCH_LIST = "Could not find a list of elements using the "
      + "following locator: ";

  protected final List<ImgRecogBy> imgRecogByList;
  protected final ImgRecogEngine imgRecogEngine;
  protected final WebDriver driver;

  public DefaultImgRecogLocator(final Field field,
                                final ImgRecogEngine imgRecogEngine,
                                final WebDriver driver)
  {
    this.imgRecogByList = buildBy(field);
    this.imgRecogEngine = imgRecogEngine;
    this.driver = driver;
  }

  @Override
  public ImgRecogElement findElement()
  {
    // List all locators applicable to the platform in use.
    final List<ImgRecogBy> pImgRecogByList = listLocatorsForPlatform(driver, imgRecogByList);
    // Take screenshot.
    final byte[] srcImg = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    // If there is more than one locator, then each is applied in succession to refine the match.
    return imgRecogEngine.findElement(pImgRecogByList, srcImg)
        .orElseThrow(() -> new NoSuchElementException(COULD_NOT_MATCH + pImgRecogByList));
  }

  @Override
  public List<ImgRecogElement> findElements()
  {
    // List all locators applicable to the platform in use.
    final List<ImgRecogBy> pImgRecogByList = listLocatorsForPlatform(driver, imgRecogByList);
    // Pick the first one only. Multiple locators cannot be used to find lists of elements.
    final ImgRecogBy imgRecogBy = pImgRecogByList.get(0);
    // Take screenshot.
    final byte[] srcImg = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    final List<ImgRecogElement> mrs = imgRecogEngine.findElements(imgRecogBy, srcImg);
    if (mrs.isEmpty())
    {
      throw new NoSuchElementException(COULD_NOT_MATCH_LIST + imgRecogBy);
    }
    return mrs;
  }

  protected static List<ImgRecogBy> listLocatorsForPlatform(final WebDriver driver,
                                                            final List<ImgRecogBy> imgRecogByList)
  {
    final String platformName;
    if (driver instanceof HasSessionDetails)
    {
      platformName = ((HasSessionDetails) driver).getPlatformName();
    }
    else
    {
      platformName = "";
    }
    if (!(ANDROID.description.equalsIgnoreCase(platformName) ||
        IOS.description.equalsIgnoreCase(platformName)))
    {
      throw new ImgRecogException(UNSUPPORTED_PLATFORM + platformName);
    }
    final List<ImgRecogBy> pImgRecogByList = imgRecogByList.stream()
        .filter(by -> platformName.equalsIgnoreCase(by.getPlatform()))
        .sorted(Comparator.comparing(ImgRecogBy::getOrder))
        .collect(ImmutableList.toImmutableList());
    if (pImgRecogByList.isEmpty())
    {
      throw new ImgRecogException(NO_LOCATOR_FOR_PLATFORM + platformName);
    }
    return pImgRecogByList;
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + " '" + imgRecogByList + "'";
  }

  @SuppressWarnings("rawtypes")
  protected static List<ImgRecogBy> buildBy(final Field field)
  {
    final List<ImgRecogBy> imgRecogByList = new ArrayList<>();
    for (final Annotation annotation : field.getDeclaredAnnotations())
    {
      ImgRecogByBuilderFromAnnotation builder = null;
      if (annotation.annotationType().isAnnotationPresent(ImageTemplateFinder.class))
      {
        try
        {
          builder = annotation.annotationType()
              .getAnnotation(ImageTemplateFinder.class)
              .value()
              .newInstance();
        }
        catch (ReflectiveOperationException x)
        {
          // Fall through.
        }
      }
      if (nonNull(builder))
      {
        final ImgRecogBy by = builder.build(annotation, field);
        imgRecogByList.add(by);
      }
    }
    if (imgRecogByList.isEmpty())
    {
      throw new IllegalArgumentException("Cannot determine how to locate element " + field);
    }
    return imgRecogByList;
  }

}
