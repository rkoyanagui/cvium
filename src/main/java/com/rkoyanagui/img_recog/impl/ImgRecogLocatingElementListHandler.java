package com.rkoyanagui.img_recog.impl;

import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogLocator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ImgRecogLocatingElementListHandler implements InvocationHandler
{

  protected final ImgRecogLocator locator;

  public ImgRecogLocatingElementListHandler(final ImgRecogLocator locator)
  {
    this.locator = locator;
  }

  @Override
  public Object invoke(final Object proxy,
                       final Method method,
                       final Object[] args) throws Throwable
  {
    final List<ImgRecogElement> elements = locator.findElements();

    try
    {
      return method.invoke(elements, args);
    }
    catch (InvocationTargetException x)
    {
      // Unwrap the underlying exception
      throw x.getCause();
    }
  }

}
