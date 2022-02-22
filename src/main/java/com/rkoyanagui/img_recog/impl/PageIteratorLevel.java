package com.rkoyanagui.img_recog.impl;

import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;

public enum PageIteratorLevel
{
  /** Block of text/image/separator line. */
  BLOCK(TessPageIteratorLevel.RIL_BLOCK),
  /** Paragraph within a block. */
  PARA(TessPageIteratorLevel.RIL_PARA),
  /** Line within a paragraph. */
  LINE(TessPageIteratorLevel.RIL_TEXTLINE),
  /** Word within a textline. */
  WORD(TessPageIteratorLevel.RIL_WORD),
  /** Symbol/character within a word. */
  SYMBOL(TessPageIteratorLevel.RIL_SYMBOL);

  public final int level;

  PageIteratorLevel(final int level)
  {
    this.level = level;
  }
}
