package com.github.jsr330.spi.config.builder;

/**
 * The linking binder performs bitwise and, or or xor combinations of the pre-defined condition.
 */
public interface LinkingBinder<T> extends Binder<T> {
    
    LinkingBinder<T> and(BindingCondition<T> condition);
    
    LinkingBinder<T> or(BindingCondition<T> condition);
    
    LinkingBinder<T> xor(BindingCondition<T> condition);
    
}
