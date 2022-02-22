package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.isNull;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import com.rkoyanagui.img_recog.Extensions;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.util.ImageIOHelper;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

class ReaderUtils
{

  protected static final String COULD_NOT_LOAD = "Could not load image from file: ";

  protected ReaderUtils()
  {
  }

  /**
   * Verifies that a {@link Mat} is not null and not empty.
   *
   * @param mat a matrix.
   * @return an {@link Optional} containing the same matrix, if not null and not empty. Otherwise,
   * {@link Optional#empty()}.
   */
  protected static <T extends Mat> Optional<T> maybeNotEmpty(final T mat)
  {
    if (isNull(mat) || mat.empty())
    {
      return Optional.empty();
    }
    return Optional.of(mat);
  }

  protected static byte[] readBytes(final String path)
  {
    final Mat img = imread(path);
    final MatOfByte mb = new MatOfByte();
    Imgcodecs.imencode(Extensions.PNG, img, mb);
    final byte[] sourceImage = mb.toArray();
    if (isNull(sourceImage) || sourceImage.length == 0)
    {throw new IllegalArgumentException(COULD_NOT_LOAD + path);}
    return sourceImage;
  }

  protected static Mat read(final String path)
  {
    final Mat sourceImage = imread(path);
    if (sourceImage.empty())
    {throw new IllegalArgumentException(COULD_NOT_LOAD + path);}
    return sourceImage;
  }

  protected static Mat readGray(final String path)
  {
    final Mat sourceImage = imread(path, Imgcodecs.IMREAD_GRAYSCALE);
    if (sourceImage.empty())
    {throw new IllegalArgumentException(COULD_NOT_LOAD + path);}
    return sourceImage;
  }

  protected static BufferedImage toBufferedImage(final Mat img)
  {
    final MatOfByte imgByteMat = new MatOfByte();
    Imgcodecs.imencode(Extensions.PNG, img, imgByteMat);
    final BufferedImage bi;
    try
    {
      bi = ImageIO.read(new ByteArrayInputStream(imgByteMat.toArray()));
    }
    catch (IOException x)
    {
      throw new IllegalArgumentException("Unable to read BufferedImage from: " + img, x);
    }
    return bi;
  }

  protected static BufferedImage toBufferedImage(final byte[] img)
  {
    final BufferedImage bi;
    try
    {
      bi = ImageIO.read(new ByteArrayInputStream(img));
    }
    catch (IOException x)
    {
      throw new IllegalArgumentException("Unable to read BufferedImage.", x);
    }
    return bi;
  }

  protected static List<IIOImage> toIIOImageList(final BufferedImage bi)
  {
    try
    {
      return ImageIOHelper.getIIOImageList(bi);
    }
    catch (IOException e)
    {
      throw new UncheckedIOException("Could not transform BufferedImage into List<IIOImage>.", e);
    }
  }

}
