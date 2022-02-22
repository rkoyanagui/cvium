package com.rkoyanagui.img_recog.impl;

import java.util.function.Function;
import java.util.function.Predicate;

public enum OcrTest
{

  EQUALS(searchTerm -> s -> s.trim().equals(searchTerm)),
  EQUALS_IGNORE_CASE(searchTerm -> s -> s.trim().equalsIgnoreCase(searchTerm)),
  CONTAINS(searchTerm -> s -> s.contains(searchTerm)),
  CONTAINS_IGNORE_CASE(searchTerm -> s -> s.toLowerCase().contains(searchTerm.toLowerCase())),
  REGEX(searchTerm -> s -> s.matches(searchTerm));

  public final Function<String, Predicate<String>> predicateFactory;

  OcrTest(final Function<String, Predicate<String>> predicateFactory)
  {
    this.predicateFactory = predicateFactory;
  }

}
