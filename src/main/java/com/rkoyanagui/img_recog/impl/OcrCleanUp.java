package com.rkoyanagui.img_recog.impl;

import java.util.function.Function;

/**
 * Represents functions to apply to OCR'ed text, before the test/predicate is applied, presumably to
 * clean the text up and remove unwanted or insignificant characters.
 */
public enum OcrCleanUp
{

  /** Returns the original text without modifications. */
  NONE(s -> s),
  /** See {@link String#trim}. */
  TRIM(s -> s.trim()),
  /** Uses {@link String#replaceAll} to remove all digits: {@code \d} or {@code [0-9]}. */
  REMOVE_ALL_DIGITS(s -> s.replaceAll("\\d", "")),
  /** Uses {@link String#replaceAll} to remove all non-digits: {@code \D} or {@code [^0-9]}. */
  REMOVE_ALL_NON_DIGITS(s -> s.replaceAll("\\D", "")),
  /**
   * Uses {@link String#replaceAll} to remove all whitespaces: {@code \s} or {@code [
   * \t\n\x0B\f\r]}.
   */
  REMOVE_ALL_SPACES(s -> s.replaceAll("\\s", "")),
  /**
   * Uses {@link String#replaceAll} to remove all word characters: {@code \w} or {@code
   * [a-zA-Z_0-9]}.
   */
  REMOVE_ALL_WORD_CHARS(s -> s.replaceAll("\\s", "")),
  /**
   * Uses {@link String#replaceAll} to remove all non-word characters: {@code \W} or {@code [^\w]}.
   */
  REMOVE_ALL_NON_WORD_CHARS(s -> s.replaceAll("\\s", ""));

  /** A function from an original {@link String} to a cleaned-up {@link String}. */
  public final Function<String, String> f;

  @SuppressWarnings("squid:S4276")
  OcrCleanUp(final Function<String, String> f)
  {
    this.f = f;
  }

}
