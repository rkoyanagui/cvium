package com.rkoyanagui.img_recog;

import static com.rkoyanagui.img_recog.impl.ImageFilter.ADAPTIVE_BINARY_THRESHOLD;
import static com.rkoyanagui.img_recog.impl.ImageFilter.GAUSSIAN_BLUR;
import static com.rkoyanagui.img_recog.impl.ImageFilter.GRAY;
import static com.rkoyanagui.img_recog.impl.ImageFilter.MORPH_DILATE;

import com.rkoyanagui.img_recog.ImgRecogBy.ImgRecogByBuilderFromAnnotation;
import com.rkoyanagui.img_recog.TemplateMatchingBy.TemplateMatchingByBuilder;
import com.rkoyanagui.img_recog.iOSImageTemplateFindBy.iOSImageTemplateFindByBuilder;
import com.rkoyanagui.img_recog.impl.ImageFilter;
import com.rkoyanagui.img_recog.impl.ImgRecogConst;
import com.rkoyanagui.utils.MobilePlatform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Locates an image in a mobile iOS device's screen, using the <i>template matching</i> method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ImageTemplateFinder(iOSImageTemplateFindByBuilder.class)
public @interface iOSImageTemplateFindBy
{

  /** See {@link ImgRecogBy#templateFilename}. */
  String filepath() default "";

  /** See {@link ImgRecogBy#order}. */
  int order() default 0;

  /** See {@link TemplateMatchingBy#matchThreshold}. */
  double matchThreshold() default ImgRecogConst.TemplateMatching.MATCH_THRESHOLD;

  /** See {@link TemplateMatchingBy#resizeFactor}. */
  double resizeFactor() default ImgRecogConst.TemplateMatching.RESIZE_FACTOR;

  /** See {@link TemplateMatchingBy#resizeMaxAttempts}. */
  int resizeMaxAttempts() default ImgRecogConst.TemplateMatching.RESIZE_MAX_ATTEMPTS;

  /** See {@link TemplateMatchingBy#preResizingFilters}. */
  ImageFilter[] preResizingFilters() default {GRAY, GAUSSIAN_BLUR, ADAPTIVE_BINARY_THRESHOLD};

  /** See {@link TemplateMatchingBy#postResizingFilters}. */
  ImageFilter[] postResizingFilters() default {MORPH_DILATE};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#offset}. */
  float[] offset() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  /** An array in the order {@code x, y, width, height}. See {@link ImgRecogBy#cutout}. */
  float[] cutout() default {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

  @SuppressWarnings("squid:S101")
  class iOSImageTemplateFindByBuilder extends
      TemplateMatchingByBuilder<TemplateMatchingBy, iOSImageTemplateFindByBuilder> implements
      ImgRecogByBuilderFromAnnotation<TemplateMatchingBy>
  {

    public iOSImageTemplateFindByBuilder()
    {
      super();
    }

    @Override
    protected iOSImageTemplateFindByBuilder self()
    {
      return this;
    }

    @Override
    public TemplateMatchingBy build()
    {
      return new TemplateMatchingBy((TemplateMatchingBy) this.memo);
    }

    @Override
    public TemplateMatchingBy build(final Object annotation, final Field field)
    {
      final iOSImageTemplateFindBy ann = (iOSImageTemplateFindBy) annotation;
      return this.templateFilename(ann.filepath())
          .order(ann.order())
          .platform(MobilePlatform.IOS.description)
          .offset(ann.offset()[0], ann.offset()[1], ann.offset()[2], ann.offset()[3])
          .cutout(ann.cutout()[0], ann.cutout()[1], ann.cutout()[2], ann.cutout()[3])
          .matchThreshold(ann.matchThreshold())
          .resizeFactor(ann.resizeFactor())
          .resizeMaxAttempts(ann.resizeMaxAttempts())
          .preResizingFilters(ann.preResizingFilters())
          .postResizingFilters(ann.postResizingFilters())
          .build();
    }

  }

}
