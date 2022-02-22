package com.rkoyanagui.img_recog;

import static java.util.Objects.nonNull;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A set of parameters on how to locate a screen element.
 */
public abstract class ImgRecogBy
{

  /** Absolute or relative path to the template/query image file. */
  protected String templateFilename;
  /** Image recognition method. */
  protected ImgRecogMethod method;
  /** Order in which this locator should be applied. */
  protected Integer order;
  /** Type of mobile platform/OS. */
  protected String platform;
  // @formatter:off
  /**
   * Using the original located element as an anchor and as a tape measure or ruler, you can add or
   * subtract <b>x</b> and <b>y</b> coordinates, and define <b>width</b> and <b>height</b>,
   * proportionally to the first element's coordinates and dimensions, in order to locate some other
   * second element. Given the first element <i>E<sub>0</sub></i> that is already located; the
   * second element <i>E<sub>1</sub></i> that you are trying to locate; and the multipliers you
   * define for x, y, width, height:
   * <p/>x<sub>E1</sub> = x<sub>E0</sub> + (x<sub>multiplier</sub> * w<sub>E0</sub>)
   * <p/>y<sub>E1</sub> = y<sub>E0</sub> + (y<sub>multiplier</sub> * h<sub>E0</sub>)
   * <p/>w<sub>E1</sub> = w<sub>multiplier</sub> * w<sub>E0</sub>
   * <p/>h<sub>E1</sub> = h<sub>multiplier</sub> * h<sub>E0</sub>
   */
  // @formatter:on
  protected FractionalRectangle offset;
  // @formatter:off
  /**
   * Instead of trying to locate an element in the whole screen, you may section off only the
   * relevant part of the screen, and thus increase your chances of a successful element look-up.
   * Using the screen as a tape measure or ruler, define the <b>x</b> and <b>y</b> coordinates, as
   * well as <b>width</b> and <b>height</b>, of the rectangular section of interest. Given the
   * original screen <i>S<sub>0</sub></i>; the new section of the screen you are setting apart
   * <i>S<sub>1</sub></i>; and the multipliers you define for x, y, width, height:
   * <p/>x<sub>S1</sub> = x<sub>multiplier</sub> * w<sub>S0</sub>
   * <p/>y<sub>S1</sub> = y<sub>multiplier</sub> * h<sub>S0</sub>
   * <p/>w<sub>S1</sub> = w<sub>multiplier</sub> * w<sub>S0</sub>
   * <p/>h<sub>S1</sub> = h<sub>multiplier</sub> * h<sub>S0</sub>
   */
  // @formatter:on
  protected FractionalRectangle cutout;

  protected ImgRecogBy()
  {
  }

  protected ImgRecogBy(final ImgRecogBy o)
  {
    if (nonNull(o))
    {
      this.templateFilename = o.templateFilename;
      this.method = o.method;
      this.order = o.order;
      this.platform = o.platform;
      this.offset = o.offset;
      this.cutout = o.cutout;
    }
  }

  protected static <T, X extends Throwable> void verifyParam(
      final T param,
      final Predicate<T> predicate,
      final Supplier<? extends X> exceptionSupplier)
      throws X
  {
    if (!predicate.test(param))
    {
      throw exceptionSupplier.get();
    }
  }

  public String getTemplateFilename()
  {
    return this.templateFilename;
  }

  public ImgRecogMethod getMethod()
  {
    return this.method;
  }

  public Integer getOrder()
  {
    return this.order;
  }

  public String getPlatform()
  {
    return this.platform;
  }

  public FractionalRectangle getOffset()
  {
    return this.offset;
  }

  public FractionalRectangle getCutout()
  {
    return this.cutout;
  }

  public String toString()
  {
    return "ImgRecogBy(templateFilename=" + this.getTemplateFilename() + ", method="
        + this.getMethod() + ", order=" + this.getOrder() + ", platform=" + this.getPlatform()
        + ", offset=" + this.getOffset() + ", cutout=" + this.getCutout() + ")";
  }

  public abstract static class ImgRecogByBuilder<C extends ImgRecogBy, B extends ImgRecogByBuilder<C, B>>
  {

    protected ImgRecogBy memo;

    /** See {@link ImgRecogBy#templateFilename}. */
    public B templateFilename(String templateFilename)
    {
      this.memo.templateFilename = templateFilename;
      return self();
    }

    /** See {@link ImgRecogBy#method}. */
    public B method(ImgRecogMethod method)
    {
      this.memo.method = method;
      return self();
    }

    /** See {@link ImgRecogBy#order}. */
    public B order(Integer order)
    {
      this.memo.order = order;
      return self();
    }

    /** See {@link ImgRecogBy#platform}. */
    public B platform(String platform)
    {
      this.memo.platform = platform;
      return self();
    }

    /** See {@link ImgRecogBy#offset}. */
    public B offset(FractionalRectangle offset)
    {
      this.memo.offset = offset;
      return self();
    }

    /** See {@link ImgRecogBy#offset}. */
    public B offset(float x, float y, float width, float height)
    {
      if (ImmutableList.of(x, y, width, height)
          .stream()
          .noneMatch(f -> f.equals(Float.MAX_VALUE)))
      {
        this.memo.offset = new FractionalRectangle(x, y, width, height);
      }
      return self();
    }

    /** See {@link ImgRecogBy#cutout}. */
    public B cutout(FractionalRectangle cutout)
    {
      this.memo.cutout = cutout;
      return self();
    }

    /** See {@link ImgRecogBy#cutout}. */
    public B cutout(float x, float y, float width, float height)
    {
      if (ImmutableList.of(x, y, width, height)
          .stream()
          .noneMatch(f -> f.equals(Float.MAX_VALUE)))
      {
        this.memo.cutout = new FractionalRectangle(x, y, width, height);
      }
      return self();
    }

    protected abstract B self();

    public abstract C build();

  }

  public interface ImgRecogByBuilderFromAnnotation<C extends ImgRecogBy>
  {

    C build(Object annotation, Field field);

  }

}
