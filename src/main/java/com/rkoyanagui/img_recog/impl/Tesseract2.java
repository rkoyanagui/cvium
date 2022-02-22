package com.rkoyanagui.img_recog.impl;

import static java.util.Objects.nonNull;

import com.rkoyanagui.img_recog.impl.ImgRecogConst.OcrMatching;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import net.sourceforge.tess4j.Tesseract1;
import org.apache.commons.lang3.reflect.FieldUtils;

public class Tesseract2 extends Tesseract1
{

  protected boolean hasBeenInit;

  /**
   * Initialises the Tesseract native API, pointing it to the language and data file of your choice.
   * Check language support at <a href="https://github.com/tesseract-ocr/tessdoc">tessdoc</a>.
   * Engine mode is set to LSTM neural net only. Page segmentation mode is set to 6 (single uniform
   * block of text). For choosing more than one language, use the <b>+</b> symbol, e.g.,
   * <i>por+eng</i>.
   *
   * @param language the language of trained data
   * @param datapath the path to the trained data file
   */
  public Tesseract2(final String language, final String datapath)
  {
    super();
    setLanguage(language);
    setDatapath(datapath);
    // Use only the LSTM neural net, as opposed to Legacy+LSTM.
    setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY);
    // PAGE SEGMENTATION MODE
    // 0    Orientation and script detection (OSD) only.
    // 1    Automatic page segmentation with OSD.
    // 2    Automatic page segmentation, but no OSD, or OCR.
    // 3    Fully automatic page segmentation, but no OSD. (Default)
    // 4    Assume a single column of text of variable sizes.
    // 5    Assume a single uniform block of vertically aligned text.
    // 6    Assume a single uniform block of text.
    // 7    Treat the image as a single text line.
    // 8    Treat the image as a single word.
    // 9    Treat the image as a single word in a circle.
    // 10    Treat the image as a single character.
    // 11    Sparse text. Find as much text as possible in no particular order.
    // 12    Sparse text with OSD.
    // 13    Raw line. Treat the image as a single text line,
    // bypassing hacks that are Tesseract-specific.
    setPageSegMode(OcrMatching.PAGE_SEGMENTATION_MODE);
    hasBeenInit = false;
    // Calling init() only once, as opposed to every time OCR is done, improves performance.
    init();
    setTessVariables();
    hasBeenInit = true;
  }

  @Override
  protected void init()
  {
    if (!hasBeenInit)
    {
      super.init();
    }
  }

  @Override
  protected void setTessVariables()
  {
    if (!hasBeenInit)
    {
      super.setTessVariables();
    }
  }

  // dispose() is called after every OCR action, in a try-finally block. It releases resources
  // associated with the native Tesseract API, including memory allocation. But, since init() is
  // not now called before every OCR action, calling dispose() and then doing OCR again would cause
  // an invalid memory access error. So, dispose() had to be overridden, to 'do nothing'.
  @Override
  protected void dispose()
  {
    // Do nothing
  }

  /**
   * Call this method once you're completely done and will not do any OCR anymore.
   */
  public void actuallyDispose()
  {
    super.dispose();
  }

  // Lots of PNG images don't have DPI metadata. That causes Leptonica (Tesseract's internal utility
  // for some image processing tasks) to log a warning every time such an image is read. That is why
  // this method is overridden. It now sets DPI to 70 if was found initially to be 0.
  @Override
  protected void setImage(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp)
  {
    super.setImage(xsize, ysize, buf, rect, bpp);
    TessBaseAPI handle = null;
    int res = 0;
    try
    {
      handle = (TessBaseAPI) FieldUtils.readField(this, "handle", true);
    }
    catch (IllegalAccessException e)
    {
      // fall through
    }
    if (nonNull(handle))
    {
      res = TessBaseAPIGetSourceYResolution(handle);
    }
    if (res < 70)
    {
      res = 70;
    }
    TessBaseAPISetSourceResolution(handle, res);
  }

}
