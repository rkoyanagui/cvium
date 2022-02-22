package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.opencv.imgproc.Imgproc.INTER_AREA;

import com.rkoyanagui.img_recog.FractionalRectangle;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.Rectangle;

class SizeUtils
{

  protected SizeUtils()
  {
  }

  protected static Mat resize(final Mat img,
                              final Double scaleFactor,
                              final Integer interpolation)
  {
    final int inter = nonNull(interpolation) ? interpolation : INTER_AREA;
    final Mat resizedImg = new Mat();

    if (isNull(scaleFactor))
    {return img;}

    Imgproc.resize(img, resizedImg, new Size(), scaleFactor, scaleFactor, inter);
    return resizedImg;
  }

  protected static Rect proportionalToAbsoluteRect(final Size srcImgSize,
                                                   final FractionalRectangle fRect)
  {
    int x = Math.round(fRect.getX() * (float) srcImgSize.width);
    if (x < 0) {x = 0;}
    else if (x > srcImgSize.width) {x = (int) srcImgSize.width;}

    int y = Math.round(fRect.getY() * (float) srcImgSize.height);
    if (y < 0) {y = 0;}
    else if (y > srcImgSize.height) {y = (int) srcImgSize.height;}

    int w = Math.round(fRect.getWidth() * (float) srcImgSize.width);
    if (w < 0) {w = 0;}
    else if (w > srcImgSize.width) {w = (int) srcImgSize.width;}
    if (x + w > srcImgSize.width) {w = w - 1;}

    int h = Math.round(fRect.getHeight() * (float) srcImgSize.height);
    if (h < 0) {h = 0;}
    else if (h > srcImgSize.height) {h = (int) srcImgSize.height;}
    if (y + h > srcImgSize.height) {h = h - 1;}

    if (x + w > srcImgSize.width)
    {
      throw new IllegalArgumentException(String.format("Coord x plus width must be less than or "
          + "equal to total screen width. Rect: (x=%d, y=%d, w=%d, h=%d); "
          + "Screen: (w=%f, h=%f).", x, y, w, h, srcImgSize.width, srcImgSize.height));
    }
    if (y + h > srcImgSize.height)
    {
      throw new IllegalArgumentException(String.format("Coord y plus height must be less than or "
          + "equal to total screen height. Rect: (x=%d, y=%d, w=%d, h=%d); "
          + "Screen: (w=%f, h=%f).", x, y, w, h, srcImgSize.width, srcImgSize.height));
    }

    return new Rect(x, y, w, h);
  }

  protected static Mat cutout(final Mat img, final Rect rect)
  {
    return new Mat(img, rect);
  }

  protected static Rectangle correctCutoutCoords(final Rect cutoutRect,
                                                 final Rectangle elementRect)
  {
    return new Rectangle(
        cutoutRect.x + elementRect.x,
        cutoutRect.y + elementRect.y,
        elementRect.height,
        elementRect.width);
  }

  protected static java.awt.Rectangle fitRect(final Size srcImgSize,
                                              final java.awt.Rectangle rect)
  {
    // Making sure x is positive and less than the image's width.
    int x = rect.x;
    if (x < 0)
    {
      x = 0;
    }
    else if (x > srcImgSize.width)
    {
      x = (int) srcImgSize.width;
    }

    // Making sure width is positive and less than the image's width.
    int w = rect.width;
    if (w < 0)
    {
      w = 0;
    }
    else if (rect.width > srcImgSize.width)
    {
      w = (int) srcImgSize.width;
    }

    // If the right side of the rectangle clips through the right border of the image,
    // then reduce width by 1.
    if (x + w > srcImgSize.width)
    {
      w = w - 1;
    }
    // If that is not enough, then pinch off the offending area of the rectangle and leave only the
    // area that is inside the image's borders.
    if (x + w > srcImgSize.width)
    {
      w = (int) (srcImgSize.width - x - 1);
    }

    // Making sure y is positive and less than the image's height.
    int y = rect.y;
    if (y < 0)
    {
      y = 0;
    }
    else if (y > srcImgSize.height)
    {
      y = (int) srcImgSize.height;
    }

    // Making sure height is positive and less than the image's height.
    int h = rect.height;
    if (h < 0)
    {
      h = 0;
    }
    else if (h > srcImgSize.height)
    {
      h = (int) srcImgSize.height;
    }
    // If the bottom of the rectangle clips through the bottom of the image,
    // then reduce height by 1.
    if (y + h > srcImgSize.height)
    {
      h = h - 1;
    }
    // If that is not enough, then pinch off the offending area of the rectangle and leave only the
    // area that is inside the image's borders.
    if (y + h > srcImgSize.height)
    {
      w = (int) (srcImgSize.height - y - 1);
    }

    return new java.awt.Rectangle(x, y, w, h);
  }

  protected static List<java.awt.Rectangle> fitRects(final Size srcImgSize,
                                                     final List<java.awt.Rectangle> rects)
  {
    return rects.stream()
        .map(r -> fitRect(srcImgSize, r))
        .collect(Collectors.toList());
  }

  protected static Rectangle resizeRect(final Rectangle rect, final double factor)
  {
    final int x = (int) ((double) rect.x * factor);
    final int y = (int) ((double) rect.y * factor);
    final int width = (int) ((double) rect.width * factor);
    final int height = (int) ((double) rect.height * factor);
    return new Rectangle(x, y, height, width)
    {
      @Override
      public String toString()
      {
        return String.format("Rectangle(x=%d, y=%d, width=%d, height=%d)", x, y, width, height);
      }
    };
  }

  protected static Rectangle toSeleniumRect(final java.awt.Rectangle awtRect)
  {
    return new Rectangle(
        awtRect.x,
        awtRect.y,
        awtRect.height,
        awtRect.width
    )
    {
      @Override
      public String toString()
      {
        return String.format("Rectangle(x=%d, y=%d, width=%d, height=%d)", x, y, width, height);
      }
    };
  }

  protected static java.awt.Rectangle toAwtRect(final Rectangle seleniumRect)
  {
    return new java.awt.Rectangle(
        seleniumRect.x,
        seleniumRect.y,
        seleniumRect.width,
        seleniumRect.height
    );
  }

}
