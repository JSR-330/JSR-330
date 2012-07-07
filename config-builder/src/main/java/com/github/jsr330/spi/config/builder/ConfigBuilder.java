package com.github.jsr330.spi.config.builder;

import com.github.jsr330.spi.TypeConfig;

/**
 * The config builder is used to programmatically build a valid type configuration.
 */
public class ConfigBuilder {
    
    protected InitialBinder<?> binder = new DefaultBinder<Object>();
    
    /**
     * Gets the type config.
     */
    public TypeConfig build() {
        return binder.build();
    }
    
    /**
     * Gets the initial binder which is the entry point for the type configuration.
     */
    public InitialBinder<?> get() {
        return binder;
    }
    
}
