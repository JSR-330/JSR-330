package com.github.jsr330.spi.config.builder;

import com.github.jsr330.spi.TypeConfig;

public interface Binder<T> {
    
    TypeConfig build();
    
}
