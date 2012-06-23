package com.github.jsr330.spi.config.builder;

import com.github.jsr330.spi.TypeConfig;

public class ConfigBuilder {
    
    protected InitialBinder<?> binder = new DefaultBinder<Object>();
    
    public TypeConfig build() {
        return binder.build();
    }
    
    public InitialBinder<?> get() {
        return binder;
    }
    
}
