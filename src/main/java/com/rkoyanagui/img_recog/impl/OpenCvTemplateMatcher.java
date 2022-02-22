package com.rkoyanagui.img_recog.impl;

import com.google.common.collect.ImmutableList;
import com.rkoyanagui.img_recog.Extensions;
import com.rkoyanagui.img_recog.ImgUtils;
import com.rkoyanagui.img_recog.TemplateMatchingBy;
import com.rkoyanagui.utils.Pair;
import com.rkoyanagui.utils.Triplet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

class OpenCvTemplateMatcher
{

  protected OpenCvTemplateMatcher()
  {
  }

  protected static TemplateMatchResult matchTemplate(final Mat templateImage,
                                                     final Mat sourceImage)
  {
    final Mat srcImg2 = sourceImage.clone();
    final Mat result = new Mat(srcImg2.rows() - templateImage.rows() + 1,
        srcImg2.cols() - templateImage.cols() + 1, CvType.CV_32FC1);
    Imgproc.matchTemplate(srcImg2, templateImage, result, Imgproc.TM_CCOEFF_NORMED);
    final MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result, new Mat());
    final Point matchLoc = minMaxLocResult.maxLoc;
    final double matchVal = minMaxLocResult.maxVal;

    final int x = (int) Math.round(matchLoc.x);
    final int y = (int) Math.round(matchLoc.y);
    final int width = templateImage.cols();
    final int height = templateImage.rows();
    final Rectangle rectangle = new Rectangle(x, y, height, width)
    {
      @Override
      public String toString()
      {
        return String.format("Rectangle(x=%d, y=%d, width=%d, height=%d)", x, y, width, height);
      }
    };
    return new TemplateMatchResult(rectangle, matchVal);
  }

  @SuppressWarnings("squid:S4276")
  protected static List<TemplateMatchResult> matchTemplateSizeInvariant(final Mat templateImage,
                                                                        final Mat sourceImage,
                                                                        final TemplateMatchingBy params)
  {
    final Mat templImg2 = OpenCvImgUtils.accFilters(params.getPreResizingFilters())
        .apply(templateImage);
    final Mat srcImg2 = OpenCvImgUtils.accFilters(params.getPreResizingFilters())
        .apply(sourceImage);
    final double resizeFactor = params.getResizeFactor();
    final int resizeAttempts = params.getResizeMaxAttempts();
    final Function<Mat, Mat> postResizeFunct =
        OpenCvImgUtils.accFilters(params.getPostResizingFilters());
    return ImgUtils.generatePowerSequencePair(resizeFactor, resizeAttempts)
        .stream()
        .map(fp -> new Triplet<>(fp.a, OpenCvImgUtils.resize(templImg2, fp.a, null),
            OpenCvImgUtils.resize(srcImg2, fp.b, null)))
        .filter(mt -> mt.b.width() <= mt.c.width() && mt.b.height() <= mt.c.height())
        .map(mt -> new Triplet<>(mt.a, postResizeFunct.apply(mt.b), postResizeFunct.apply(mt.c)))
        .map(mt -> new Pair<>(mt.a, matchTemplate(mt.b, mt.c).withResizeFactor(Math.pow(mt.a, 2))))
        .map(mp -> mp.b.withRectangle(OpenCvImgUtils.resizeRect(mp.b.rectangle, mp.a)))
        .sorted(Comparator.<TemplateMatchResult>comparingDouble(r -> r.matchCoeff).reversed())
        .collect(ImmutableList.toImmutableList());
  }

  protected static Optional<TemplateMatchResult> findElementSizeInvariant(final Mat templateImage,
                                                                          final Mat sourceImage,
                                                                          final TemplateMatchingBy params,
                                                                          final boolean visualise)
  {
    final Double matchThreshold = params.getMatchThreshold();
    final Size srcSize = sourceImage.size();
    final Dimension srcDimension = new Dimension((int) srcSize.width, (int) srcSize.height);
    final List<TemplateMatchResult> matchResults =
        matchTemplateSizeInvariant(templateImage, sourceImage, params);
    if (matchResults.isEmpty())
    {return Optional.empty();}
    final TemplateMatchResult mr = matchResults.get(0)
        .withMatchThreshold(matchThreshold)
        .withSrcImgDimension(srcDimension)
        .withTemplate(templateImage)
        .withDrawnMatch(sourceImage);
    if (visualise) {visualise(params, mr);}
    if (mr.matchCoeff >= mr.matchThreshold) {return Optional.of(mr);}
    return Optional.empty();
  }

  protected static List<TemplateMatchResult> findElementsSizeInvariant(final Mat templateImage,
                                                                       final Mat sourceImage,
                                                                       final TemplateMatchingBy params,
                                                                       final boolean visualise)
  {
    final Double matchThreshold = params.getMatchThreshold();
    final Size srcSize = sourceImage.size();
    final Dimension srcDimension = new Dimension((int) srcSize.width, (int) srcSize.height);
    final List<TemplateMatchResult> mrs =
        matchTemplateSizeInvariant(templateImage, sourceImage, params)
            .stream()
            .map(mr -> mr.withMatchThreshold(matchThreshold)
                .withSrcImgDimension(srcDimension)
                .withTemplate(templateImage)
                .withDrawnMatch(sourceImage))
            .collect(ImmutableList.toImmutableList());
    final List<TemplateMatchResult> positiveMrs = mrs.stream()
        .filter(mr -> mr.matchCoeff >= mr.matchThreshold)
        .collect(ImmutableList.toImmutableList());
    if (!positiveMrs.isEmpty())
    {
      if (visualise) {visualise(params, positiveMrs, OpenCvImgUtils.GREEN);}
      return positiveMrs;
    }
    else if (!mrs.isEmpty())
    {
      if (visualise) {visualise(params, mrs, OpenCvImgUtils.RED);}
      return mrs;
    }
    return ImmutableList.of();
  }

  protected static Mat drawResult(final Mat filteredImg,
                                  final Rectangle rect)
  {
    final Mat backToColourImg = OpenCvImgUtils.backToColour(filteredImg);
    return OpenCvImgUtils.drawRectangle(backToColourImg, rect, OpenCvImgUtils.GREEN);
  }

  protected static void visualise(final TemplateMatchingBy params,
                                  final TemplateMatchResult plainMr)
  {
    final TemplateMatchResult mr
        = plainMr.withDrawnMatch(drawResult(plainMr.drawnMatch, plainMr.rectangle));
    final List<String> comments = new ArrayList<>();
    comments.add(params.toString());
    comments.add(mr.rectangle.toString());
    comments.add("matchCoeff: " + mr.matchCoeff);
    comments.add("resizeFactor: " + mr.resizeFactor);
    final boolean isOverTheThreshold = mr.matchCoeff >= mr.matchThreshold;
    comments.add(String.format("isOverTheThreshold: %b", isOverTheThreshold));
    OpenCvImgUtils.displayInWindow("DEBUG Img Recog Templ Match",
        mr.drawnMatch, mr.template, Extensions.PNG, comments);
  }

  protected static Mat drawResults(final Mat filteredImg,
                                   final List<Rectangle> rects,
                                   final Scalar colour)
  {
    final Mat backToColourImg = OpenCvImgUtils.backToColour(filteredImg);
    return OpenCvImgUtils.drawRectangles(backToColourImg, rects, colour);
  }

  protected static void visualise(final TemplateMatchingBy params,
                                  final List<TemplateMatchResult> mrs,
                                  final Scalar colour)
  {
    final List<Rectangle> rects = mrs.stream()
        .map(mr -> mr.rectangle)
        .collect(ImmutableList.toImmutableList());
    final Mat drawnMatch = drawResults(mrs.get(0).drawnMatch, rects, colour);
    final List<String> comments = new ArrayList<>();
    comments.add(params.toString());
    for (final TemplateMatchResult mr : mrs)
    {
      comments.add("--------------");
      comments.add(mr.rectangle.toString());
      comments.add("matchCoeff: " + mr.matchCoeff);
      comments.add("resizeFactor: " + mr.resizeFactor);
    }
    OpenCvImgUtils.displayInWindow("DEBUG Img Recog Templ Match",
        drawnMatch, mrs.get(0).template, Extensions.PNG, comments);
  }

}
