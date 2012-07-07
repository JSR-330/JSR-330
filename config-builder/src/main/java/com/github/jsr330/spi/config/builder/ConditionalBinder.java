package com.github.jsr330.spi.config.builder;

/**
 * This binder is used to express a condition for the appliance of the binding configuration.
 */
public interface ConditionalBinder<T> extends Binder<T> {
    
    /**
     * when can be executed after an {@link InstancingBinder}.
     */
    LinkingBinder<T> when(BindingCondition<T> condition);
    
}
