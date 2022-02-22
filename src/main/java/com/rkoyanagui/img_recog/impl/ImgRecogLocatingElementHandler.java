package com.rkoyanagui.img_recog.impl;

import com.rkoyanagui.img_recog.ImgRecogElement;
import com.rkoyanagui.img_recog.ImgRecogLocator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openqa.selenium.NoSuchElementException;

public class ImgRecogLocatingElementHandler implements InvocationHandler
{

  protected final ImgRecogLocator locator;

  public ImgRecogLocatingElementHandler(final ImgRecogLocator locator)
  {
    this.locator = locator;
  }

  @Override
  public Object invoke(final Object proxy,
                       final Method method,
                       final Object[] args) throws Throwable
  {
    final ImgRecogElement element;
    try
    {
      element = locator.findElement();
    }
    catch (NoSuchElementException x)
    {
      if ("toString".equals(method.getName()))
      {
        return "Proxy element for: " + locator;
      }
      throw x;
    }

    try
    {
      return method.invoke(element, args);
    }
    catch (InvocationTargetException x)
    {
      // Unwrap the underlying exception
      throw x.getCause();
    }
  }

}
