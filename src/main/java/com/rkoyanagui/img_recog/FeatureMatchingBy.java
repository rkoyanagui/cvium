package com.rkoyanagui.img_recog;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.impl.ImageFilter;
import java.util.Arrays;
import java.util.Objects;

public class FeatureMatchingBy extends ImgRecogBy
{

  /**
   * Number of features to look for in the query image. Should be greater than zero. A number in the
   * low hundreds should be adequate.
   */
  protected Integer queryImgFeatures;
  /**
   * Number of features to look for in the training image. Should be greater than zero. A number
   * around one thousand should be adequate.
   */
  protected Integer trainImgFeatures;
  /**
   * Minimum number of feature matches to consider the match attempt as successful. Should be
   * greater than or equal to eight. It is impossible to derive location data from less than eight
   * key feature points. A number around twenty should be adequate.
   */
  protected Integer minNumOfMatches;
  /**
   * Of the total number of feature matches found, how many to keep when calculating position and
   * dimensions. Should be greater than or equal to eight. This is just a maximum cap, so it can be
   * less, equal to, or greater than {@link #minNumOfMatches}. A number around twenty should be
   * adequate.
   */
  protected Integer numOfMatchesToKeep;
  /**
   * Should be > 1.0. See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int,
   * int, int, int)}
   */
  protected Float scaleFactor;
  /**
   * Should be >= 1. See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int,
   * int, int, int)}
   */
  protected Integer nlevels;
  /** See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int, int, int, int)} */
  protected Integer edgeThreshold;
  /**
   * Should be >= 0. See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int,
   * int, int, int)}
   */
  protected Integer firstLevel;
  /**
   * Should be equal to 2, 3, or 4. See {@link org.opencv.features2d.ORB#create(int, float, int,
   * int, int, int, int, int, int)}
   */
  protected Integer wtaK;
  /**
   * Should be equal to 0 (HARRIS_SCORE) or 1 (FAST_SCORE). See {@link
   * org.opencv.features2d.ORB#create(int, float, int, int, int, int, int, int, int)}
   */
  protected Integer scoreType;
  /**
   * Should be >= 2. See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int,
   * int, int, int)}
   */
  protected Integer patchSize;
  /** See {@link org.opencv.features2d.ORB#create(int, float, int, int, int, int, int, int, int)} */
  protected Integer fastThreshold;
  /**
   * Filters to be applied to the images before matching them.
   */
  protected ImageFilter[] filters;

  /**
   * No-args constructor
   */
  protected FeatureMatchingBy()
  {
  }

  /**
   * Makes a deep copy of another {@link FeatureMatchingBy}.
   *
   * @param o the locator to be copied
   */
  public FeatureMatchingBy(final FeatureMatchingBy o)
  {
    super(o);
    if (nonNull(o))
    {
      this.queryImgFeatures = o.queryImgFeatures;
      this.trainImgFeatures = o.trainImgFeatures;
      this.minNumOfMatches = o.minNumOfMatches;
      this.numOfMatchesToKeep = o.numOfMatchesToKeep;
      this.scaleFactor = o.scaleFactor;
      this.nlevels = o.nlevels;
      this.edgeThreshold = o.edgeThreshold;
      this.firstLevel = o.firstLevel;
      this.wtaK = o.wtaK;
      this.scoreType = o.scoreType;
      this.patchSize = o.patchSize;
      this.fastThreshold = o.fastThreshold;
      this.filters = o.filters;
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {return true;}
    if (!(o instanceof FeatureMatchingBy)) {return false;}
    FeatureMatchingBy that = (FeatureMatchingBy) o;
    return Objects.equals(templateFilename, that.templateFilename)
        && Objects.equals(method, that.method)
        && Objects.equals(order, that.order)
        && Objects.equals(platform, that.platform)
        && Objects.equals(queryImgFeatures, that.queryImgFeatures)
        && Objects.equals(trainImgFeatures, that.trainImgFeatures)
        && Objects.equals(minNumOfMatches, that.minNumOfMatches)
        && Objects.equals(numOfMatchesToKeep, that.numOfMatchesToKeep)
        && Objects.equals(scaleFactor, that.scaleFactor)
        && Objects.equals(nlevels, that.nlevels)
        && Objects.equals(edgeThreshold, that.edgeThreshold)
        && Objects.equals(firstLevel, that.firstLevel)
        && Objects.equals(wtaK, that.wtaK)
        && Objects.equals(scoreType, that.scoreType)
        && Objects.equals(patchSize, that.patchSize)
        && Objects.equals(fastThreshold, that.fastThreshold)
        && Arrays.equals(filters, that.filters);
  }

  @Override
  public int hashCode()
  {
    int result = Objects.hash(templateFilename, method, order, platform, queryImgFeatures,
        trainImgFeatures, minNumOfMatches, numOfMatchesToKeep, scaleFactor, nlevels, edgeThreshold,
        firstLevel, wtaK, scoreType, patchSize, fastThreshold);
    result = 31 * result + Arrays.hashCode(filters);
    return result;
  }

  public static void verifyFeatureMatchingParams(final ImgRecogBy params)
  {
    if (!(params instanceof FeatureMatchingBy))
    {
      throw new IllegalArgumentException("If 'method'=FEATURE_MATCHING, then a 'parameters' "
          + "object of type com.rkoyanagui.img_recog.FeatureMatchingBy should be provided.");
    }

    final FeatureMatchingBy fParams = (FeatureMatchingBy) params;

    final String templateFilename = fParams.getTemplateFilename();
    final String msg0 = String.format("'templateFilename' should be neither null nor empty "
        + "but was '%s'", templateFilename);
    ImgRecogBy.verifyParam(templateFilename, p -> nonNull(p) && !p.isEmpty(),
        () -> new IllegalArgumentException(msg0));

    final Integer qImgFeatures = fParams.getQueryImgFeatures();
    final String msg1 = String.format("Expected queryImgFeatures > 0 but was '%d'", qImgFeatures);
    ImgRecogBy.verifyParam(qImgFeatures, p -> nonNull(p) && p > 0,
        () -> new IllegalArgumentException(msg1));

    final Integer tImgFeatures = fParams.getTrainImgFeatures();
    final String msg2 = String.format("Expected trainImgFeatures > 0 but was '%d'", tImgFeatures);
    ImgRecogBy.verifyParam(tImgFeatures, p -> nonNull(p) && p > 0,
        () -> new IllegalArgumentException(msg2));

    final Integer minMatches = fParams.getMinNumOfMatches();
    final String msg3 = String.format("Expected minNumOfMatches >= 8 but was '%d'", minMatches);
    ImgRecogBy.verifyParam(minMatches, p -> nonNull(p) && p >= 8,
        () -> new IllegalArgumentException(msg3));

    final Integer numToKeep = fParams.getNumOfMatchesToKeep();
    final String msg4 = String.format("Expected numOfMatchesToKeep >= 8 but was '%d'", numToKeep);
    ImgRecogBy.verifyParam(numToKeep, p -> nonNull(p) && p >= 8,
        () -> new IllegalArgumentException(msg4));

    final Float scaleFactor = fParams.getScaleFactor();
    final String msg5 = String.format("Expected scaleFactor > 1.0 but was '%.4f'", scaleFactor);
    ImgRecogBy.verifyParam(scaleFactor, p -> nonNull(p) && p > 1.0f,
        () -> new IllegalArgumentException(msg5));

    final Integer nlevels = fParams.getNlevels();
    final String msg6 = String.format("Expected nlevels >= 1 but was '%d'", nlevels);
    ImgRecogBy.verifyParam(nlevels, p -> nonNull(p) && p >= 1,
        () -> new IllegalArgumentException(msg6));

    final Integer edgeThresh = fParams.getEdgeThreshold();
    final String msg7 = String.format("Expected edgeThreshold non-null but was '%d'", edgeThresh);
    ImgRecogBy.verifyParam(edgeThresh, p -> nonNull(p), () -> new IllegalArgumentException(msg7));

    final Integer firstLevel = fParams.getFirstLevel();
    final String msg8 = String.format("Expected firstLevel >= 0 but was '%d'", firstLevel);
    ImgRecogBy.verifyParam(firstLevel, p -> nonNull(p) && p >= 0,
        () -> new IllegalArgumentException(msg8));

    final Integer wtaK = fParams.getWtaK();
    final String msg9 = String.format("Expected WTA_K equal to 2, 3, or 4 but was '%d'", wtaK);
    ImgRecogBy.verifyParam(wtaK, p -> nonNull(p) && (p == 2 || p == 3 || p == 4),
        () -> new IllegalArgumentException(msg9));

    final Integer scoreType = fParams.getScoreType();
    final String msg10 = String.format("Expected scoreType equal to 0 (HARRIS_SCORE) or "
        + "1 (FAST_SCORE) but was '%d'", scoreType);
    ImgRecogBy.verifyParam(scoreType, p -> nonNull(p) && (p == 0 || p == 1),
        () -> new IllegalArgumentException(msg10));

    final Integer patchSize = fParams.getPatchSize();
    final String msg11 = String.format("Expected patchSize >= 2 but was '%d'", scoreType);
    ImgRecogBy.verifyParam(patchSize, p -> nonNull(p) && p >= 2,
        () -> new IllegalArgumentException(msg11));
  }

  public Integer getQueryImgFeatures()
  {
    return this.queryImgFeatures;
  }

  public Integer getTrainImgFeatures()
  {
    return this.trainImgFeatures;
  }

  public Integer getMinNumOfMatches()
  {
    return this.minNumOfMatches;
  }

  public Integer getNumOfMatchesToKeep()
  {
    return this.numOfMatchesToKeep;
  }

  public Float getScaleFactor()
  {
    return this.scaleFactor;
  }

  public Integer getNlevels()
  {
    return this.nlevels;
  }

  public Integer getEdgeThreshold()
  {
    return this.edgeThreshold;
  }

  public Integer getFirstLevel()
  {
    return this.firstLevel;
  }

  public Integer getWtaK()
  {
    return this.wtaK;
  }

  public Integer getScoreType()
  {
    return this.scoreType;
  }

  public Integer getPatchSize()
  {
    return this.patchSize;
  }

  public Integer getFastThreshold()
  {
    return this.fastThreshold;
  }

  public ImageFilter[] getFilters()
  {
    return this.filters;
  }

  @Override
  public String toString()
  {
    return "FeatureMatchingBy(queryImgFeatures=" + this.getQueryImgFeatures()
        + ", trainImgFeatures=" + this.getTrainImgFeatures() + ", minNumOfMatches="
        + this.getMinNumOfMatches() + ", numOfMatchesToKeep=" + this.getNumOfMatchesToKeep()
        + ", scaleFactor=" + this.getScaleFactor() + ", nlevels=" + this.getNlevels()
        + ", edgeThreshold=" + this.getEdgeThreshold() + ", firstLevel=" + this.getFirstLevel()
        + ", wtaK=" + this.getWtaK() + ", scoreType=" + this.getScoreType() + ", patchSize="
        + this.getPatchSize() + ", fastThreshold=" + this.getFastThreshold() + ", filters="
        + Arrays.deepToString(this.getFilters()) + ")";
  }

  public static FeatureMatchingByBuilder<FeatureMatchingBy, FeatureMatchingByBuilderImpl> builder()
  {
    return new FeatureMatchingByBuilderImpl();
  }

  public abstract static class FeatureMatchingByBuilder<C extends FeatureMatchingBy, B extends FeatureMatchingByBuilder<C, B>> extends
      ImgRecogByBuilder<C, B>
  {

    protected FeatureMatchingByBuilder()
    {
      super.memo = new FeatureMatchingBy();
      super.method(ImgRecogMethod.FEATURE_MATCHING);
    }

    /** See {@link FeatureMatchingBy#FeatureMatchingBy(FeatureMatchingBy)}. */
    public B clone(FeatureMatchingBy by)
    {
      super.memo = new FeatureMatchingBy(by);
      return self();
    }

    /** See {@link FeatureMatchingBy#queryImgFeatures}. */
    public B queryImgFeatures(Integer queryImgFeatures)
    {
      ((FeatureMatchingBy) super.memo).queryImgFeatures = queryImgFeatures;
      return self();
    }

    /** See {@link FeatureMatchingBy#trainImgFeatures}. */
    public B trainImgFeatures(Integer trainImgFeatures)
    {
      ((FeatureMatchingBy) super.memo).trainImgFeatures = trainImgFeatures;
      return self();
    }

    /** See {@link FeatureMatchingBy#minNumOfMatches}. */
    public B minNumOfMatches(Integer minNumOfMatches)
    {
      ((FeatureMatchingBy) super.memo).minNumOfMatches = minNumOfMatches;
      return self();
    }

    /** See {@link FeatureMatchingBy#numOfMatchesToKeep}. */
    public B numOfMatchesToKeep(Integer numOfMatchesToKeep)
    {
      ((FeatureMatchingBy) super.memo).numOfMatchesToKeep = numOfMatchesToKeep;
      return self();
    }

    /** See {@link FeatureMatchingBy#scaleFactor}. */
    public B scaleFactor(Float scaleFactor)
    {
      ((FeatureMatchingBy) super.memo).scaleFactor = scaleFactor;
      return self();
    }

    /** See {@link FeatureMatchingBy#nlevels}. */
    public B nlevels(Integer nlevels)
    {
      ((FeatureMatchingBy) super.memo).nlevels = nlevels;
      return self();
    }

    /** See {@link FeatureMatchingBy#edgeThreshold}. */
    public B edgeThreshold(Integer edgeThreshold)
    {
      ((FeatureMatchingBy) super.memo).edgeThreshold = edgeThreshold;
      return self();
    }

    /** See {@link FeatureMatchingBy#firstLevel}. */
    public B firstLevel(Integer firstLevel)
    {
      ((FeatureMatchingBy) super.memo).firstLevel = firstLevel;
      return self();
    }

    /** See {@link FeatureMatchingBy#wtaK}. */
    public B wtaK(Integer wtaK)
    {
      ((FeatureMatchingBy) super.memo).wtaK = wtaK;
      return self();
    }

    /** See {@link FeatureMatchingBy#scoreType}. */
    public B scoreType(Integer scoreType)
    {
      ((FeatureMatchingBy) super.memo).scoreType = scoreType;
      return self();
    }

    /** See {@link FeatureMatchingBy#patchSize}. */
    public B patchSize(Integer patchSize)
    {
      ((FeatureMatchingBy) super.memo).patchSize = patchSize;
      return self();
    }

    /** See {@link FeatureMatchingBy#fastThreshold}. */
    public B fastThreshold(Integer fastThreshold)
    {
      ((FeatureMatchingBy) super.memo).fastThreshold = fastThreshold;
      return self();
    }

    /** See {@link FeatureMatchingBy#filters}. */
    public B filters(ImageFilter[] filters)
    {
      ((FeatureMatchingBy) super.memo).filters = filters;
      return self();
    }

    protected abstract B self();

    public abstract C build();

  }

  public static final class FeatureMatchingByBuilderImpl extends
      FeatureMatchingByBuilder<FeatureMatchingBy, FeatureMatchingByBuilderImpl>
  {

    protected FeatureMatchingByBuilderImpl()
    {
    }

    @Override
    protected FeatureMatchingByBuilderImpl self()
    {
      return this;
    }

    @Override
    public FeatureMatchingBy build()
    {
      return (FeatureMatchingBy) this.memo;
    }

  }

}
