package com.github.jsr330.spi.config.builder;

import javax.inject.Provider;

public interface TypeBinder<T> extends Binder<T> {
    
    InstancingBinder<T> asSingleton();
    
    InstancingBinder<T> asSingleton(Class<? extends T> type);
    
    InstancingBinder<T> as(Class<? extends T> type);
    
    ConditionalBinder<T> with(Provider<? extends T> provider);
    
}
