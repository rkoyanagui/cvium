package com.rkoyanagui.img_recog;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.impl.ImageFilter;
import com.rkoyanagui.img_recog.impl.OcrCleanUp;
import com.rkoyanagui.img_recog.impl.OcrTest;
import java.util.Arrays;
import java.util.Objects;

/**
 * A set of parameters on how to locate a screen element, employing the OCR (Optical Character
 * Recognition) method.
 */
public class OcrMatchingBy extends ImgRecogBy
{

  /** The character sequence to be looked up. */
  protected String searchTerm;
  /**
   * The minimum level of confidence to consider a text as successfully found. Choose a value
   * between {@code 0.0} and {@code 1.0}.
   */
  protected Float minScore;
  /** What test a piece of text must pass to be considered as successfully found. */
  protected OcrTest ocrTest;
  /**
   * The non-maximum suppression threshold, for filtering out redundant text bounding boxes (false
   * positives). Choose a value between {@code 0.0} and {@code 1.0}.
   */
  protected Float nms;
  // @formatter:off
  /**
   * The intersection-over-union threshold, for merging overlapping text bounding boxes. Choose a
   * value between {@code 0.0} and {@code 1.0}. Given a rectangle {@code A} and another {@code B}:
   * <p/><code>iou = area(A ⋂ B) / area(A ⋃ B)</code>
   * <code><p/>if iou >= threshold:
   * <br/>&#032;&#032;# create a new rectangle C, encompassing A and B
   * <br/>&#032;&#032;x<sub>C</sub> = min(x<sub>A</sub>, x<sub>B</sub>)
   * <br/>&#032;&#032;y<sub>C</sub> = min(y<sub>A</sub>, y<sub>B</sub>)
   * <br/>&#032;&#032;w<sub>C</sub> = w<sub>A</sub> + w<sub>B</sub> - w<sub>intersection</sub>
   * <br/>&#032;&#032;h<sub>C</sub> = h<sub>A</sub> + h<sub>B</sub> - h<sub>intersection</sub>
   * <br/>&#032;&#032;C = Rectangle(x<sub>C</sub>, y<sub>C</sub>, w<sub>C</sub>, h<sub>C</sub>)
   * <br/>else:
   * <br/>&#032;&#032;# do nothing, leave A and B unchanged</code>
   */
  // @formatter:on
  protected Float iou;
  /**
   * Left and right-side padding as a fraction of a box's width, top and bottom-side padding as a
   * fraction of a box's height, to be applied to the text detection bounding boxes, prior to their
   * being used in text recognition. The padding values may vary from negative infinity to positive
   * infinity but, for most purposes, a fraction between {@code 0.0} and {@code 1.0} will suffice.
   */
  protected Padding padding;
  /**
   * Represents functions to apply to OCR'ed text, before the test/predicate is applied, presumably
   * to clean the text up and remove unwanted or insignificant characters.
   */
  protected OcrCleanUp[] cleanUp;
  /** Filters to be applied to the images before matching them. */
  protected ImageFilter[] filters;

  /**
   * No-args constructor
   */
  protected OcrMatchingBy()
  {
  }

  /**
   * Makes a deep copy of another {@link OcrMatchingBy}.
   *
   * @param o the locator to be copied
   */
  public OcrMatchingBy(final OcrMatchingBy o)
  {
    super(o);
    if (nonNull(o))
    {
      this.searchTerm = o.searchTerm;
      this.minScore = o.minScore;
      this.ocrTest = o.ocrTest;
      this.nms = o.nms;
      this.iou = o.iou;
      this.padding = o.padding;
      this.cleanUp = o.cleanUp;
      this.filters = o.filters;
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {return true;}
    if (!(o instanceof OcrMatchingBy)) {return false;}
    OcrMatchingBy that = (OcrMatchingBy) o;
    return Objects.equals(templateFilename, that.templateFilename)
        && Objects.equals(method, that.method)
        && Objects.equals(order, that.order)
        && Objects.equals(platform, that.platform)
        && Objects.equals(searchTerm, that.searchTerm)
        && Objects.equals(minScore, that.minScore)
        && Objects.equals(ocrTest, that.ocrTest)
        && Objects.equals(nms, that.nms)
        && Objects.equals(iou, that.iou)
        && Objects.equals(padding, that.padding)
        && Arrays.equals(cleanUp, that.cleanUp)
        && Arrays.equals(filters, that.filters);
  }

  @Override
  public int hashCode()
  {
    int result = Objects.hash(templateFilename, method, order, platform, searchTerm,
        minScore, ocrTest, nms, iou, padding);
    result = 31 * result + Arrays.hashCode(cleanUp);
    result = 31 * result + Arrays.hashCode(filters);
    return result;
  }

  public static void verifyOcrParams(final ImgRecogBy params)
  {
    if (!(params instanceof OcrMatchingBy))
    {
      throw new IllegalArgumentException("If 'method'=OCR, then a 'parameters' "
          + "object of type com.rkoyanagui.img_recog.OcrMatchingBy should be provided.");
    }
    final OcrMatchingBy oParams = (OcrMatchingBy) params;

    final String searchTerm = oParams.getSearchTerm();
    final String msg0 =
        String.format("'searchTerm' should be neither null nor empty but was '%s'", searchTerm);
    ImgRecogBy.verifyParam(searchTerm, p -> nonNull(p) && !p.isEmpty(),
        () -> new IllegalArgumentException(msg0));

    final OcrTest ocrTest = oParams.getOcrTest();
    final String msg1 = String.format("'ocrTest' should be non-null but was '%s'", ocrTest);
    ImgRecogBy.verifyParam(ocrTest, p -> nonNull(p), () -> new IllegalArgumentException(msg1));

    final Float minScore = oParams.getMinScore();
    final String msg2 = String.format("Expected 0.0 <= minScore <= 1.0 but was '%.4f'", minScore);
    ImgRecogBy.verifyParam(minScore, p -> nonNull(p) && p >= 0.0f && p <= 1.0f,
        () -> new IllegalArgumentException(msg2));

    final Float nms = oParams.getNms();
    final String msg3 = String.format("Expected 0.0 <= nms <= 1.0 but was '%.4f'", nms);
    ImgRecogBy.verifyParam(nms, p -> nonNull(p) && p >= 0.0f && p <= 1.0f,
        () -> new IllegalArgumentException(msg3));

    final Float iou = oParams.getIou();
    final String msg4 = String.format("Expected 0.0 <= iou <= 1.0 but was '%.4f'", iou);
    ImgRecogBy.verifyParam(iou, p -> nonNull(p) && p >= 0.0f && p <= 1.0f,
        () -> new IllegalArgumentException(msg4));

    final Padding padding = oParams.getPadding();
    final String msg5 = String.format("'padding' should be non-null but was '%s'", padding);
    ImgRecogBy.verifyParam(padding, p -> nonNull(p), () -> new IllegalArgumentException(msg5));
  }

  public String getSearchTerm()
  {
    return this.searchTerm;
  }

  public Float getMinScore()
  {
    return this.minScore;
  }

  public OcrTest getOcrTest()
  {
    return this.ocrTest;
  }

  public Float getNms()
  {
    return this.nms;
  }

  public Float getIou()
  {
    return this.iou;
  }

  public Padding getPadding()
  {
    return this.padding;
  }

  public OcrCleanUp[] getCleanUp()
  {
    return this.cleanUp;
  }

  public ImageFilter[] getFilters()
  {
    return this.filters;
  }

  @Override
  public String toString()
  {
    return "OcrMatchingBy(searchTerm=" + this.getSearchTerm() + ", minScore=" + this.getMinScore()
        + ", ocrTest=" + this.getOcrTest() + ", nms=" + this.getNms() + ", iou=" + this.getIou()
        + ", padding=" + this.getPadding() + ", filters=" + Arrays.deepToString(
        this.getFilters()) + ")";
  }

  public static OcrMatchingByBuilder<OcrMatchingBy, OcrMatchingByBuilderImpl> builder()
  {
    return new OcrMatchingByBuilderImpl();
  }

  public abstract static class OcrMatchingByBuilder<C extends OcrMatchingBy, B extends OcrMatchingByBuilder<C, B>> extends
      ImgRecogByBuilder<C, B>
  {

    protected OcrMatchingByBuilder()
    {
      super.memo = new OcrMatchingBy();
      super.method(ImgRecogMethod.OCR);
    }

    /** See {@link OcrMatchingBy#OcrMatchingBy(OcrMatchingBy)}. */
    public B clone(OcrMatchingBy by)
    {
      super.memo = new OcrMatchingBy(by);
      return self();
    }

    /** See {@link OcrMatchingBy#searchTerm}. */
    public B searchTerm(String searchTerm)
    {
      ((OcrMatchingBy) super.memo).searchTerm = searchTerm;
      return self();
    }

    /** See {@link OcrMatchingBy#minScore}. */
    public B minScore(Float minScore)
    {
      ((OcrMatchingBy) super.memo).minScore = minScore;
      return self();
    }

    /** See {@link OcrMatchingBy#ocrTest}. */
    public B ocrTest(OcrTest ocrTest)
    {
      ((OcrMatchingBy) super.memo).ocrTest = ocrTest;
      return self();
    }

    /** See {@link OcrMatchingBy#nms}. */
    public B nms(Float nms)
    {
      ((OcrMatchingBy) super.memo).nms = nms;
      return self();
    }

    /** See {@link OcrMatchingBy#iou}. */
    public B iou(Float iou)
    {
      ((OcrMatchingBy) super.memo).iou = iou;
      return self();
    }

    /** See {@link OcrMatchingBy#padding}. */
    public B padding(Padding padding)
    {
      ((OcrMatchingBy) super.memo).padding = padding;
      return self();
    }

    /** See {@link OcrMatchingBy#cleanUp}. */
    public B cleanUp(OcrCleanUp[] cleanUp)
    {
      ((OcrMatchingBy) super.memo).cleanUp = cleanUp;
      return self();
    }

    /** See {@link OcrMatchingBy#filters}. */
    public B filters(ImageFilter[] filters)
    {
      ((OcrMatchingBy) super.memo).filters = filters;
      return self();
    }

    protected abstract B self();

    public abstract C build();

  }

  public static final class OcrMatchingByBuilderImpl extends
      OcrMatchingByBuilder<OcrMatchingBy, OcrMatchingByBuilderImpl>
  {

    protected OcrMatchingByBuilderImpl()
    {
    }

    @Override
    protected OcrMatchingByBuilderImpl self()
    {
      return this;
    }

    @Override
    public OcrMatchingBy build()
    {
      return (OcrMatchingBy) this.memo;
    }

  }

}
