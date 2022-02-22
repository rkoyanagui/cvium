package com.rkoyanagui.img_recog;

public class EmptyMatException extends ImgRecogException
{

  private static final long serialVersionUID = 2689129047280873550L;
  protected static final String EMPTY_MAT = "Could not read (Mat null or empty) from '%s'";

  public EmptyMatException()
  {
  }

  public EmptyMatException(final String message)
  {
    super(message);
  }

  public EmptyMatException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public EmptyMatException(final Throwable cause)
  {
    super(cause);
  }

  public EmptyMatException(final String message,
                           final Throwable cause,
                           final boolean enableSuppression,
                           final boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static EmptyMatException cannotReadFrom(final String path)
  {
    return new EmptyMatException(String.format(EMPTY_MAT, path));
  }

}
