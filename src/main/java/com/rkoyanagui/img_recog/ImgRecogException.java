package com.rkoyanagui.img_recog;

public class ImgRecogException extends RuntimeException
{

  private static final long serialVersionUID = -6324823544663553537L;

  public ImgRecogException()
  {
    super();
  }

  public ImgRecogException(final String message)
  {
    super(message);
  }

  public ImgRecogException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ImgRecogException(final Throwable cause)
  {
    super(cause);
  }

  protected ImgRecogException(final String message,
                              final Throwable cause,
                              final boolean enableSuppression,
                              final boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
