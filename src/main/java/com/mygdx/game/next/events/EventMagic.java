package com.mygdx.game.next.events;

import java.util.function.Consumer;

import com.badlogic.gdx.utils.Array;

public class EventMagic<T> implements EventPublisher<T>, EventReceiver<T> {

  private final Array<Consumer<T>> listeners = new Array<>(false, 10);

  @Override
  public void addListener(Consumer<T> listener) {
    listeners.add(listener);
  }

  @Override
  public void fire(T event) {
    for (Consumer<T> c : listeners) {
      c.accept(event);
    }
  }
}
