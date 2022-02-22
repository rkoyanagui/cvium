package com.rkoyanagui.img_recog.impl;

import com.rkoyanagui.img_recog.FractionalRectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.imageio.IIOImage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.openqa.selenium.Rectangle;

/**
 * A utility class for processing images with OpenCV.
 */
public class OpenCvImgUtils
{

  protected static final Scalar RED = new Scalar(0.0, 0.0, 255.0);
  protected static final Scalar GREEN = new Scalar(0.0, 255.0, 0.0);
  protected static final Scalar BLUE = new Scalar(255.0, 127.0, 0.0);

  protected OpenCvImgUtils()
  {
  }

  /**
   * Verifies that a {@link Mat} is not null and not empty.
   *
   * @param mat a matrix.
   * @return an {@link Optional} containing the same matrix, if not null and not empty. Otherwise,
   * {@link Optional#empty()}.
   */
  public static <T extends Mat> Optional<T> maybeNotEmpty(final T mat)
  {
    return ReaderUtils.maybeNotEmpty(mat);
  }

  public static byte[] readBytes(final String path)
  {
    return ReaderUtils.readBytes(path);
  }

  public static Mat read(final String path)
  {
    return ReaderUtils.read(path);
  }

  public static Mat readGray(final String path)
  {
    return ReaderUtils.readGray(path);
  }

  public static BufferedImage toBufferedImage(final Mat img)
  {
    return ReaderUtils.toBufferedImage(img);
  }

  public static BufferedImage toBufferedImage(final byte[] img)
  {
    return ReaderUtils.toBufferedImage(img);
  }

  public static List<IIOImage> toIIOImageList(final BufferedImage bi)
  {
    return ReaderUtils.toIIOImageList(bi);
  }

  public static void displayInWindow(final String title,
                                     final Mat srcImg,
                                     final Mat templImg,
                                     final String ext,
                                     final List<String> comments)
  {
    DisplayUtils.displayInWindow(title, srcImg, templImg, ext, comments);
  }

  public static void displayInWindow(final String title,
                                     final List<Mat> imgs,
                                     final String ext,
                                     final List<String> comments)
  {
    DisplayUtils.displayInWindow(title, imgs, ext, comments);
  }

  public static void displayInWindow(final String title,
                                     final Mat img,
                                     final String ext,
                                     final List<String> comments)
  {
    DisplayUtils.displayInWindow(title, img, ext, comments);
  }

  public static void displayInWindow(final String title, final Mat img, final String ext)
  {
    DisplayUtils.displayInWindow(title, img, ext);
  }

  public static Mat drawRectangle(final Mat img,
                                  final Rectangle rect,
                                  final Scalar colour)
  {
    return DisplayUtils.drawRectangle(img, rect, colour);
  }

  public static Mat drawRectangles(final Mat img,
                                   final List<Rectangle> rects,
                                   final Scalar colour)
  {
    return DisplayUtils.drawRectangles(img, rects, colour);
  }

  public static Mat resize(final Mat img,
                           final Double scaleFactor,
                           final Integer interpolation)
  {
    return SizeUtils.resize(img, scaleFactor, interpolation);
  }

  public static Rect proportionalToAbsoluteRect(final Size srcImgSize,
                                                final FractionalRectangle fRect)
  {
    return SizeUtils.proportionalToAbsoluteRect(srcImgSize, fRect);
  }

  public static Mat cutout(final Mat img, final Rect rect)
  {
    return SizeUtils.cutout(img, rect);
  }

  public static Rectangle correctCutoutCoords(final Rect cutoutRect,
                                              final Rectangle elementRect)
  {
    return SizeUtils.correctCutoutCoords(cutoutRect, elementRect);
  }

  public static java.awt.Rectangle fitRect(final Size srcImgSize,
                                           final java.awt.Rectangle rect)
  {
    return SizeUtils.fitRect(srcImgSize, rect);
  }

  public static List<java.awt.Rectangle> fitRects(final Size srcImgSize,
                                                  final List<java.awt.Rectangle> rects)
  {
    return SizeUtils.fitRects(srcImgSize, rects);
  }

  public static Rectangle resizeRect(final Rectangle rect, final double factor)
  {
    return SizeUtils.resizeRect(rect, factor);
  }

  public static MatOfPoint toMatOfPoint(final Rectangle rect)
  {
    return DisplayUtils.toMatOfPoint(rect);
  }

  public static Rectangle toSeleniumRect(final java.awt.Rectangle awtRect)
  {
    return SizeUtils.toSeleniumRect(awtRect);
  }

  public static java.awt.Rectangle toAwtRect(final Rectangle seleniumRect)
  {
    return SizeUtils.toAwtRect(seleniumRect);
  }

  /**
   * Turns a colour image into a grayscale image.
   *
   * @param img the original image
   * @return the resulting grayscale image
   */
  public static Mat gray(final Mat img)
  {
    return FilterUtils.gray(img);
  }

  /**
   * If the image has less than 3 colour channels, then adds BGR channels to it. Else, leaves the
   * image unchanged.
   *
   * @param img the original image
   * @return an image with colour channels
   */
  public static Mat backToColour(final Mat img)
  {
    return FilterUtils.backToColour(img);
  }

  /**
   * Inverts every bit of an array. In the case of an image, that means creating a negative.
   *
   * @param img the original image
   * @return a negative of the image
   */
  public static Mat negative(final Mat img)
  {
    return FilterUtils.negative(img);
  }

  /**
   * Turns a grayscale image into a black and white image. Applies a simple global threshold of
   * {@code thresh}. Pixels with a value above that, are set to the white foreground. Otherwise,
   * they are set to the value {@code maxVal}.
   *
   * @param img    the image to be processed
   * @param thresh the threshold value, from 0.0 to 255.0
   * @param maxVal the value to assign when a pixel is above the threshold, from 0.0 to 255.0
   * @return the resulting binary image
   */
  public static Mat simpleThreshold(final Mat img, final double thresh, final double maxVal)
  {
    return FilterUtils.simpleThreshold(img, thresh, maxVal);
  }

  /**
   * Applies {@link #simpleThreshold(Mat, double, double)} with params: thresh=127.0, maxVal=255.0.
   */
  public static Mat simpleThreshold(final Mat img)
  {
    return FilterUtils.simpleThreshold(img);
  }

  /**
   * Turns a grayscale image into a black and white image. Calculates a global threshold from the
   * image's colour histogram using Otsu's algorithm. Advised for when the image is dominated by
   * just two regions with simple coloration.
   *
   * @param img    the image to be processed
   * @param maxVal the value to assign when a pixel is above the threshold, from 0.0 to 255.0
   * @return the resulting binary image
   */
  public static Mat otsuThreshold(final Mat img, final double maxVal)
  {
    return FilterUtils.otsuThreshold(img, maxVal);
  }

  /** Applies {@link #otsuThreshold(Mat, double)} with params: maxVal=255.0. */
  public static Mat otsuThreshold(final Mat img)
  {
    return FilterUtils.otsuThreshold(img);
  }

  /**
   * Turns a grayscale image into a black and white image. Dynamically calculates a different
   * threshold for each region of the image, using a Gaussian-weighted sum of a pixel's
   * neighbourhood values.
   *
   * @param img       the image to be processed
   * @param maxVal    the value to assign when a pixel is above the threshold, from 0.0 to 255.0
   * @param blockSize Size of a pixel neighborhood that is used to calculate a threshold value for
   *                  the pixel: 3, 5, 7, and so on.
   * @param c         Constant subtracted from the mean or weighted mean
   * @return the resulting binary image
   */
  public static Mat adaptiveGaussianThreshold(final Mat img,
                                              final double maxVal,
                                              final int blockSize,
                                              final int c)
  {
    return FilterUtils.adaptiveGaussianThreshold(img, maxVal, blockSize, c);
  }

  /**
   * Applies {@link #adaptiveGaussianThreshold(Mat, double, int, int)} with params: maxVal=255.0,
   * blockSize=11, c=2.
   */
  public static Mat adaptiveGaussianThreshold(final Mat img)
  {
    return FilterUtils.adaptiveGaussianThreshold(img);
  }

  /**
   * Smooths an image using a normalised box blur filter.
   *
   * @param img        the image to be processed
   * @param kernelSize the size of the blur-box
   * @return the resulting blurred image
   */
  public static Mat normalisedBoxBlur(final Mat img, final Double kernelSize)
  {
    return FilterUtils.normalisedBoxBlur(img, kernelSize);
  }

  /** Applies {@link #normalisedBoxBlur(Mat, Double)} with params: kernelSize=3.0. */
  public static Mat normalisedBoxBlur(final Mat img)
  {
    return FilterUtils.normalisedBoxBlur(img);
  }

  /**
   * Smooths an image using a Gaussian blur filter.
   *
   * @param img    the image to be processed
   * @param width  width of blur-kernel
   * @param height height of blur-kernel
   * @param sigmaX kernel standard deviation in the X-axis
   * @param sigmaY kernel standard deviation in the Y-axis
   * @return the resulting blurred image
   */
  public static Mat gaussianBlur(final Mat img,
                                 final double width,
                                 final double height,
                                 final double sigmaX,
                                 final double sigmaY)
  {
    return FilterUtils.gaussianBlur(img, width, height, sigmaX, sigmaY);
  }

  /**
   * Applies {@link #gaussianBlur(Mat, double, double, double, double)} with params: width=3.0,
   * height=3.0, sigmaX=1.0, sigmaY=1.0.
   */
  public static Mat gaussianBlur(final Mat img)
  {
    return FilterUtils.gaussianBlur(img);
  }

  /**
   * Convenience method, to compose a series of image filter functions. For example, given a series
   * of three functions, {@code f}, {@code g}, {@code h}, they will be composed into a single
   * function {@code h ∘ g ∘ f}.
   *
   * @param filters the image filter functions
   * @return a single accumulated function
   */
  @SuppressWarnings("squid:S4276")
  public static Function<Mat, Mat> accFilters(final ImageFilter[] filters)
  {
    return FilterUtils.accFilters(filters);
  }

  /**
   * Applies {@link #detectCannyContours(Mat, Integer, Integer)} with params: threshold=40,
   * thickness=1.
   *
   * @param src the original image
   * @return a drawing of the detected contours
   */
  public static Mat detectCannyContours(final Mat src)
  {
    return FilterUtils.detectCannyContours(src);
  }

  /**
   * Applies {@link #detectCannyContours(Mat, Integer, Integer)} with params: thickness=1.
   *
   * @param src       the original image
   * @param threshold minimum threshold for edge detection and linking, between 0 and 100.
   * @return a drawing of the detected contours
   */
  public static Mat detectCannyContours(final Mat src, final Integer threshold)
  {
    return FilterUtils.detectCannyContours(src, threshold);
  }

  /**
   * Detects an image's contours using the Canny algorithm.
   *
   * @param src       the original image
   * @param threshold minimum threshold for edge detection and linking, between 0 and 100.
   * @param thickness thickness of the drawn contours in the returned image
   * @return a drawing of the detected contours
   */
  public static Mat detectCannyContours(final Mat src,
                                        final Integer threshold,
                                        final Integer thickness)
  {
    return FilterUtils.detectCannyContours(src, threshold, thickness);
  }

  /**
   * Noise removal morphological operation. Pulls back (erodes) the borders of all foreground
   * (white) objects.
   *
   * @param img the original image
   * @return the transformed image
   */
  public static Mat morphErode(final Mat img)
  {
    return FilterUtils.morphErode(img);
  }

  /**
   * Noise removal morphological operation. Expands (dilates) the borders of all foreground (white)
   * objects.
   *
   * @param img the original image
   * @return the transformed image
   */
  public static Mat morphDilate(final Mat img)
  {
    return FilterUtils.morphDilate(img);
  }

  /**
   * Noise removal morphological operation. Erosion followed by dilation. Useful in removing small
   * detached foreground (white) objects (dust motes).
   *
   * @param img the original image
   * @return the transformed image
   */
  public static Mat morphOpen(final Mat img)
  {
    return FilterUtils.morphOpen(img);
  }

  /**
   * Noise removal morphological operation. Dilation followed by erosion. Useful in removing small
   * inscribed background (black) objects (pinholes).
   *
   * @param img the original image
   * @return the transformed image
   */
  public static Mat morphClose(final Mat img)
  {
    return FilterUtils.morphClose(img);
  }

  /**
   * Performs some type of morphological transformation, using an ellipse kernel of size 2.
   *
   * @param img       the original image
   * @param morphType the type of morphological transformation
   * @return the transformed image
   */
  public static Mat morph(final Mat img, final int morphType)
  {
    return FilterUtils.morph(img, morphType);
  }

}
