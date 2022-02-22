package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.MatchResult;
import org.opencv.core.Mat;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

class OcrMatchResult implements MatchResult
{

  protected final Rectangle rectangle;
  protected final Dimension srcImgDimension;
  protected final String searchTerm;
  protected final OcrTest ocrTest;
  protected final Float actualConfidenceLevel;
  protected final String actualText;
  protected final Mat drawnMatch;

  public OcrMatchResult(final Rectangle rectangle,
                        final Dimension srcImgDimension,
                        final String searchTerm,
                        final OcrTest ocrTest,
                        final Float actualConfidenceLevel,
                        final String actualText,
                        final Mat drawnMatch)
  {
    this.rectangle = rectangle;
    this.srcImgDimension = srcImgDimension;
    this.searchTerm = searchTerm;
    this.ocrTest = ocrTest;
    this.actualConfidenceLevel = actualConfidenceLevel;
    this.actualText = actualText;
    this.drawnMatch = drawnMatch;
  }

  public OcrMatchResult withRectangle(final Rectangle rectangle)
  {
    return new OcrMatchResult(
        rectangle,
        this.srcImgDimension,
        this.searchTerm,
        this.ocrTest,
        this.actualConfidenceLevel,
        this.actualText,
        this.drawnMatch);
  }

  public OcrMatchResult withSrcImgDimension(final Dimension srcImgDimension)
  {
    return new OcrMatchResult(
        this.rectangle,
        srcImgDimension,
        this.searchTerm,
        this.ocrTest,
        this.actualConfidenceLevel,
        this.actualText,
        this.drawnMatch);
  }

  protected OcrMatchResult withSearchTerm(final String searchTerm)
  {
    return new OcrMatchResult(
        this.rectangle,
        this.srcImgDimension,
        searchTerm,
        this.ocrTest,
        this.actualConfidenceLevel,
        this.actualText,
        this.drawnMatch);
  }

  protected OcrMatchResult withOcrTest(final OcrTest ocrTest)
  {
    return new OcrMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.searchTerm,
        ocrTest,
        this.actualConfidenceLevel,
        this.actualText,
        this.drawnMatch);
  }

  protected OcrMatchResult withActualConfidenceLevel(final Float actualConfidenceLevel)
  {
    return new OcrMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.searchTerm,
        this.ocrTest,
        actualConfidenceLevel,
        this.actualText,
        this.drawnMatch);
  }

  protected OcrMatchResult withActualText(final String actualText)
  {
    return new OcrMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.searchTerm,
        this.ocrTest,
        this.actualConfidenceLevel,
        actualText,
        this.drawnMatch);
  }

  protected OcrMatchResult withDrawnMatch(final Mat drawnMatch)
  {
    return new OcrMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.searchTerm,
        this.ocrTest,
        this.actualConfidenceLevel,
        this.actualText,
        drawnMatch);
  }

  @Override
  public Double getScore()
  {
    if (nonNull(actualConfidenceLevel))
    {return actualConfidenceLevel.doubleValue();}
    else
    {return 0.0;}
  }

  @Override
  public Rectangle getRectangle()
  {
    return rectangle;
  }

  @Override
  public Dimension getSrcImgDimension()
  {
    return srcImgDimension;
  }

  public String getSearchTerm()
  {
    return searchTerm;
  }

  public OcrTest getOcrTest()
  {
    return ocrTest;
  }

  public Float getActualConfidenceLevel()
  {
    return actualConfidenceLevel;
  }

  public String getActualText()
  {
    return actualText;
  }

  public Mat getDrawnMatch()
  {
    return drawnMatch;
  }

}
