package org.example;

@FunctionalInterface
public interface Handler<T> {

  void handle(T t);
}
