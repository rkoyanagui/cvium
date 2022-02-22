package com.rkoyanagui.img_recog.impl;

import static org.opencv.imgcodecs.Imgcodecs.imencode;
import static org.opencv.imgproc.Imgproc.LINE_8;

import com.rkoyanagui.img_recog.ImgUtils;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.Rectangle;

class DisplayUtils
{

  protected DisplayUtils()
  {
  }

  protected static void displayInWindow(final String title,
                                        final Mat srcImg,
                                        final Mat templImg,
                                        final String ext,
                                        final List<String> comments)
  {
    final List<byte[]> byteArrayList = new ArrayList<>();
    final MatOfByte matOfByte1 = new MatOfByte();
    imencode(ext, srcImg, matOfByte1);
    byteArrayList.add(matOfByte1.toArray());
    final MatOfByte matOfByte2 = new MatOfByte();
    imencode(ext, templImg, matOfByte2);
    byteArrayList.add(matOfByte2.toArray());
    ImgUtils.displayInWindow(title, byteArrayList, comments);
  }

  protected static void displayInWindow(final String title,
                                        final List<Mat> imgs,
                                        final String ext,
                                        final List<String> comments)
  {
    final List<byte[]> byteArrayList = new ArrayList<>();
    for (final Mat img : imgs)
    {
      final MatOfByte matOfByte = new MatOfByte();
      imencode(ext, img, matOfByte);
      byteArrayList.add(matOfByte.toArray());
    }
    ImgUtils.displayInWindow(title, byteArrayList, comments);
  }

  protected static void displayInWindow(final String title,
                                        final Mat img,
                                        final String ext,
                                        final List<String> comments)
  {
    final MatOfByte matOfByte = new MatOfByte();
    imencode(ext, img, matOfByte);
    final byte[] byteArray = matOfByte.toArray();
    final List<byte[]> byteArrayList = new ArrayList<>();
    byteArrayList.add(byteArray);
    ImgUtils.displayInWindow(title, byteArrayList, comments);
  }

  protected static void displayInWindow(final String title, final Mat img, final String ext)
  {
    displayInWindow(title, img, ext, new ArrayList<>());
  }

  protected static Mat drawRectangle(final Mat img,
                                     final Rectangle rect,
                                     final Scalar colour)
  {
    final Mat drawnImg = img.clone();
    final List<MatOfPoint> matOfPoints = new ArrayList<>();
    matOfPoints.add(toMatOfPoint(rect));
    Imgproc.polylines(drawnImg, matOfPoints, true, colour, 2, LINE_8, 0);
    return drawnImg;
  }

  protected static Mat drawRectangles(final Mat img,
                                      final List<Rectangle> rects,
                                      final Scalar colour)
  {
    final Mat drawnImg = img.clone();
    final List<MatOfPoint> matOfPoints = new ArrayList<>();
    for (final Rectangle rect : rects)
    {
      matOfPoints.add(toMatOfPoint(rect));
    }
    Imgproc.polylines(drawnImg, matOfPoints, true, colour, 2, LINE_8, 0);
    return drawnImg;
  }

  protected static MatOfPoint toMatOfPoint(final Rectangle rect)
  {
    final Point p0 = new Point(rect.x, rect.y);
    final Point p1 = new Point((double) rect.x + rect.width, rect.y);
    final Point p2 = new Point((double) rect.x + rect.width, (double) rect.y + rect.height);
    final Point p3 = new Point(rect.x, (double) rect.y + rect.height);
    return new MatOfPoint(p0, p1, p2, p3);
  }

}
