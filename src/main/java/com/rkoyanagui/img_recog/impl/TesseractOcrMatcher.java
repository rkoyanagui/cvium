package com.rkoyanagui.img_recog.impl;

import static com.rkoyanagui.img_recog.impl.OpenCvImgUtils.toBufferedImage;
import static com.rkoyanagui.img_recog.impl.OpenCvImgUtils.toIIOImageList;
import static java.util.Objects.nonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.rkoyanagui.img_recog.Extensions;
import com.rkoyanagui.img_recog.OcrMatchingBy;
import com.rkoyanagui.img_recog.Padding;
import com.rkoyanagui.utils.Pair;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.IIOImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRotatedRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.utils.Converters;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TesseractOcrMatcher implements Closeable
{

  private static final Logger LOG = LoggerFactory.getLogger(TesseractOcrMatcher.class);
  protected static final String CONF_LAYER = "feature_fusion/Conv_7/Sigmoid";
  protected static final String BB_LAYER = "feature_fusion/concat_3";
  protected static final String LANGUAGE = "por+eng";
  protected static final String TESS_PATH = "tessdata/";
  protected static final String EAST_PATH = "tessdata/frozen_east_text_detection.pb";

  protected final ITesseract tesseract;
  protected final Net net;

  public TesseractOcrMatcher()
  {
    final String language = System.getProperty("tesseract.language", LANGUAGE);
    final String tessPath = System.getProperty("tesseract.datapath", TESS_PATH);
    final String eastPath = System.getProperty("east.datapath", EAST_PATH);
    this.tesseract = new Tesseract2(language, tessPath);
    this.net = Dnn.readNetFromTensorflow(eastPath);
  }

  /**
   * Performs text detection using <a href="https://github.com/argman/EAST">EAST</a>, and then
   * performs text recognition using <a href="https://tesseract-ocr.github.io/">Tesseract</a>.
   *
   * @param params      set of parameters for the OCR engine, and definition of what to consider as
   *                    a successful match
   * @param img         the image to be OCR'ed
   * @param isDebugMode if "debug" mode is enabled, then a window will pop up, displaying the search
   *                    results
   * @return maybe a successful match, maybe nothing
   */
  public Optional<OcrMatchResult> locateText(final OcrMatchingBy params,
                                             final Mat img,
                                             final boolean isDebugMode)
  {
    final Predicate<String> searchPredicate =
        params.getOcrTest().predicateFactory.apply(params.getSearchTerm());

    @SuppressWarnings("squid:S4276") final Function<String, String> cleanUp =
        composeTextCleaner(params.getCleanUp());

    final Mat filteredImg = OpenCvImgUtils.accFilters(params.getFilters()).apply(img);

    final float scoreThresh = params.getMinScore();
    final float nmsThresh = params.getNms();
    final float iouThresh = params.getIou();
    final Padding padding = params.getPadding();

    final List<Word> words = recognise(
        toBufferedImage(filteredImg),
        detect(filteredImg, scoreThresh, nmsThresh, iouThresh, padding)
    );

    final Optional<Word> bestMatch = words.stream()
        .filter(w -> searchPredicate.test(cleanUp.apply(w.getText())))
        .max(Comparator.comparing(Word::getConfidence));

    if (bestMatch.isPresent())
    {
      if (isDebugMode)
      {
        showResults(params, bestMatch.get(), filteredImg, true);
      }
      return Optional.of(toMatchResult(bestMatch.get(), filteredImg, params));
    }
    else if (isDebugMode)
    {
      showResults(params, words, filteredImg, false);
    }
    return Optional.empty();
  }

  /**
   * Performs text detection using <a href="https://github.com/argman/EAST">EAST</a>, and then
   * performs text recognition using <a href="https://tesseract-ocr.github.io/">Tesseract</a>.
   *
   * @param params      set of parameters for the OCR engine, and definition of what to consider as
   *                    a successful match
   * @param img         the image to be OCR'ed
   * @param isDebugMode if "debug" mode is enabled, then a window will pop up, displaying the search
   *                    results
   * @return a list of successful matches, or an empty list
   */
  public List<OcrMatchResult> locateTexts(final OcrMatchingBy params,
                                          final Mat img,
                                          final boolean isDebugMode)
  {
    final Predicate<String> searchPredicate =
        params.getOcrTest().predicateFactory.apply(params.getSearchTerm());

    @SuppressWarnings("squid:S4276") final Function<String, String> cleanUp =
        composeTextCleaner(params.getCleanUp());

    final Mat filteredImg = OpenCvImgUtils.accFilters(params.getFilters()).apply(img);

    final float scoreThresh = params.getMinScore();
    final float nmsThresh = params.getNms();
    final float iouThresh = params.getIou();
    final Padding padding = params.getPadding();
    final List<Word> words = recognise(
        toBufferedImage(filteredImg),
        detect(filteredImg, scoreThresh, nmsThresh, iouThresh, padding)
    );

    final List<Word> filteredWords = words.stream()
        .filter(w -> searchPredicate.test(cleanUp.apply(w.getText())))
        .collect(ImmutableList.toImmutableList());

    if (!filteredWords.isEmpty())
    {
      if (isDebugMode)
      {
        showResults(params, filteredWords, filteredImg, true);
      }
      return filteredWords.stream()
          .map(w -> toMatchResult(w, filteredImg, params))
          .collect(ImmutableList.toImmutableList());
    }
    else if (isDebugMode)
    {
      showResults(params, words, filteredImg, false);
    }
    return ImmutableList.of();
  }

  /**
   * Performs text detection using <a href="https://github.com/argman/EAST">EAST</a>.
   *
   * @param img         the image to be OCR'ed
   * @param scoreThresh the minimum text detection confidence score
   * @param nmsThresh   the Non-Maximum Suppression bounding box overlap threshold
   * @param iouThresh   the intersection-over-union bounding box overlap threshold
   * @param padding     to add padding to the boxes' sides
   * @return a list of confidence scores and bounding boxes of areas where text was detected
   */
  public List<Pair<Float, java.awt.Rectangle>> detect(final Mat img,
                                                      final float scoreThresh,
                                                      final float nmsThresh,
                                                      final float iouThresh,
                                                      final Padding padding)
  {
    // width and height must be multiples of 32
    final Size size = new Size(640, 640);
    // Height of the output geometry and score matrices.
    // Geometry has 4 vertically stacked maps. Score has 1.
    final int H = (int) (size.height / 4);
    // Mean RGB intensity of the images used to train the frozen NN. It must be subtracted from the
    // new image to get accurate results.
    final Scalar meanRgb = new Scalar(123.68, 116.78, 103.94);
    final Mat blob = Dnn.blobFromImage(img, 1.0, size, meanRgb, true, false);
    net.setInput(blob);
    // define the two output layer names for the EAST detector model that
    // we are interested -- the first is the output probabilities and the
    // second can be used to derive the bounding box coordinates of text
    final List<Mat> outs = new ArrayList<>(2);
    final List<String> outNames = new ArrayList<>(2);
    outNames.add(CONF_LAYER);
    outNames.add(BB_LAYER);
    net.forward(outs, outNames);

    // Decode predicted bounding boxes.
    // How to reshape an n-dimensional matrix down to 2d:
    // http://answers.opencv.org/question/175676/javaandroid-access-4-dim-mat-planes/
    // Geometry, as far as I can understand it, is a 5d blob: 1 layer of angle data,
    // and 4 layers of polar coordinates that need to be converted to regular x,y coordinates.
    // In order to facilite this conversion, first geometry is reshaped into a 2d blob,
    // where the original layers are stacked on top of one another and can be easily subdivided.
    final Mat scores = outs.get(0).reshape(1, H);
    final Mat geometry = outs.get(1).reshape(1, 5 * H);
    // Filters out all boxes whose score was below the threshold.
    final Pair<List<Float>, List<RotatedRect>> scoresAndBoxes =
        decode(scores, geometry, scoreThresh);

    // Apply non-maximum suppression procedure.
    final MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(scoresAndBoxes.a));
    final Float[] scoreArray = scoresAndBoxes.a.toArray(new Float[0]);
    final RotatedRect[] boxArray = scoresAndBoxes.b.toArray(new RotatedRect[0]);
    final MatOfRotatedRect boxes = new MatOfRotatedRect(boxArray);
    final MatOfInt indices = new MatOfInt();
    Dnn.NMSBoxesRotated(boxes, confidences, scoreThresh, nmsThresh, indices);

    // The output geometry is smaller than the original image.
    // So we apply a ratio to get everything back to the original proportions.
    final Point ratio = new Point(
        (float) img.cols() / size.width,
        (float) img.rows() / size.height);

    final int[] indexArray = indices.toArray();
    final Size frameSize = img.size();
    final List<Pair<Float, java.awt.Rectangle>> rects = new ArrayList<>(indexArray.length);

    final double lp = padding.getLeft();
    final double rp = padding.getRight();
    final double tp = padding.getTop();
    final double bp = padding.getBottom();

    for (final int index : indexArray)
    {
      final Float score = scoreArray[index];
      final RotatedRect rotatedRect = boxArray[index];
      final java.awt.Rectangle rect = toRect(rotatedRect, ratio, lp, rp, tp, bp);
      final java.awt.Rectangle fittedRect = OpenCvImgUtils.fitRect(frameSize, rect);
      rects.add(new Pair<>(score, fittedRect));
    }

    mergeRects(rects, iouThresh);
    // Sorts the boxes according to their upper left corner x,y coordinates.
    return rects.stream()
        .sorted(Comparator.comparing((Pair<Float, java.awt.Rectangle> p) -> p.b.y)
            .thenComparing(p -> p.b.x))
        .collect(Collectors.toList());
  }

  /**
   * Performs text recognition using <a href="https://tesseract-ocr.github.io/">Tesseract</a>.
   *
   * @param img                 the image to be OCR'ed
   * @param scoresAndRectangles a list of score, location pairs where text has been detected
   * @return a list of located words. Each {@link Word} contains text, score, and bounding box.
   */
  public List<Word> recognise(final BufferedImage img,
                              final List<Pair<Float, java.awt.Rectangle>> scoresAndRectangles)
  {
    final List<IIOImage> iioImages = toIIOImageList(img);

    // Tesseract tries to recognise the text found by EAST.
    final List<Word> words = new ArrayList<>(scoresAndRectangles.size());
    for (int i = 0; i < scoresAndRectangles.size(); i++)
    {
      final Pair<Float, java.awt.Rectangle> p = scoresAndRectangles.get(i);
      try
      {
        final String text = tesseract.doOCR(iioImages, p.b);
        LOG.debug("word[{}]={}", i, text);
        final String trimmed = nonNull(text) ? text.trim() : null;
        words.add(new Word(trimmed, p.a, p.b));
      }
      catch (TesseractException x)
      {
        LOG.error("Could not parse text from the given image! (i={})", i, x);
      }
    }

    // EAST+Tesseract, with one-time init, using the FAST NN, on an image with 14 text boxes,
    // took ~2.0s, or 0.15s per box.
    // It barely lost any precision as compared to the BEST NN. And it beats pure Tesseract in text
    // detection and recognition. (Getting the rectangles as tightly fit as possible round the text
    // is very important for better text recognition.)

    return words;
  }

  /**
   * Performs text recognition using <a href="https://tesseract-ocr.github.io/">Tesseract</a>.
   *
   * @param img       the image to be OCR'ed
   * @param rectangle the bounding box telling what area of the image should be OCR'ed
   * @return the text, if any was recognised.
   */
  public Optional<String> recognise(final BufferedImage img, final java.awt.Rectangle rectangle)
  {
    final List<IIOImage> iioImages = toIIOImageList(img);

    try
    {
      final String text = tesseract.doOCR(iioImages, rectangle);
      LOG.debug("OCR'ed text={}", text);
      return Optional.ofNullable(text);
    }
    catch (TesseractException x)
    {
      LOG.error("Could not parse text from the given image!", x);
    }
    return Optional.empty();
  }

  /**
   * Traces the lines of a rectangle, standing straight, around the original rotated rectangle. Also
   * resizes the resulting bounding box according to a given ratio. And finally adds some padding to
   * the rectangle's sides.
   *
   * @param rotatedRect   the rotated rectangle that is to be 'boxed in'
   * @param ratio         the ratio for the resizing operation
   * @param leftPadding   an extension to the left
   * @param rightPadding  an extension to the right
   * @param topPadding    an extension to the top
   * @param bottomPadding an extension to the bottom
   * @return a bounding box, with adjustments
   */
  protected static java.awt.Rectangle toRect(final RotatedRect rotatedRect,
                                             final Point ratio,
                                             final double leftPadding,
                                             final double rightPadding,
                                             final double topPadding,
                                             final double bottomPadding)
  {
    final Rect rect = rotatedRect.boundingRect();
    final double correctedWidth = rect.width * ratio.x;
    final double correctedHeight = rect.height * ratio.y;
    final double lp = leftPadding * correctedWidth;
    final double rp = rightPadding * correctedWidth;
    final double tp = topPadding * correctedHeight;
    final double bp = bottomPadding * correctedHeight;
    final int x = (int) Math.floor(rect.x * ratio.x - lp);
    final int y = (int) Math.floor(rect.y * ratio.y - tp);
    final int w = (int) Math.floor(correctedWidth + lp + rp);
    final int h = (int) Math.floor(correctedHeight + tp + bp);
    return new java.awt.Rectangle(x, y, w, h);
  }

  /**
   * If the ratio of ioU (intersection over union) between two rectangles' areas is equal or greater
   * than a given threshold, then a merger ensues. From the two rectangles' coordinates, the mininum
   * top left corner and the maximum bottom right corner are picked, to form the coordinates of a
   * new merged rectangle, that is then returned. Otherwise, returns an empty optional.
   *
   * @param r1        the first rectangle
   * @param r2        the second rectangle
   * @param iouThresh the IoU threshold
   * @return a merged rectangle, or nothing
   */
  protected static Optional<java.awt.Rectangle> mergeRects(final java.awt.Rectangle r1,
                                                           final java.awt.Rectangle r2,
                                                           final double iouThresh)
  {
    final double area1 = (double) r1.width * r1.height;
    final double area2 = (double) r2.width * r2.height;
    final int maxTL_X = Math.max(r1.x, r2.x);
    final int maxTL_Y = Math.max(r1.y, r2.y);
    final int minBR_X = Math.min(r1.x + r1.width, r2.x + r2.width);
    final int minBR_Y = Math.min(r1.y + r1.height, r2.y + r2.height);
    final int intersectionWidth = Math.max(0, minBR_X - maxTL_X);
    final int intersectionHeight = Math.max(0, minBR_Y - maxTL_Y);
    final double intersectionArea = (double) intersectionWidth * intersectionHeight;
    final double unionArea = area1 + area2 - intersectionArea;
    final double iou = intersectionArea / unionArea;
    if (iou >= iouThresh)
    {
      final int x = Math.min(r1.x, r2.x);
      final int y = Math.min(r1.y, r2.y);
      final int w = r1.width + r2.width - intersectionWidth;
      final int h = r1.height + r2.height - intersectionHeight;
      return Optional.of(new java.awt.Rectangle(x, y, w, h));
    }
    else
    {
      return Optional.empty();
    }
  }

  /**
   * Iterates over a list of rectangles, merging two or more of them whenever their IoU ratio
   * exceeds the threshold, and otherwise leaving the rest undisturbed.
   *
   * @param rects     the list of rectangles
   * @param iouThresh the IoU threshold
   */
  protected static void mergeRects(final List<Pair<Float, java.awt.Rectangle>> rects,
                                   final double iouThresh)
  {
    int i = 0;
    while (i < rects.size() - 1)
    {
      final Pair<Float, java.awt.Rectangle> p1 = rects.get(i);
      final java.awt.Rectangle r1 = p1.b;
      int j = i + 1;
      while (j < rects.size())
      {
        final Pair<Float, java.awt.Rectangle> p2 = rects.get(j);
        final java.awt.Rectangle r2 = p2.b;
        final Optional<java.awt.Rectangle> optionalRect = mergeRects(r1, r2, iouThresh);
        if (optionalRect.isPresent())
        {
          final float minScore = Math.min(p1.a, p2.a);
          rects.remove(j);
          rects.set(i, new Pair<>(minScore, optionalRect.get()));
          i--;
          break;
        }
        else
        {
          j++;
        }
      }
      i++;
    }
  }

  /**
   * Filters and keeps the shapes whose score exceeds the threshold, then saves this to a list of
   * confidence values and a list of bounding boxes.
   *
   * @param srcScores   the raw score matrix
   * @param srcGeometry the raw rotated rectangles matrix
   * @param scoreThresh the score threshold, or lower bound for the confidence values
   * @return a list of scores paired with a corresponding list of boxes
   */
  protected static Pair<List<Float>, List<RotatedRect>> decode(final Mat srcScores,
                                                               final Mat srcGeometry,
                                                               final float scoreThresh)
  {
    final List<Float> dstScores = new ArrayList<>();
    final List<RotatedRect> dstBoxes = new ArrayList<>();

    // size of 1 geometry plane
    final int W = srcGeometry.cols();
    final int H = srcGeometry.rows() / 5;

    for (int y = 0; y < H; ++y)
    {
      final Mat scoresData = srcScores.row(y);
      final Mat x0Data = srcGeometry.submat(0, H, 0, W).row(y);
      final Mat x1Data = srcGeometry.submat(H, 2 * H, 0, W).row(y);
      final Mat x2Data = srcGeometry.submat(2 * H, 3 * H, 0, W).row(y);
      final Mat x3Data = srcGeometry.submat(3 * H, 4 * H, 0, W).row(y);
      final Mat anglesData = srcGeometry.submat(4 * H, 5 * H, 0, W).row(y);

      for (int x = 0; x < W; ++x)
      {
        final double score = scoresData.get(0, x)[0];
        if (score >= scoreThresh)
        {
          // Maybe as a result of how CNNs work, the output layer is 4x smaller than the input layer
          // so we have to undo that here.
          final double offsetX = x * 4.0;
          final double offsetY = y * 4.0;
          final double angle = anglesData.get(0, x)[0];
          final double cosA = Math.cos(angle);
          final double sinA = Math.sin(angle);
          final double x0 = x0Data.get(0, x)[0];
          final double x1 = x1Data.get(0, x)[0];
          final double x2 = x2Data.get(0, x)[0];
          final double x3 = x3Data.get(0, x)[0];
          final double h = x0 + x2;
          final double w = x1 + x3;
          final Point offset = new Point(offsetX + cosA * x1 + sinA * x2,
              offsetY - sinA * x1 + cosA * x2);
          final Point p1 = new Point(-1 * sinA * h + offset.x, -1 * cosA * h + offset.y);
          final Point p3 = new Point(-1 * cosA * w + offset.x, sinA * w + offset.y);
          final RotatedRect r = new RotatedRect(
              new Point(0.5 * (p1.x + p3.x), 0.5 * (p1.y + p3.y)),
              new Size(w, h),
              -1 * angle * 180 / Math.PI);
          dstScores.add((float) score);
          dstBoxes.add(r);
        }
      }
    }
    return new Pair<>(dstScores, dstBoxes);
  }

  @SuppressWarnings("squid:S4276")
  protected static Function<String, String> composeTextCleaner(final OcrCleanUp[] cleanUps)
  {
    final Function<String, String> result;
    if (nonNull(cleanUps) && cleanUps.length > 0)
    {
      result = Stream.of(cleanUps)
          .map(o -> o.f)
          .reduce(s -> s, (f, g) -> f.andThen(g));
    }
    else
    {
      result = s -> s;
    }
    return result;
  }

  protected static void showResults(final OcrMatchingBy params,
                                    final List<Word> words,
                                    final Mat img,
                                    final boolean success)
  {
    final Scalar colour;
    if (success)
    {
      colour = OpenCvImgUtils.GREEN;
    }
    else
    {
      colour = OpenCvImgUtils.RED;
    }
    final Mat drawnImg;
    if (!words.isEmpty())
    {
      drawnImg = drawTextRects(words, img, colour);
    }
    else
    {
      drawnImg = img;
    }
    visualise(params, words, drawnImg);
  }

  protected static void showResults(final OcrMatchingBy params,
                                    final Word word,
                                    final Mat img,
                                    final boolean success)
  {
    final List<Word> words = ImmutableList.of(word);
    showResults(params, words, img, success);
  }

  protected static OcrMatchResult toMatchResult(final Word w,
                                                final Mat drawnImg,
                                                final OcrMatchingBy params)
  {
    final Rectangle rect = OpenCvImgUtils.toSeleniumRect(w.getBoundingBox());
    final Dimension size = new Dimension(drawnImg.width(), drawnImg.height());
    return new OcrMatchResult(
        rect,
        size,
        params.getSearchTerm(),
        params.getOcrTest(),
        w.getConfidence(),
        w.getText(),
        drawnImg);
  }

  protected static Mat drawTextRects(final List<Word> words, final Mat img, final Scalar colour)
  {
    final Mat drawnImg = OpenCvImgUtils.backToColour(img);
    final List<Rectangle> rects = words.stream()
        .map(w -> OpenCvImgUtils.toSeleniumRect(w.getBoundingBox()))
        .collect(ImmutableList.toImmutableList());
    return OpenCvImgUtils.drawRectangles(drawnImg, rects, colour);
  }

  protected static void visualise(final OcrMatchingBy params,
                                  final List<Word> words,
                                  final Mat img)
  {
    final Builder<String> builder = ImmutableList.builder();
    builder.add(params.toString());
    for (final Word word : words)
    {
      builder.add(String.format("text: \"%s\", rect: %s, confidence: %f",
          word.getText(), word.getBoundingBox(), word.getConfidence()));
    }
    final List<String> comments = builder.build();
    OpenCvImgUtils.displayInWindow("DEBUG Img Recog OCR",
        img, Extensions.PNG, comments);
  }

  /**
   * Call this method once you're completely done and will not do any OCR anymore, to release all
   * resources associated with the native Tesseract API.
   */
  @Override
  public void close()
  {
    if (nonNull(this.tesseract) && this.tesseract instanceof Tesseract2)
    {
      ((Tesseract2) this.tesseract).actuallyDispose();
    }
  }

}
