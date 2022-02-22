package com.rkoyanagui.img_recog;

import com.rkoyanagui.img_recog.ImgRecogBy.ImgRecogByBuilderFromAnnotation;
import com.rkoyanagui.img_recog.OcrMatchingBy.OcrMatchingByBuilder;
import com.rkoyanagui.img_recog.iOSOcrFindBy.iOSOcrFindByBuilder;
import com.rkoyanagui.img_recog.impl.ImageFilter;
import com.rkoyanagui.img_recog.impl.ImgRecogConst.OcrMatching;
import com.rkoyanagui.img_recog.impl.OcrCleanUp;
import com.rkoyanagui.img_recog.impl.OcrTest;
import com.rkoyanagui.utils.MobilePlatform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Locates an element in a mobile iOS device's screen, using the <i>OCR (Optical Character
 * Recognition)</i> method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ImageTemplateFinder(iOSOcrFindByBuilder.class)
public @interface iOSOcrFindBy
{

  /** See {@link OcrMatchingBy#searchTerm}. */
  String searchTerm() default "";

  /** See {@link ImgRecogBy#order}. */
  int order() default 0;

  /** See {@link OcrMatchingBy#minScore}. */
  float minScore() default OcrMatching.MIN_SCORE;

  /** See {@link OcrMatchingBy#ocrTest}. */
  OcrTest test() default OcrTest.EQUALS_IGNORE_CASE;

  /** See {@link OcrMatchingBy#nms}. */
  float nms() default OcrMatching.NMS_THRESHOLD;

  /** See {@link OcrMatchingBy#iou}. */
  float iou() default OcrMatching.IOU_THRESHOLD;

  /** An array in the order {@code left, right, top, bottom}. See {@link OcrMatchingBy#padding}. */
  double[] pad() default {OcrMatching.LEFT_PADDING, OcrMatching.RIGHT_PADDING,
      OcrMatching.TOP_PADDING, OcrMatching.BOTTOM_PADDING};

  /** See {@link OcrMatchingBy#filters}. */
  ImageFilter[] filters() default {};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#offset}. */
  float[] offset() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#cutout}. */
  float[] cutout() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  /** See {@link OcrMatchingBy#cleanUp} */
  OcrCleanUp[] cleanUp() default OcrCleanUp.TRIM;

  @SuppressWarnings("squid:S101")
  class iOSOcrFindByBuilder extends
      OcrMatchingByBuilder<OcrMatchingBy, iOSOcrFindByBuilder> implements
      ImgRecogByBuilderFromAnnotation<OcrMatchingBy>
  {

    public iOSOcrFindByBuilder()
    {
      super();
    }

    @Override
    protected iOSOcrFindByBuilder self()
    {
      return this;
    }

    @Override
    public OcrMatchingBy build()
    {
      return (OcrMatchingBy) this.memo;
    }

    @Override
    public OcrMatchingBy build(final Object annotation, final Field field)
    {
      final iOSOcrFindBy ann = (iOSOcrFindBy) annotation;
      return this.order(ann.order())
          .platform(MobilePlatform.IOS.description)
          .offset(ann.offset()[0], ann.offset()[1], ann.offset()[2], ann.offset()[3])
          .cutout(ann.cutout()[0], ann.cutout()[1], ann.cutout()[2], ann.cutout()[3])
          .searchTerm(ann.searchTerm())
          .minScore(ann.minScore())
          .ocrTest(ann.test())
          .nms(ann.nms())
          .iou(ann.iou())
          .padding(new Padding(ann.pad()[0], ann.pad()[1], ann.pad()[2], ann.pad()[3]))
          .cleanUp(ann.cleanUp())
          .filters(ann.filters())
          .build();
    }

  }

}
