package com.github.jsr330.spi.config.builder;

public interface LinkingBinder<T> extends Binder<T> {
    
    LinkingBinder<T> and(BindingCondition<T> condition);
    
    LinkingBinder<T> or(BindingCondition<T> condition);
    
    LinkingBinder<T> xor(BindingCondition<T> condition);
    
}
