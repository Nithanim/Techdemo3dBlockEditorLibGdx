package com.mygdx.game.next.util;

import java.util.function.Supplier;

import com.badlogic.gdx.utils.Pool;

public class ModernPool<T> extends Pool<T> {
  private final Supplier<T> constructor;

  @Override
  protected T newObject() {
    return constructor.get();
  }

  public ModernPool(Supplier<T> constructor) {
    super();
    this.constructor = constructor;
  }

  public ModernPool(int initialCapacity, Supplier<T> constructor) {
    super(initialCapacity);
    this.constructor = constructor;
  }

  public ModernPool(int initialCapacity, int max, Supplier<T> constructor) {
    super(initialCapacity, max);
    this.constructor = constructor;
  }
}
