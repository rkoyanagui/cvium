package com.rkoyanagui.img_recog;

import java.util.Objects;

public class Padding
{

  protected final double left;
  protected final double right;
  protected final double top;
  protected final double bottom;

  public Padding(double left, double right, double top, double bottom)
  {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  public double getLeft()
  {
    return this.left;
  }

  public double getRight()
  {
    return this.right;
  }

  public double getTop()
  {
    return this.top;
  }

  public double getBottom()
  {
    return this.bottom;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {return true;}
    if (!(o instanceof Padding)) {return false;}
    Padding padding = (Padding) o;
    return Double.compare(padding.left, left) == 0
        && Double.compare(padding.right, right) == 0
        && Double.compare(padding.top, top) == 0
        && Double.compare(padding.bottom, bottom) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(left, right, top, bottom);
  }

  public String toString()
  {
    return "Padding(left=" + this.getLeft() + ", right=" + this.getRight() + ", top="
        + this.getTop() + ", bottom=" + this.getBottom() + ")";
  }

}
