package com.rkoyanagui.img_recog.impl;

import static com.rkoyanagui.img_recog.impl.OpenCvImgUtils.maybeNotEmpty;
import static com.rkoyanagui.img_recog.impl.OpenCvImgUtils.toAwtRect;
import static com.rkoyanagui.utils.MobilePlatform.ANDROID;
import static java.util.Objects.nonNull;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED;
import static org.opencv.imgcodecs.Imgcodecs.imdecode;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rkoyanagui.img_recog.EmptyMatException;
import com.rkoyanagui.img_recog.FeatureMatchingBy;
import com.rkoyanagui.img_recog.FractionalRectangle;
import com.rkoyanagui.img_recog.ImgRecogBy;
import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogEngine;
import com.rkoyanagui.img_recog.ImgUtils;
import com.rkoyanagui.img_recog.MatchResult;
import com.rkoyanagui.img_recog.OcrMatchingBy;
import com.rkoyanagui.img_recog.TemplateMatchingBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.HasAndroidDeviceDetails;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCvImgRecogEngine implements ImgRecogEngine, Closeable
{

  private static final Logger LOG = LoggerFactory.getLogger(OpenCvImgRecogEngine.class);
  protected static final String UNSUPPORTED_METHOD = "Unsupported image recognition method: ";
  protected static final String QUERY_IMG_IS_PRESENT = "queryImage::isPresent ? {}";
  protected static final String TRAIN_IMG_IS_PRESENT = "trainingImage::isPresent ? {}";
  protected final boolean debug;
  protected final TesseractOcrMatcher ocrMatcher;
  protected final WebDriver driver;
  protected final JsonParser jsonParser;

  public OpenCvImgRecogEngine(final WebDriver driver)
  {
    this.debug = Boolean.getBoolean("img_recog.debug");
    this.ocrMatcher = new TesseractOcrMatcher();
    this.driver = driver;
    this.jsonParser = new JsonParser();
  }

  @Override
  public Optional<ImgRecogElement> findElement(final ImgRecogBy params,
                                               final byte[] trainingImage)
  {
    final Mat qImg;
    // A template is used with the template matching and feature matching methods, but not with OCR.
    final String templateFilename = params.getTemplateFilename();
    if (nonNull(templateFilename) && !templateFilename.isEmpty())
    {
      qImg = maybeNotEmpty(Imgcodecs.imread(templateFilename))
          .orElseThrow(() -> EmptyMatException.cannotReadFrom(templateFilename));
    }
    else
    {
      qImg = null;
    }
    final Optional<MatOfByte> mbTrainImg = maybeNotEmpty(new MatOfByte(trainingImage));
    LOG.debug(TRAIN_IMG_IS_PRESENT, mbTrainImg.isPresent());
    return mbTrainImg.map(matOfByte -> imdecode(matOfByte, IMREAD_UNCHANGED))
        .flatMap(tImg -> findElement(params, qImg, tImg));
  }

  @Override
  public Optional<ImgRecogElement> findElement(final ImgRecogBy params,
                                               final byte[] queryImage,
                                               final byte[] trainingImage)
  {
    final Optional<MatOfByte> mbQueryImg = maybeNotEmpty(new MatOfByte(queryImage));
    LOG.debug(QUERY_IMG_IS_PRESENT, mbQueryImg.isPresent());
    final Optional<MatOfByte> mbTrainImg = maybeNotEmpty(new MatOfByte(trainingImage));
    LOG.debug(TRAIN_IMG_IS_PRESENT, mbTrainImg.isPresent());

    return mbQueryImg.map(qMatOfByte -> imdecode(qMatOfByte, IMREAD_UNCHANGED))
        .flatMap(qImg -> mbTrainImg.map(tMatOfByte -> imdecode(tMatOfByte, IMREAD_UNCHANGED))
            .flatMap(tImg -> findElement(params, qImg, tImg))
        );
  }

  @Override
  public Optional<ImgRecogElement> findElement(final List<ImgRecogBy> paramList,
                                               final byte[] trainingImage)
  {
    return applyAlternativeLocators(trainingImage, paramList);
  }

  @Override
  public List<ImgRecogElement> findElements(final ImgRecogBy params,
                                            final byte[] trainingImage)
  {
    final Mat qImg;
    // A template is used with the template matching and feature matching methods, but not with OCR.
    final String templateFilename = params.getTemplateFilename();
    if (nonNull(templateFilename) && !templateFilename.isEmpty())
    {
      qImg = maybeNotEmpty(Imgcodecs.imread(templateFilename))
          .orElseThrow(() -> EmptyMatException.cannotReadFrom(templateFilename));
    }
    else
    {
      qImg = null;
    }
    final Optional<MatOfByte> mbTrainImg = maybeNotEmpty(new MatOfByte(trainingImage));
    LOG.debug(TRAIN_IMG_IS_PRESENT, mbTrainImg.isPresent());
    return mbTrainImg.map(matOfByte -> imdecode(matOfByte, IMREAD_UNCHANGED))
        .map(tImg -> findElements(params, qImg, tImg))
        .orElseGet(() -> ImmutableList.of());
  }

  @Override
  public List<ImgRecogElement> findElements(final ImgRecogBy params,
                                            final byte[] queryImage,
                                            final byte[] trainingImage)
  {
    final Optional<MatOfByte> mbQueryImg = maybeNotEmpty(new MatOfByte(queryImage));
    LOG.debug(QUERY_IMG_IS_PRESENT, mbQueryImg.isPresent());
    final Optional<MatOfByte> mbTrainImg = maybeNotEmpty(new MatOfByte(trainingImage));
    LOG.debug(TRAIN_IMG_IS_PRESENT, mbTrainImg.isPresent());

    return mbQueryImg.map(qMatOfByte -> imdecode(qMatOfByte, IMREAD_UNCHANGED))
        .flatMap(qImg -> mbTrainImg.map(tMatOfByte -> imdecode(tMatOfByte, IMREAD_UNCHANGED))
            .map(tImg -> findElements(params, qImg, tImg))
        ).orElseGet(() -> ImmutableList.of());
  }

  @Override
  public Optional<String> recognise(final BufferedImage img, final Rectangle rectangle)
  {
    return ocrMatcher.recognise(img, toAwtRect(rectangle));
  }

  protected Optional<ImgRecogElement> findElement(final ImgRecogBy params,
                                                  final Mat queryImage,
                                                  final Mat trainingImage)
  {
    final Size tImgSize = trainingImage.size();
    final Optional<Rect> optCutoutRect = Optional.ofNullable(params.getCutout())
        .map(cutout -> OpenCvImgUtils.proportionalToAbsoluteRect(tImgSize, cutout));
    final Mat cutoutSrcImg = optCutoutRect.map(rect -> OpenCvImgUtils.cutout(trainingImage, rect))
        .orElse(trainingImage);
    final Optional<? extends MatchResult> optMr;
    switch (params.getMethod())
    {
      case TEMPLATE_MATCHING:
        TemplateMatchingBy.verifyTemplateMatchingParams(params);
        optMr = OpenCvTemplateMatcher.findElementSizeInvariant(queryImage, cutoutSrcImg,
            (TemplateMatchingBy) params, debug);
        break;
      case FEATURE_MATCHING:
        FeatureMatchingBy.verifyFeatureMatchingParams(params);
        optMr = OpenCvFeatureMatcher.matchFeatures(queryImage, cutoutSrcImg,
            (FeatureMatchingBy) params, debug);
        break;
      case OCR:
        OcrMatchingBy.verifyOcrParams(params);
        optMr = ocrMatcher.locateText((OcrMatchingBy) params, cutoutSrcImg, debug);
        break;
      default:
        throw new UnsupportedOperationException(UNSUPPORTED_METHOD + params.getMethod());
    }
    return optMr.map(mr -> optCutoutRect.map(cutRect -> reverseCutout(mr, cutRect, tImgSize))
            .orElse(mr))
        .map(mr -> toElement(ImmutableList.of(params), mr));
  }

  protected List<ImgRecogElement> findElements(final ImgRecogBy params,
                                               final Mat queryImage,
                                               final Mat trainingImage)
  {
    final Size tImgSize = trainingImage.size();
    final Optional<Rect> optCutoutRect = Optional.ofNullable(params.getCutout())
        .map(cutout -> OpenCvImgUtils.proportionalToAbsoluteRect(tImgSize, cutout));
    final Mat cutoutSrcImg = optCutoutRect.map(rect -> OpenCvImgUtils.cutout(trainingImage, rect))
        .orElse(trainingImage);
    final List<? extends MatchResult> mrs;
    switch (params.getMethod())
    {
      case TEMPLATE_MATCHING:
        TemplateMatchingBy.verifyTemplateMatchingParams(params);
        mrs = OpenCvTemplateMatcher.findElementsSizeInvariant(queryImage, cutoutSrcImg,
            (TemplateMatchingBy) params, debug);
        break;
      case FEATURE_MATCHING:
        throw new UnsupportedOperationException("The 'feature matching' image recognition method, "
            + "called by, for instance, the annotations 'AndroidImageFeatureFindBy' and "
            + "'iOSImageFeatureFindBy', cannot be used to find lists of elements. It can only be "
            + "used to find single elements.");
      case OCR:
        OcrMatchingBy.verifyOcrParams(params);
        mrs = ocrMatcher.locateTexts((OcrMatchingBy) params, cutoutSrcImg, debug);
        break;
      default:
        throw new UnsupportedOperationException(UNSUPPORTED_METHOD + params.getMethod());
    }
    return mrs.stream()
        .map(mr -> optCutoutRect.map(cutoutRect -> reverseCutout(mr, cutoutRect, tImgSize))
            .orElse(mr))
        .map(mr -> toElement(ImmutableList.of(params), mr))
        .collect(ImmutableList.toImmutableList());
  }

  /**
   * Looks for an element inside another, usually bigger, picture, usually representing some sort of
   * video screen. Tries every locator in the locator list in succession. The match is only
   * considered as successful if all locators are successful, and if the centre of each matching
   * location is inside the perimeter of the previous matching location. That means the locators are
   * in a boolean AND relation to each other. If these conditions are fulfilled, the operation
   * returns the element. The operation return nothing only when all locators fail.
   *
   * @param trainingImage  the source or training image, where the element should be located
   * @param imgRecogByList list of element locators
   * @return an element, if it was found, or nothing.
   */
  protected Optional<ImgRecogElement> applySuccessiveLocators(final byte[] trainingImage,
                                                              final List<ImgRecogBy> imgRecogByList)
  {
    // Find the first match result using the first locator.
    final ImgRecogBy firstImgRecogBy = imgRecogByList.get(0);
    Optional<ImgRecogElement> mrOpt = findElement(firstImgRecogBy, trainingImage);
    // If there are multiple locators, then iterate over them, placing each successive match result
    // in lieu of any previous one.
    int i = 1;
    while (i < imgRecogByList.size())
    {
      final ImgRecogBy imgRecogBy = imgRecogByList.get(i);
      mrOpt = mrOpt.flatMap(mr0 -> findElement(imgRecogBy, trainingImage).map(mr1 -> {
        // If the centre of the Nth match is inside the rectangle of the (N-1)th match, then
        // the Nth match becomes the latest result and can be used in the next (N+1)th match
        // operation, or else break the operation cycle and consider the element as 'not found'.
        if (ImgUtils.isOneRectsCentreInsideTheOtherRectsBorders(
            mr1.getRect(), mr0.getRect()))
        {
          return mr1;
        }
        else
        {
          return null;
        }
      }));
      i++;
    }
    return mrOpt;
  }

  /**
   * Looks for an element inside another, usually bigger, picture, usually representing some sort of
   * video screen. Tries each locator in the locator list, until the element is found or until the
   * last locator in the list fails. That means the locators are in a boolean OR relation to each
   * other. It is sufficient that just one locator be successful, for the operation to return the
   * element successfully. The operation return nothing only when all locators fail.
   *
   * @param trainingImage  the source or training image, where the element should be located
   * @param imgRecogByList list of element locators
   * @return an element, if it was found, or nothing.
   */
  protected Optional<ImgRecogElement> applyAlternativeLocators(final byte[] trainingImage,
                                                               final List<ImgRecogBy> imgRecogByList)
  {
    Optional<ImgRecogElement> optElem = Optional.empty();
    int i = 0;
    while (!optElem.isPresent() && i < imgRecogByList.size())
    {
      optElem = findElement(imgRecogByList.get(i), trainingImage);
      i++;
    }
    return optElem;
  }

  protected ImgRecogElement toMobileOrWebElement(final Rectangle offsetRect,
                                                 final List<ImgRecogBy> imgRecogByList,
                                                 final Double score)
  {
    if (driver instanceof AppiumDriver)
    {
      return new ImgRecogMobileElement(
          offsetRect, imgRecogByList, score, (AppiumDriver) this.driver,
          this);
    }
    else
    {
      return new ImgRecogWebElement(
          offsetRect, imgRecogByList, score, this.driver, this);
    }
  }

  /**
   * It is possible that screen size, as concerns Appium interactions, is different from screen
   * resolution, as in the screenshot's dimensions. That is especially true in the case of Android
   * devices, with the added complication that Appium may have subtracted the status and navigation
   * bars' height from the full screen height. Then, corrections must be applied to the rectangle
   * representing the location of the element. This method applies these corrections, when needed,
   * and returns a usable screen element.
   *
   * @param imgRecogByList list of locators
   * @param mr             the locators' match result
   * @return a usable screen element
   */
  protected ImgRecogElement toElement(final List<ImgRecogBy> imgRecogByList,
                                      final MatchResult mr)
  {
    final Rectangle rect = mr.getRectangle();
    final Dimension srcImgSize = mr.getSrcImgDimension();
    final Rectangle correctedRect;
    if (ANDROID.matches(driver))
    {
      correctedRect = correctForAndroidScreenSize(rect, srcImgSize, driver);
    }
    else
    {
      correctedRect = correctForScreenSize(rect, srcImgSize, driver);
    }
    final Rectangle offsetRect = Optional.ofNullable(imgRecogByList.get(imgRecogByList.size() - 1)
            .getOffset())
        .map(offset -> calculateOffsetRect(correctedRect, offset, srcImgSize))
        .orElse(correctedRect);
    return toMobileOrWebElement(offsetRect, imgRecogByList, mr.getScore());
  }

  protected static Rectangle calculateOffsetRect(final Rectangle original,
                                                 final FractionalRectangle offset,
                                                 final Dimension srcImgSize)
  {
    int x = Math.round(original.x + offset.getX() * original.width);
    if (x < 0) {x = 0;}
    else if (x > srcImgSize.width) {x = srcImgSize.width;}

    int y = Math.round(original.y + offset.getY() * original.height);
    if (y < 0) {y = 0;}
    else if (y > srcImgSize.height) {y = srcImgSize.height;}

    int w = Math.round(offset.getWidth() * original.width);
    if (w < 0) {w = 0;}
    else if (w > srcImgSize.width) {w = srcImgSize.width;}
    if (x + w > srcImgSize.width) {w = w - 1;}

    int h = Math.round(offset.getHeight() * original.height);
    if (h < 0) {h = 0;}
    else if (h > srcImgSize.height) {h = srcImgSize.height;}
    if (y + h > srcImgSize.height) {h = h - 1;}

    if (x + w > srcImgSize.width)
    {
      throw new IllegalArgumentException(String.format("Coord x plus width must be less than or "
          + "equal to total screen width. Rect: (x=%d, y=%d, w=%d, h=%d); "
          + "Screen: (w=%d, h=%d).", x, y, w, h, srcImgSize.width, srcImgSize.height));
    }
    if (y + h > srcImgSize.height)
    {
      throw new IllegalArgumentException(String.format("Coord y plus height must be less than or "
          + "equal to total screen height. Rect: (x=%d, y=%d, w=%d, h=%d); "
          + "Screen: (w=%d, h=%d).", x, y, w, h, srcImgSize.width, srcImgSize.height));
    }

    return new Rectangle(x, y, h, w);
  }

  protected static Rectangle correctForScreenSize(final Rectangle rect,
                                                  final Dimension trainImgSize,
                                                  final WebDriver driver)
  {
    final Dimension windowSize = driver.manage().window().getSize();
    if (Objects.equals(trainImgSize, windowSize))
    {
      return rect;
    }
    else
    {
      return adjustRectToScreenSize(rect, trainImgSize, windowSize);
    }
  }

  protected Rectangle correctForAndroidScreenSize(final Rectangle rect,
                                                  final Dimension trainImgSize,
                                                  final WebDriver driver)
  {
    final Dimension windowSize = driver.manage().window().getSize();
    // If the image resolution is equal to Appium's window size, then return the original rectangle.
    if (Objects.equals(trainImgSize, windowSize))
    {
      return rect;
    }
    else
    {
      // Else, add the status and navigation bars' heights to the window size, to obtain an adjusted
      // window size.
      final JsonObject androidDetailsJson = (JsonObject) jsonParser.parse(
          ((HasAndroidDeviceDetails) driver).getSystemBars().toString());
      final JsonObject statusBarJson = (JsonObject) jsonParser.parse(
          androidDetailsJson.getAsJsonObject("statusBar").toString());
      final JsonObject navigationBarJson = (JsonObject) jsonParser.parse(
          androidDetailsJson.getAsJsonObject("navigationBar").toString());
      final int statusBarHeight = statusBarJson.get("height").getAsInt();
      final int navigationBarHeight = navigationBarJson.get("height").getAsInt();
      final Dimension windowSizePlusStatus = new Dimension(windowSize.width,
          windowSize.height + statusBarHeight);
      final Dimension windowSizePlusNav = new Dimension(windowSize.width,
          windowSize.height + navigationBarHeight);
      final Dimension windowSizePlusStatusAndNav = new Dimension(windowSize.width,
          windowSize.height + statusBarHeight + navigationBarHeight);
      if (Objects.equals(trainImgSize, windowSizePlusStatus))
      {
        // If the image's height is equal to the window's height plus the status bar's height, or
        // equal to the window's height plus both the status and navigation bars' height, then a
        // correction must be made: the status bar's height must be subtracted from the rectangle's
        // Y-coord.
        return new Rectangle(rect.x, rect.y - statusBarHeight, rect.height, rect.width);
      }
      else if (Objects.equals(trainImgSize, windowSizePlusNav) ||
          Objects.equals(trainImgSize, windowSizePlusStatusAndNav))
      {
        // If the image's height is equal to the window's height plus the navigation bar's height,
        // or equal to the window's height plus both the status and navigation bars' height, then
        // the original rectangle can be used without modifications.
        return rect;
      }
      else
      {
        // If the image resolution is not equal to the original window size, and not equal to the
        // adjusted window size, then the window must have a really wacky size, and the rectangle
        // will have to be thoroughly adjusted, coords and dimensions, all.
        return adjustRectToScreenSize(rect, trainImgSize, windowSize);
      }
    }
  }

  protected static Rectangle adjustRectToScreenSize(final Rectangle rect,
                                                    final Dimension trainImgSize,
                                                    final Dimension windowSize)
  {
    final float xProportion = ((float) windowSize.width) / trainImgSize.width;
    final float yProportion = ((float) windowSize.height) / trainImgSize.height;
    return new Rectangle(
        Math.round(xProportion * rect.x),
        Math.round(yProportion * rect.y),
        Math.round(yProportion * rect.height),
        Math.round(xProportion * rect.width)
    );
  }

  protected static MatchResult reverseCutout(final MatchResult mr,
                                             final Rect cutoutRect,
                                             final Size trainImgSize)
  {
    final Dimension trainImgDimension = new Dimension(
        (int) trainImgSize.width,
        (int) trainImgSize.height);
    final Rectangle correctedRect =
        OpenCvImgUtils.correctCutoutCoords(cutoutRect, mr.getRectangle());
    return mr.withRectangle(correctedRect).withSrcImgDimension(trainImgDimension);
  }

  @Override
  public void close()
  {
    if (nonNull(this.ocrMatcher))
    {
      this.ocrMatcher.close();
    }
  }

}
