package com.rkoyanagui.img_recog;

import static com.rkoyanagui.img_recog.impl.ImageFilter.GRAY;

import com.rkoyanagui.img_recog.FeatureMatchingBy.FeatureMatchingByBuilder;
import com.rkoyanagui.img_recog.ImgRecogBy.ImgRecogByBuilderFromAnnotation;
import com.rkoyanagui.img_recog.iOSImageFeatureFindBy.iOSImageFeatureFindByBuilder;
import com.rkoyanagui.img_recog.impl.ImageFilter;
import com.rkoyanagui.img_recog.impl.ImgRecogConst.FeatureMatching;
import com.rkoyanagui.utils.MobilePlatform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Locates an image in a mobile iOS device's screen, using the <i>feature matching</i> method.
 * Please do not apply to fields of the type {@code Collection<Element> }! The feature matching
 * image recognition method cannot be used to find lists of elements. It can only be used to find
 * single elements.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ImageTemplateFinder(iOSImageFeatureFindByBuilder.class)
public @interface iOSImageFeatureFindBy
{

  /** See {@link ImgRecogBy#templateFilename}. */
  String filepath() default "";

  /** See {@link ImgRecogBy#order}. */
  int order() default 0;

  /** See {@link FeatureMatchingBy#queryImgFeatures}. */
  int queryImgFeatures() default FeatureMatching.NUM_OF_QUERY_FEATURES;

  /** See {@link FeatureMatchingBy#trainImgFeatures}. */
  int trainImgFeatures() default FeatureMatching.NUM_OF_TRAIN_FEATURES;

  /** See {@link FeatureMatchingBy#minNumOfMatches}. */
  int minNumOfMatches() default FeatureMatching.MIN_NUM_OF_MATCHES;

  /** See {@link FeatureMatchingBy#numOfMatchesToKeep}. */
  int numOfMatchesToKeep() default FeatureMatching.NUM_OF_MATCHES_TO_KEEP;

  /** See {@link FeatureMatchingBy#scaleFactor}. */
  float scaleFactor() default FeatureMatching.SCALE_FACTOR;

  /** See {@link FeatureMatchingBy#nlevels}. */
  int nlevels() default FeatureMatching.N_LEVELS;

  /** See {@link FeatureMatchingBy#edgeThreshold}. */
  int edgeThreshold() default FeatureMatching.EDGE_THRESHOLD;

  /** See {@link FeatureMatchingBy#firstLevel}. */
  int firstLevel() default FeatureMatching.FIRST_LEVEL;

  /** See {@link FeatureMatchingBy#wtaK}. */
  int wtaK() default FeatureMatching.WTA_K;

  /** See {@link FeatureMatchingBy#scoreType}. */
  int scoreType() default FeatureMatching.SCORE_TYPE;

  /** See {@link FeatureMatchingBy#patchSize}. */
  int patchSize() default FeatureMatching.PATCH_SIZE;

  /** See {@link FeatureMatchingBy#fastThreshold}. */
  int fastThreshold() default FeatureMatching.FAST_THRESHOLD;

  /** See {@link FeatureMatchingBy#filters}. */
  ImageFilter[] filters() default {GRAY};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#offset}. */
  float[] offset() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#cutout}. */
  float[] cutout() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  @SuppressWarnings("squid:S101")
  class iOSImageFeatureFindByBuilder extends
      FeatureMatchingByBuilder<FeatureMatchingBy, iOSImageFeatureFindByBuilder> implements
      ImgRecogByBuilderFromAnnotation<FeatureMatchingBy>
  {

    public iOSImageFeatureFindByBuilder()
    {
      super();
    }

    @Override
    protected iOSImageFeatureFindByBuilder self()
    {
      return this;
    }

    @Override
    public FeatureMatchingBy build()
    {
      return (FeatureMatchingBy) this.memo;
    }

    @Override
    public FeatureMatchingBy build(final Object annotation, final Field field)
    {
      final iOSImageFeatureFindBy ann = (iOSImageFeatureFindBy) annotation;
      return this.templateFilename(ann.filepath())
          .order(ann.order())
          .platform(MobilePlatform.IOS.description)
          .offset(ann.offset()[0], ann.offset()[1], ann.offset()[2], ann.offset()[3])
          .cutout(ann.cutout()[0], ann.cutout()[1], ann.cutout()[2], ann.cutout()[3])
          .queryImgFeatures(ann.queryImgFeatures())
          .trainImgFeatures(ann.trainImgFeatures())
          .minNumOfMatches(ann.minNumOfMatches())
          .numOfMatchesToKeep(ann.numOfMatchesToKeep())
          .scaleFactor(ann.scaleFactor())
          .nlevels(ann.nlevels())
          .edgeThreshold(ann.edgeThreshold())
          .firstLevel(ann.firstLevel())
          .wtaK(ann.wtaK())
          .scoreType(ann.scoreType())
          .patchSize(ann.patchSize())
          .fastThreshold(ann.fastThreshold())
          .filters(ann.filters())
          .build();
    }

  }

}
