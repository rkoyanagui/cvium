package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.MatchResult;
import org.opencv.core.Mat;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

class TemplateMatchResult implements MatchResult
{

  protected final Rectangle rectangle;
  protected final Dimension srcImgDimension;
  protected final Mat template;
  protected final Mat drawnMatch;
  protected final Double matchCoeff;
  protected final Double matchThreshold;
  protected final Double resizeFactor;

  protected TemplateMatchResult(final Rectangle rectangle,
                                final Dimension srcImgDimension,
                                final Mat template,
                                final Mat drawnMatch,
                                final Double matchCoeff,
                                final Double matchThreshold,
                                final Double resizeFactor)
  {
    this.rectangle = rectangle;
    this.srcImgDimension = srcImgDimension;
    this.template = template;
    this.drawnMatch = drawnMatch;
    this.matchCoeff = matchCoeff;
    this.matchThreshold = matchThreshold;
    this.resizeFactor = resizeFactor;
  }

  protected TemplateMatchResult(final Rectangle rectangle,
                                final Double matchCoeff)
  {
    this.rectangle = rectangle;
    this.srcImgDimension = null;
    this.template = null;
    this.drawnMatch = null;
    this.matchCoeff = matchCoeff;
    this.matchThreshold = null;
    this.resizeFactor = null;
  }

  public TemplateMatchResult withRectangle(final Rectangle rectangle)
  {
    return new TemplateMatchResult(
        rectangle,
        this.srcImgDimension,
        this.template,
        this.drawnMatch,
        this.matchCoeff,
        this.matchThreshold,
        this.resizeFactor);
  }

  public TemplateMatchResult withSrcImgDimension(final Dimension srcImgDimension)
  {
    return new TemplateMatchResult(
        this.rectangle,
        srcImgDimension,
        this.template,
        this.drawnMatch,
        this.matchCoeff,
        this.matchThreshold,
        this.resizeFactor);
  }

  protected TemplateMatchResult withTemplate(final Mat template)
  {
    return new TemplateMatchResult(
        this.rectangle,
        this.srcImgDimension,
        template,
        this.drawnMatch,
        this.matchCoeff,
        this.matchThreshold,
        this.resizeFactor);
  }

  protected TemplateMatchResult withDrawnMatch(final Mat drawnMatch)
  {
    return new TemplateMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.template,
        drawnMatch,
        this.matchCoeff,
        this.matchThreshold,
        this.resizeFactor);
  }

  protected TemplateMatchResult withMatchCoeff(final Double matchCoeff)
  {
    return new TemplateMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.template,
        this.drawnMatch,
        matchCoeff,
        this.matchThreshold,
        this.resizeFactor);
  }

  protected TemplateMatchResult withMatchThreshold(final Double matchThreshold)
  {
    return new TemplateMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.template,
        this.drawnMatch,
        this.matchCoeff,
        matchThreshold,
        this.resizeFactor);
  }

  protected TemplateMatchResult withResizeFactor(final Double resizeFactor)
  {
    return new TemplateMatchResult(
        this.rectangle,
        this.srcImgDimension,
        this.template,
        this.drawnMatch,
        this.matchCoeff,
        this.matchThreshold,
        resizeFactor);
  }

  @Override
  public Double getScore()
  {
    if (nonNull(matchCoeff))
    {return matchCoeff;}
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

  public Mat getTemplate()
  {
    return this.template;
  }

  public Mat getDrawnMatch()
  {
    return this.drawnMatch;
  }

  public Double getMatchCoeff()
  {
    return this.matchCoeff;
  }

  public Double getMatchThreshold()
  {
    return this.matchThreshold;
  }

  public Double getResizeFactor()
  {
    return this.resizeFactor;
  }

}
