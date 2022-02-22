package com.rkoyanagui.img_recog;

import java.util.Objects;

public class FractionalRectangle
{

  protected Float x;
  protected Float y;
  protected Float width;
  protected Float height;

  public FractionalRectangle(final Float x, final Float y, final Float width, final Float height)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {return true;}
    if (!(o instanceof FractionalRectangle)) {return false;}
    FractionalRectangle that = (FractionalRectangle) o;
    return Objects.equals(x, that.x) && Objects.equals(y, that.y)
        && Objects.equals(width, that.width) && Objects.equals(height, that.height);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(x, y, width, height);
  }

  @Override
  public String toString()
  {
    return "{"
        + "\"x\": " + x
        + ", \"y\": " + y
        + ", \"width\": " + width
        + ", \"height\": " + height
        + "}";
  }

  public Float getX()
  {
    return this.x;
  }

  public Float getY()
  {
    return this.y;
  }

  public Float getWidth()
  {
    return this.width;
  }

  public Float getHeight()
  {
    return this.height;
  }

  public static OffsetRectangleBuilder builder()
  {
    return new OffsetRectangleBuilder();
  }

  public static class OffsetRectangleBuilder
  {

    protected Float x;
    protected Float y;
    protected Float width;
    protected Float height;

    protected OffsetRectangleBuilder()
    {
    }

    public OffsetRectangleBuilder x(Float x)
    {
      this.x = x;
      return this;
    }

    public OffsetRectangleBuilder y(Float y)
    {
      this.y = y;
      return this;
    }

    public OffsetRectangleBuilder width(Float width)
    {
      this.width = width;
      return this;
    }

    public OffsetRectangleBuilder height(Float height)
    {
      this.height = height;
      return this;
    }

    public FractionalRectangle build()
    {
      return new FractionalRectangle(x, y, width, height);
    }

  }

}
