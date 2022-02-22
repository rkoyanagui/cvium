package com.rkoyanagui.img_recog;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.Rectangle;

public interface ImgRecogEngine
{

  /**
   * Locates an element in an image, using a given image recognition method.
   *
   * @param params        parameters indicating how to find the element
   * @param trainingImage the image where the element should be found
   * @return the element, if it was found
   */
  Optional<ImgRecogElement> findElement(ImgRecogBy params, byte[] trainingImage);

  /**
   * Locates an element in an image, using a given image recognition method.
   *
   * @param params        parameters indicating how to find the element
   * @param queryImage    an image of the element to be queried inside the <i>trainingImage</i>
   * @param trainingImage the image where the element should be found
   * @return the element, if it was found
   */
  Optional<ImgRecogElement> findElement(ImgRecogBy params, byte[] queryImage, byte[] trainingImage);

  /**
   * Locates an element in an image, using a sequence of image recognition methods and parameters.
   * For each successive attempt a<sub>n</sub>, its centre must be inside the perimeter of the
   * previous attempt a<sub>n-1</sub>'s rectangle, otherwise the sequence is interrupted and
   * considered unsuccessful as a whole.
   *
   * @param paramList     a list of parameters indicating how to find the element
   * @param trainingImage the image where the element should be found
   * @return the element, if it was found
   */
  Optional<ImgRecogElement> findElement(List<ImgRecogBy> paramList, byte[] trainingImage);

  /**
   * Locates a sequence of elements in an image, using a given image recognition method.
   *
   * @param params        parameters indicating how to find the element
   * @param trainingImage the image where the element should be found
   * @return a sequence of elements, if any were found
   */
  List<ImgRecogElement> findElements(ImgRecogBy params, byte[] trainingImage);

  /**
   * Locates a sequence of elements in an image, using a given image recognition method.
   *
   * @param params        parameters indicating how to find the element
   * @param queryImage    an image of the element to be queried inside the <i>trainingImage</i>
   * @param trainingImage the image where the element should be found
   * @return a sequence of elements, if any were found
   */
  List<ImgRecogElement> findElements(ImgRecogBy params, byte[] queryImage, byte[] trainingImage);

  /**
   * Performs text recognition using <a href="https://tesseract-ocr.github.io/">Tesseract</a>.
   *
   * @param img       the image to be OCR'ed
   * @param rectangle the bounding box telling what area of the image should be OCR'ed
   * @return the text, if any was recognised.
   */
  Optional<String> recognise(BufferedImage img, Rectangle rectangle);

}
