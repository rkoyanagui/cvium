package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_DILATE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.MORPH_ERODE;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

class FilterUtils
{

  protected static final Size KERNEL_SIZE = new Size(2, 2);
  protected static final Scalar WHITE = Scalar.all(256);
  protected static final int CONTOUR_THRESH = 40;
  protected static final int THICKNESS = 1;

  protected FilterUtils()
  {
  }

  /**
   * Turns a colour image into a grayscale image.
   *
   * @param img the original image
   * @return the resulting grayscale image
   */
  protected static Mat gray(final Mat img)
  {
    final Mat img2 = new Mat();
    Imgproc.cvtColor(img, img2, COLOR_BGR2GRAY);
    return img2;
  }

  /**
   * If the image has less than 3 colour channels, then adds BGR channels to it. Else, leaves the
   * image unchanged.
   *
   * @param img the original image
   * @return an image with colour channels
   */
  protected static Mat backToColour(final Mat img)
  {
    final Mat backToColourImg = new Mat();
    if (img.channels() < 3)
    {
      Imgproc.cvtColor(img, backToColourImg, Imgproc.COLOR_GRAY2BGR);
      return backToColourImg;
    }
    else
    {
      return img;
    }
  }

  /**
   * Inverts every bit of an array. In the case of an image, that means creating a negative.
   *
   * @param img the original image
   * @return a negative of the image
   */
  protected static Mat negative(final Mat img)
  {
    final Mat neg = new Mat();
    Core.bitwise_not(img, neg);
    return neg;
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
  protected static Mat simpleThreshold(final Mat img, final double thresh, final double maxVal)
  {
    final Mat img2 = new Mat();
    Imgproc.threshold(img, img2, thresh, maxVal, THRESH_BINARY);
    return img2;
  }

  /**
   * Applies {@link #simpleThreshold(Mat, double, double)} with params: thresh=127.0, maxVal=255.0.
   */
  protected static Mat simpleThreshold(final Mat img)
  {
    return simpleThreshold(img, 127.0, 255.0);
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
  protected static Mat otsuThreshold(final Mat img, final double maxVal)
  {
    final Mat img2 = new Mat();
    Imgproc.threshold(img, img2, 0.0, maxVal, THRESH_OTSU);
    return img2;
  }

  /** Applies {@link #otsuThreshold(Mat, double)} with params: maxVal=255.0. */
  protected static Mat otsuThreshold(final Mat img)
  {
    return otsuThreshold(img, 255.0);
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
  protected static Mat adaptiveGaussianThreshold(final Mat img,
                                                 final double maxVal,
                                                 final int blockSize,
                                                 final int c)
  {
    final Mat img2 = new Mat();
    Imgproc.adaptiveThreshold(img, img2, maxVal, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY,
        blockSize, c);
    return img2;
  }

  /**
   * Applies {@link #adaptiveGaussianThreshold(Mat, double, int, int)} with params: maxVal=255.0,
   * blockSize=11, c=2.
   */
  protected static Mat adaptiveGaussianThreshold(final Mat img)
  {
    return adaptiveGaussianThreshold(img, 255.0, 11, 2);
  }

  /**
   * Smooths an image using a normalised box blur filter.
   *
   * @param img        the image to be processed
   * @param kernelSize the size of the blur-box
   * @return the resulting blurred image
   */
  protected static Mat normalisedBoxBlur(final Mat img, final Double kernelSize)
  {
    final double kSize = nonNull(kernelSize) ? kernelSize : 3.0;
    final Mat img2 = new Mat();
    Imgproc.blur(img, img2, new Size(kSize, kSize));
    return img2;
  }

  /** Applies {@link #normalisedBoxBlur(Mat, Double)} with params: kernelSize=3.0. */
  protected static Mat normalisedBoxBlur(final Mat img)
  {
    return normalisedBoxBlur(img, 3.0);
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
  protected static Mat gaussianBlur(final Mat img,
                                    final double width,
                                    final double height,
                                    final double sigmaX,
                                    final double sigmaY)
  {
    final Mat img2 = new Mat();
    Imgproc.GaussianBlur(img, img2, new Size(width, height), sigmaX, sigmaY);
    return img2;
  }

  /**
   * Applies {@link #gaussianBlur(Mat, double, double, double, double)} with params: width=3.0,
   * height=3.0, sigmaX=1.0, sigmaY=1.0.
   */
  protected static Mat gaussianBlur(final Mat img)
  {
    final Mat img2 = new Mat();
    Imgproc.GaussianBlur(img, img2, new Size(3.0, 3.0), 1.0, 1.0);
    return img2;
  }

  @SuppressWarnings("squid:S4276")
  protected static Function<Mat, Mat> accFilters(final ImageFilter[] filters)
  {
    Function<Mat, Mat> result = m -> m;
    if (nonNull(filters) && filters.length > 0)
    {
      BinaryOperator<Function<Mat, Mat>> accumulator = Function::andThen;
      for (final ImageFilter filter : filters)
      {
        result = accumulator.apply(result, filter.f);
      }
      return result;
    }
    return result;
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
    return detectCannyContours(src, null, null);
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
    return detectCannyContours(src, threshold, null);
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
    final int dThreshold = nonNull(threshold) ? threshold : CONTOUR_THRESH;
    final int dThickness = nonNull(thickness) ? thickness : THICKNESS;
    final Mat cannyOutput = new Mat();
    // Finds edges in an image using the Canny algorithm.
    // Edges are defined as points located between two areas with markedly different pixel intensities.
    Imgproc.Canny(src, cannyOutput, dThreshold, dThreshold * 2.0);
    final List<MatOfPoint> contours = new ArrayList<>();
    final Mat hierarchy = new Mat();
    // Finds contours in a binary image. It puts together the edge-points to form full lines.
    Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE,
        Imgproc.CHAIN_APPROX_SIMPLE);
    final Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC1);
    for (int i = 0; i < contours.size(); i++)
    {
      Imgproc.drawContours(drawing, contours, i, WHITE, dThickness, Imgproc.LINE_8, hierarchy, 0,
          new Point());
    }
    return drawing;
  }

  /**
   * Noise removal morphological operation. Pulls back (erodes) the borders of all foreground
   * (white) objects.
   *
   * @param img the original image
   * @return the transformed image
   */
  protected static Mat morphErode(final Mat img)
  {
    return morph(img, MORPH_ERODE);
  }

  /**
   * Noise removal morphological operation. Expands (dilates) the borders of all foreground (white)
   * objects.
   *
   * @param img the original image
   * @return the transformed image
   */
  protected static Mat morphDilate(final Mat img)
  {
    return morph(img, MORPH_DILATE);
  }

  /**
   * Noise removal morphological operation. Erosion followed by dilation. Useful in removing small
   * detached foreground (white) objects (dust motes).
   *
   * @param img the original image
   * @return the transformed image
   */
  protected static Mat morphOpen(final Mat img)
  {
    return morph(img, MORPH_OPEN);
  }

  /**
   * Noise removal morphological operation. Dilation followed by erosion. Useful in removing small
   * inscribed background (black) objects (pinholes).
   *
   * @param img the original image
   * @return the transformed image
   */
  protected static Mat morphClose(final Mat img)
  {
    return morph(img, MORPH_CLOSE);
  }

  /**
   * Performs some type of morphological transformation, using an ellipse kernel of size 2.
   *
   * @param img       the original image
   * @param morphType the type of morphological transformation
   * @return the transformed image
   */
  protected static Mat morph(final Mat img, final int morphType)
  {
    final Mat dst = new Mat();
    final Mat kernel = Imgproc.getStructuringElement(MORPH_ELLIPSE, KERNEL_SIZE);
    Imgproc.morphologyEx(img, dst, morphType, kernel);
    return dst;
  }

}
