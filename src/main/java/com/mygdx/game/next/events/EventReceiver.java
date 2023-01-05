package com.mygdx.game.next.events;

import java.util.function.Consumer;

public interface EventReceiver<T> {
  void addListener(Consumer<T> listener);
}
