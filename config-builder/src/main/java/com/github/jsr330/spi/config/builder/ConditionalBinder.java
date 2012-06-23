package com.github.jsr330.spi.config.builder;

public interface ConditionalBinder<T> extends Binder<T> {
    
    LinkingBinder<T> when(BindingCondition<T> condition);
    
}
