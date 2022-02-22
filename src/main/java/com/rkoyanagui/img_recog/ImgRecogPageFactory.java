package com.rkoyanagui.img_recog;

import com.google.common.collect.ImmutableList;
import com.rkoyanagui.img_recog.impl.DefaultImgRecogLocator;
import com.rkoyanagui.img_recog.impl.ImgRecogLocatingElementHandler;
import com.rkoyanagui.img_recog.impl.ImgRecogLocatingElementListHandler;
import com.rkoyanagui.img_recog.impl.ImgRecogMobileElement;
import com.rkoyanagui.img_recog.impl.ImgRecogWebElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.WebDriver;

public class ImgRecogPageFactory
{

  protected static final String ERROR_MSG = "Could not access fields to initialise image recognition elements.";
  protected static final List<Class<? extends ImgRecogElement>> AVAILABLE_ELEMENT_CLASSES =
      ImmutableList.of(
          ImgRecogElement.class,
          ImgRecogMobileElement.class,
          ImgRecogWebElement.class);
  protected static final List<Class<? extends Annotation>> AVAILABLE_LOCATOR_CLASSES =
      ImmutableList.of(
          AndroidImageFeatureFindBy.class,
          AndroidImageTemplateFindBy.class,
          AndroidOcrFindBy.class,
          iOSImageFeatureFindBy.class,
          iOSImageTemplateFindBy.class,
          iOSOcrFindBy.class);

  protected ImgRecogPageFactory()
  {
  }

  public static void initElements(final ImgRecogEngine imgRecogEngine,
                                  final WebDriver driver,
                                  final Object page)
  {
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class)
    {
      proxyFields(imgRecogEngine, driver, page, proxyIn);
      proxyIn = proxyIn.getSuperclass();
    }
  }

  @SuppressWarnings("squid:S3011")
  protected static void proxyFields(final ImgRecogEngine imgRecogEngine,
                                    final WebDriver driver,
                                    final Object page,
                                    final Class<?> proxyIn)
  {
    final Field[] fields = proxyIn.getDeclaredFields();
    for (Field field : fields)
    {
      final Object value = decorate(imgRecogEngine, driver, page.getClass().getClassLoader(),
          field);
      if (value != null)
      {
        try
        {
          field.setAccessible(true);
          field.set(page, value);
        }
        catch (IllegalAccessException x)
        {
          throw new ImgRecogException(ERROR_MSG, x);
        }
      }
    }
  }

  protected static Object decorate(final ImgRecogEngine imgRecogEngine,
                                   final WebDriver driver,
                                   final ClassLoader loader,
                                   final Field field)
  {
    // If the field is not of the correct type, then short-circuit the operation.
    if (!(ImgRecogElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field)))
    {
      return null;
    }
    // If the field does not contain at least one of the locator annotations, then short it.
    for (final Annotation ann : field.getDeclaredAnnotations())
    {
      if (AVAILABLE_LOCATOR_CLASSES.stream().noneMatch(a -> ann.annotationType().equals(a)))
      {
        return null;
      }
    }
    if (ImgRecogElement.class.isAssignableFrom(field.getType()))
    {
      // If the field's type subclasses ImgRecogElement, then...
      return proxyForLocator(loader, new DefaultImgRecogLocator(field, imgRecogEngine, driver));
    }
    else if (List.class.isAssignableFrom(field.getType()))
    {
      // Else if the field is a list, then...
      return proxyForListLocator(loader, new DefaultImgRecogLocator(field, imgRecogEngine, driver));
    }
    else
    {
      return null;
    }
  }

  protected static ImgRecogElement proxyForLocator(final ClassLoader loader,
                                                   final ImgRecogLocator locator)
  {
    final InvocationHandler handler = new ImgRecogLocatingElementHandler(locator);
    return (ImgRecogElement) Proxy.newProxyInstance(
        loader, new Class[]{ImgRecogElement.class}, handler);
  }

  @SuppressWarnings("unchecked")
  protected static List<ImgRecogElement> proxyForListLocator(final ClassLoader loader,
                                                             final ImgRecogLocator locator)
  {
    final InvocationHandler handler = new ImgRecogLocatingElementListHandler(locator);
    return (List<ImgRecogElement>) Proxy.newProxyInstance(
        loader, new Class[]{List.class}, handler);
  }

  protected static boolean isDecoratableList(final Field field)
  {
    if (!List.class.isAssignableFrom(field.getType()))
    {
      return false;
    }

    // Type erasure in Java isn't complete. Attempt to discover the generic
    // type of the list.
    final Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType))
    {
      return false;
    }

    final Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
    final List<Type> bounds = (listType instanceof TypeVariable)
        ? Arrays.asList(((TypeVariable<?>) listType).getBounds())
        : Collections.emptyList();

    return AVAILABLE_ELEMENT_CLASSES.stream()
        .anyMatch(e -> e.equals(listType) || bounds.contains(e));
  }

}
