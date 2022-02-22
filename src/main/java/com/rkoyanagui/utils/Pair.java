package com.rkoyanagui.utils;

public class Pair<A, B>
{

  public final A a;
  public final B b;

  public Pair(final A a, final B b)
  {
    this.a = a;
    this.b = b;
  }

  public Pair<A, B> withA(final A a)
  {
    return new Pair<>(a, this.b);
  }

  public Pair<A, B> withB(final B b)
  {
    return new Pair<>(this.a, b);
  }

}
