package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.MatchResult;
import org.opencv.core.Mat;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

class FeatureMatchResult implements MatchResult
{

  protected final Rectangle rectangle;
  protected final Dimension srcImgDimension;
  protected final Integer actualNumOfMatches;
  protected final Integer minNumOfMatches;
  protected final Mat drawnMatch;

  protected FeatureMatchResult(final Rectangle rectangle,
                               final Dimension srcImgDimension,
                               final Integer actualNumOfMatches,
                               final Integer minNumOfMatches,
                               final Mat drawnMatch)
  {
    this.rectangle = rectangle;
    this.srcImgDimension = srcImgDimension;
    this.actualNumOfMatches = actualNumOfMatches;
    this.minNumOfMatches = minNumOfMatches;
    this.drawnMatch = drawnMatch;
  }

  protected FeatureMatchResult(final Rectangle rectangle,
                               final Mat drawnMatch)
  {
    this.rectangle = rectangle;
    this.srcImgDimension = null;
    this.actualNumOfMatches = null;
    this.minNumOfMatches = null;
    this.drawnMatch = drawnMatch;
  }

  public FeatureMatchResult withRectangle(final Rectangle rectangle)
  {
    return new FeatureMatchResult(
        rectangle,
        this.srcImgDimension,
        this.actualNumOfMatches,
        this.minNumOfMatches,
        this.drawnMatch);
  }

  public FeatureMatchResult withSrcImgDimension(final Dimension srcImgDimension)
  {
    return new FeatureMatchResult(
        this.rectangle,
        srcImgDimension,
        this.actualNumOfMatches,
        this.minNumOfMatches,
        this.drawnMatch);
  }

  protected FeatureMatchResult withActualNumOfMatches(final Integer actualNumOfMatches)
  {
    return new FeatureMatchResult(
        this.rectangle,
        this.srcImgDimension,
        actualNumOfMatches,
        this.minNumOfMatches,
        this.drawnMatch);
  }

  protected FeatureMatchResult withMinNumOfMatches(final Integer minNumOfMatches)
  {
    return new FeatureMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.actualNumOfMatches,
        minNumOfMatches,
        this.drawnMatch);
  }

  protected FeatureMatchResult withDrawnMatch(final Mat drawnMatch)
  {
    return new FeatureMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.actualNumOfMatches,
        this.minNumOfMatches,
        drawnMatch);
  }

  @Override
  public Double getScore()
  {
    if (nonNull(actualNumOfMatches))
    {return actualNumOfMatches.doubleValue();}
    else
    {return 0.0;}
  }

  public Rectangle getRectangle()
  {
    return this.rectangle;
  }

  public Dimension getSrcImgDimension()
  {
    return this.srcImgDimension;
  }

  public Integer getActualNumOfMatches()
  {
    return this.actualNumOfMatches;
  }

  public Integer getMinNumOfMatches()
  {
    return this.minNumOfMatches;
  }

  public Mat getDrawnMatch()
  {
    return this.drawnMatch;
  }

}
