package com.rkoyanagui.img_recog;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;

public interface MatchResult
{

  Rectangle getRectangle();

  MatchResult withRectangle(Rectangle rectangle);

  Dimension getSrcImgDimension();

  MatchResult withSrcImgDimension(Dimension srcImgDimension);

  Double getScore();

}
