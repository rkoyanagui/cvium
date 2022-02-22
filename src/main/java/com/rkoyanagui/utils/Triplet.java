package com.rkoyanagui.utils;

public class Triplet<A, B, C>
{

  public final A a;
  public final B b;
  public final C c;

  public Triplet(final A a, final B b, final C c)
  {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public Triplet<A, B, C> withA(final A a)
  {
    return new Triplet<>(a, this.b, this.c);
  }

  public Triplet<A, B, C> withB(final B b)
  {
    return new Triplet<>(this.a, b, this.c);
  }

  public Triplet<A, B, C> withC(final C c)
  {
    return new Triplet<>(this.a, this.b, c);
  }

}
