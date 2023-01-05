package com.mygdx.game.next.events;

public interface EventPublisher<T> {
  void fire(T event);
}
