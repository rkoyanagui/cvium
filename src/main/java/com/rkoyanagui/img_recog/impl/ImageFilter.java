package com.rkoyanagui.img_recog.impl;

import java.util.function.UnaryOperator;
import org.opencv.core.Mat;

public enum ImageFilter
{

  /** Turns a colour image into a grayscale image. */
  GRAY(OpenCvImgUtils::gray),
  /** See {@link OpenCvImgUtils#normalisedBoxBlur(Mat)}. */
  BOX_BLUR(OpenCvImgUtils::normalisedBoxBlur),
  /** See {@link OpenCvImgUtils#gaussianBlur(Mat)}. */
  GAUSSIAN_BLUR(OpenCvImgUtils::gaussianBlur),
  /** See {@link OpenCvImgUtils#simpleThreshold(Mat)}. */
  SIMPLE_BINARY_THRESHOLD(OpenCvImgUtils::simpleThreshold),
  /** See {@link OpenCvImgUtils#adaptiveGaussianThreshold(Mat)}. */
  ADAPTIVE_BINARY_THRESHOLD(OpenCvImgUtils::adaptiveGaussianThreshold),
  /** See {@link OpenCvImgUtils#otsuThreshold(Mat)}. */
  OTSU_BINARY_THRESHOLD(OpenCvImgUtils::otsuThreshold),
  /** See {@link OpenCvImgUtils#morphErode(Mat)}. */
  MORPH_ERODE(OpenCvImgUtils::morphErode),
  /** See {@link OpenCvImgUtils#morphDilate(Mat)}. */
  MORPH_DILATE(OpenCvImgUtils::morphDilate),
  /** See {@link OpenCvImgUtils#morphOpen(Mat)}. */
  MORPH_OPEN(OpenCvImgUtils::morphOpen),
  /** See {@link OpenCvImgUtils#morphClose(Mat)}. */
  MORPH_CLOSE(OpenCvImgUtils::morphClose),
  /** See {@link OpenCvImgUtils#detectCannyContours(Mat)} */
  CANNY_CONTOURS(OpenCvImgUtils::detectCannyContours),
  /** See {@link OpenCvImgUtils#negative(Mat)} */
  NEGATIVE(OpenCvImgUtils::negative);

  protected final UnaryOperator<Mat> f;

  ImageFilter(final UnaryOperator<Mat> f)
  {
    this.f = f;
  }

}
