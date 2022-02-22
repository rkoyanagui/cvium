package com.rkoyanagui.img_recog;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.impl.ImageFilter;
import java.util.Arrays;
import java.util.Objects;

public class TemplateMatchingBy extends ImgRecogBy
{

  /**
   * Minimum degree of similarity between the template image and the source image to consider the
   * match attempt as successful. Should be between 0 and 1, inclusive: <i>[0, 1]</i>.
   */
  protected Double matchThreshold;
  // @formatter:off
  /**
   * Factor by which the template and source images' dimensions can be multiplied, when attempting
   * to match them against each other. Should be greater than one. In general, a number around 1.1D
   * is adequate.
   * <p/>For each resized match attempt <i>n | n >= 0</i>, given a factor <i>f | f > 1</i>, given
   * the template image <i>t<sub>0</sub></i> and the source image <i>s<sub>0</sub></i> with
   * dimensions width <i>w</i> and height <i>h</i>, then:
   * <p/>(t<sub>1</sub>.w, t<sub>1</sub>.h) = f<sup>n</sup> * (t<sub>0</sub>.w, t<sub>0</sub>.h)
   * <p/>(t<sub>2</sub>.w, t<sub>2</sub>.h) = f<sup>-n</sup> * (t<sub>0</sub>.w, t<sub>0</sub>.h)
   * <p/>(s<sub>1</sub>.w, s<sub>1</sub>.h) = f<sup>-n</sup> * (s<sub>0</sub>.w, s<sub>0</sub>.h)
   * <p/>(s<sub>2</sub>.w, s<sub>2</sub>.h) = f<sup>n</sup> * (s<sub>0</sub>.w, s<sub>0</sub>.h)
   * <p/><i>t<sub>1</sub>, t<sub>2</sub></i> being upsized and downsized versions of the original
   * template image <i>t<sub>0</sub></i>.
   * <p/><i>s<sub>1</sub>, s<sub>2</sub></i> being downsized and upsized versions of the original
   * source image <i>s<sub>0</sub></i>.
   * <p/>The result being that, for each template match attempt, a bigger template image is matched
   * against a smaller source image <i>(t<sub>1</sub>, s<sub>1</sub>)</i>, and then a smaller
   * template image is matched against a bigger source image <i>(t<sub>2</sub>, s<sub>2</sub>)</i>.
   */
  // @formatter:on
  protected Double resizeFactor;
  /**
   * Number of match attempts with the template and source images. Should be greater than or equal
   * to zero. If equal to zero, then the images are matched in their original dimensions. If greater
   * than zero, then the first attempt is made with the original dimensions and for each next
   * attempt, two pairs are matched against each other, a pair <i>(t<sub>1</sub>, s<sub>1</sub>)</i>
   * of an upsized template image with a downsized source image, and another pair <i>(t<sub>2</sub>,
   * s<sub>2</sub>)</i> of a downsized template image with an upsized source image. See {@link
   * #resizeFactor}.
   */
  protected Integer resizeMaxAttempts;
  /**
   * Filters to be applied to the images before matching them. <p/>In the case of the <i>template
   * matching</i> method, they can be divided in pre- and post-resizing filters, that is, to be
   * applied before or after the resizing operation. <b>Attention:</b> applying a filter after each
   * resizing operation is resource intensive.
   */
  protected ImageFilter[] preResizingFilters;
  /** See {@link #preResizingFilters}. */
  protected ImageFilter[] postResizingFilters;

  /**
   * No-args constructor
   */
  protected TemplateMatchingBy()
  {
  }

  /**
   * Makes a deep copy of another {@link TemplateMatchingBy}.
   *
   * @param o the locator to be copied
   */
  public TemplateMatchingBy(final TemplateMatchingBy o)
  {
    super(o);
    if (nonNull(o))
    {
      this.matchThreshold = o.matchThreshold;
      this.resizeFactor = o.resizeFactor;
      this.resizeMaxAttempts = o.resizeMaxAttempts;
      this.preResizingFilters = o.preResizingFilters;
      this.postResizingFilters = o.postResizingFilters;
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {return true;}
    if (!(o instanceof TemplateMatchingBy)) {return false;}
    TemplateMatchingBy that = (TemplateMatchingBy) o;
    return Objects.equals(templateFilename, that.templateFilename)
        && Objects.equals(method, that.method)
        && Objects.equals(order, that.order)
        && Objects.equals(platform, that.platform)
        && Objects.equals(matchThreshold, that.matchThreshold)
        && Objects.equals(resizeFactor, that.resizeFactor)
        && Objects.equals(resizeMaxAttempts, that.resizeMaxAttempts)
        && Arrays.equals(preResizingFilters, that.preResizingFilters)
        && Arrays.equals(postResizingFilters, that.postResizingFilters);
  }

  @Override
  public int hashCode()
  {
    int result = Objects.hash(templateFilename, method, order, platform, matchThreshold,
        resizeFactor, resizeMaxAttempts);
    result = 31 * result + Arrays.hashCode(preResizingFilters);
    result = 31 * result + Arrays.hashCode(postResizingFilters);
    return result;
  }

  public static void verifyTemplateMatchingParams(final ImgRecogBy params)
  {
    if (!(params instanceof TemplateMatchingBy))
    {
      throw new IllegalArgumentException("If 'method'=TEMPLATE_MATCHING, then a 'parameters' "
          + "object of type com.rkoyanagui.img_recog.TemplateMatchingBy should be provided.");
    }
    final TemplateMatchingBy tParams = (TemplateMatchingBy) params;

    final String templateFilename = tParams.getTemplateFilename();
    final String msg0 = "'templateFilename' should be neither null nor empty.";
    ImgRecogBy.verifyParam(templateFilename, p -> nonNull(p) && !p.isEmpty(),
        () -> new IllegalArgumentException(msg0));

    final Double matchThresh = tParams.getMatchThreshold();
    final String msg1 =
        String.format("Expected 0 <= matchThreshold <= 1 but was '%.4f'", matchThresh);
    ImgRecogBy.verifyParam(matchThresh, p -> nonNull(p) && p >= 0.0 && p <= 1.0,
        () -> new IllegalArgumentException(msg1));

    final Double resizeFactor = tParams.getResizeFactor();
    final String msg2 = String.format("Expected resizeFactor >= 1.0 but was '%.4f'", resizeFactor);
    ImgRecogBy.verifyParam(resizeFactor, p -> nonNull(p) && p >= 1.0,
        () -> new IllegalArgumentException(msg2));

    final Integer maxAttempts = tParams.getResizeMaxAttempts();
    final String msg3 = String.format("Expected resizeMaxAttempts >= 0 but was '%d'", maxAttempts);
    ImgRecogBy.verifyParam(maxAttempts, p -> nonNull(p) && p >= 0,
        () -> new IllegalArgumentException(msg3));
  }

  public Double getMatchThreshold()
  {
    return this.matchThreshold;
  }

  public Double getResizeFactor()
  {
    return this.resizeFactor;
  }

  public Integer getResizeMaxAttempts()
  {
    return this.resizeMaxAttempts;
  }

  public ImageFilter[] getPreResizingFilters()
  {
    return this.preResizingFilters;
  }

  public ImageFilter[] getPostResizingFilters()
  {
    return this.postResizingFilters;
  }

  @Override
  public String toString()
  {
    return "TemplateMatchingBy(matchThreshold=" + this.getMatchThreshold() + ", resizeFactor="
        + this.getResizeFactor() + ", resizeMaxAttempts=" + this.getResizeMaxAttempts()
        + ", preResizingFilters=" + Arrays.deepToString(this.getPreResizingFilters())
        + ", postResizingFilters=" + Arrays.deepToString(this.getPostResizingFilters())
        + ")";
  }

  public static TemplateMatchingByBuilder<TemplateMatchingBy, TemplateMatchingByBuilderImpl> builder()
  {
    return new TemplateMatchingByBuilderImpl();
  }

  public abstract static class TemplateMatchingByBuilder<C extends TemplateMatchingBy, B extends TemplateMatchingByBuilder<C, B>> extends
      ImgRecogByBuilder<C, B>
  {

    protected TemplateMatchingByBuilder()
    {
      super.memo = new TemplateMatchingBy();
      super.method(ImgRecogMethod.TEMPLATE_MATCHING);
    }

    /** See {@link TemplateMatchingBy#TemplateMatchingBy(TemplateMatchingBy)}. */
    public B clone(TemplateMatchingBy by)
    {
      super.memo = new TemplateMatchingBy(by);
      return self();
    }

    /** See {@link TemplateMatchingBy#matchThreshold}. */
    public B matchThreshold(Double matchThreshold)
    {
      ((TemplateMatchingBy) super.memo).matchThreshold = matchThreshold;
      return self();
    }

    /** See {@link TemplateMatchingBy#resizeFactor}. */
    public B resizeFactor(Double resizeFactor)
    {
      ((TemplateMatchingBy) super.memo).resizeFactor = resizeFactor;
      return self();
    }

    /** See {@link TemplateMatchingBy#resizeMaxAttempts}. */
    public B resizeMaxAttempts(Integer resizeMaxAttempts)
    {
      ((TemplateMatchingBy) super.memo).resizeMaxAttempts = resizeMaxAttempts;
      return self();
    }

    /** See {@link TemplateMatchingBy#preResizingFilters}. */
    public B preResizingFilters(ImageFilter[] preResizingFilters)
    {
      ((TemplateMatchingBy) super.memo).preResizingFilters = preResizingFilters;
      return self();
    }

    /** See {@link TemplateMatchingBy#postResizingFilters}. */
    public B postResizingFilters(ImageFilter[] postResizingFilters)
    {
      ((TemplateMatchingBy) super.memo).postResizingFilters = postResizingFilters;
      return self();
    }

    protected abstract B self();

    public abstract C build();

  }

  public static final class TemplateMatchingByBuilderImpl extends
      TemplateMatchingByBuilder<TemplateMatchingBy, TemplateMatchingByBuilderImpl>
  {

    protected TemplateMatchingByBuilderImpl()
    {
    }

    @Override
    protected TemplateMatchingByBuilderImpl self()
    {
      return this;
    }

    @Override
    public TemplateMatchingBy build()
    {
      return (TemplateMatchingBy) super.memo;
    }

  }

}
