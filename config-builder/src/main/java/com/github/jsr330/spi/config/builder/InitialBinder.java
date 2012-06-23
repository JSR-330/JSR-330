package com.github.jsr330.spi.config.builder;

public interface InitialBinder<T> extends Binder<T> {
    
    <V> TypeBinder<V> instance(Class<V> type);
    
}
