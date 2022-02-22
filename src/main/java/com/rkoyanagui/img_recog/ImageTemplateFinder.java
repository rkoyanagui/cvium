package com.rkoyanagui.img_recog;

import com.rkoyanagui.img_recog.ImgRecogBy.ImgRecogByBuilderFromAnnotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ImageTemplateFinder
{

  Class<? extends ImgRecogByBuilderFromAnnotation> value();

}
